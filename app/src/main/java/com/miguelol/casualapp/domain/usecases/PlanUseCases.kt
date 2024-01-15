package com.miguelol.casualapp.domain.usecases.plans

import androidx.core.net.toUri
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.Plan
import com.miguelol.casualapp.domain.model.PlanType
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.repositories.PlanRepository
import com.miguelol.casualapp.domain.usecases.FriendUseCases
import com.miguelol.casualapp.domain.usecases.UserUseCases
import com.miguelol.casualapp.domain.usecases.images.ImageUseCases
import com.miguelol.casualapp.utils.Constants.DATABASE_ERROR
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

data class PlanUseCases(
    val getPlans: GetPlans,
    val getMyPlans: GetMyPlans,
    val filterPlans: FilterPlans,
    val createPlan: CreatePlan,
)

class GetPlans @Inject constructor(private val planRepository: PlanRepository) {
    operator fun invoke(uid: String): Flow<Response<List<Plan>>> {
        return planRepository.getPublicPlans(uid).map { resp ->
            when (resp) {
                is Success -> Success(resp.data.filter { it.host.uid != uid })
                is Error -> Error(Exception(DATABASE_ERROR))
            }
        }
    }
}

class GetMyPlans @Inject constructor(private val planRepository: PlanRepository) {
    operator fun invoke(uid: String): Flow<Response<List<Plan>>> =
        planRepository.getPlansCreatedBy(uid).map { resp ->
            when (resp) {
                is Error -> Error(Exception(DATABASE_ERROR))
                is Success -> resp
            }
        }
}

class FilterPlans @Inject constructor() {
    operator fun invoke(plans: List<Plan>, type: String): List<Plan> {
        //if (type != PRIVATE)
        return plans

        //return plans.filter { it.type == type }
    }
}

class CreatePlan @Inject constructor(
    private val planRepository: PlanRepository,
    private val userUseCases: UserUseCases,
    private val friendUseCases: FriendUseCases,
    private val imageUseCases: ImageUseCases,
) {
    suspend operator fun invoke(plan: Plan, uid: String): Response<Boolean> {

        when (val resp = userUseCases.getUser(uid).first()) {
            is Error -> return resp
            is Success -> {
                plan.host.uid = uid
                plan.host.username = resp.data.username
                plan.participants = listOf(uid)
            }
        }

        if (plan.type == PlanType.PRIVATE) {
            when(val resp = friendUseCases.getFriends(uid).first()) {
                is Error -> return resp
                is Success -> plan.friendsOfHost = resp.data.map { it.uid }
            }
        }

        if (plan.image.isNotBlank()) {
            when (val resp =
                imageUseCases.saveImage(plan.image.toUri(), UUID.randomUUID().toString())) {
                is Error -> return resp
                is Success -> plan.image = resp.data.toString()
            }
        }

        return planRepository.createPlan(plan)
    }
}

