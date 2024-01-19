package com.miguelol.casualapp.domain.usecases.plans

import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.Timestamp
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.Message
import com.miguelol.casualapp.domain.model.Plan
import com.miguelol.casualapp.domain.model.PlanType
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.domain.repositories.PlanRepository
import com.miguelol.casualapp.domain.usecases.FriendUseCases
import com.miguelol.casualapp.domain.usecases.UserUseCases
import com.miguelol.casualapp.domain.usecases.images.ImageUseCases
import com.miguelol.casualapp.presentation.screens.plans.FilterType
import com.miguelol.casualapp.utils.Constants.DATABASE_ERROR
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

data class PlanUseCases(
    val getPlans: GetPlans,
    val getPlan: GetPlan,
    val getParticipants: GetParticipants,
    val getMyPlans: GetMyPlans,
    val filterPlans: FilterPlans,
    val createPlan: CreatePlan,
    val deletePlan: DeletePlan,
    val addParticipant: AddParticipant,
    val deleteParticipant: DeleteParticipant,
    val getChat: GetChat,
    val sendMessage: SendMessage
)

class GetPlans @Inject constructor(private val planRepository: PlanRepository) {
    operator fun invoke(uid: String): Flow<Response<List<Plan>>> {
        return planRepository.getVisiblePlans(uid).map { resp ->
            when (resp) {
                is Success -> {
                    val plans = resp.data
                    val filtered =
                        plans.filter { (it.host.uid != uid) && (!it.participants.contains(uid)) }
                    Success(filtered)
                }
                is Error -> Error(Exception(DATABASE_ERROR))
            }
        }
    }
}

class GetPlan @Inject constructor(private val planRepository: PlanRepository) {
    operator fun invoke(planId: String): Flow<Response<Plan?>> {
        return planRepository.getPlan(planId).map { resp ->
            when (resp) {
                is Success ->  Success(resp.data)
                is Error -> Error(Exception(DATABASE_ERROR))
            }
        }
    }
}

class GetParticipants @Inject constructor(private val planRepository: PlanRepository) {
    operator fun invoke(planId: String): Flow<Response<List<UserPreview>>> {
        return planRepository.getParticipants(planId).map { resp ->
            when (resp) {
                is Success -> resp
                is Error -> Error(Exception(DATABASE_ERROR))
            }
        }
    }
}

class GetMyPlans @Inject constructor(private val planRepository: PlanRepository) {
    operator fun invoke(uid: String): Flow<Response<List<Plan>>> =
        planRepository.getMyPlans(uid).map { resp ->
            when (resp) {
                is Error -> Error(Exception(DATABASE_ERROR))
                is Success -> resp
            }
        }
}

class FilterPlans @Inject constructor() {
    operator fun invoke(plans: List<Plan>, type: FilterType): List<Plan> {
        return when (type) {
            FilterType.PUBLIC -> plans.filter { it.type == PlanType.PUBLIC }
            FilterType.PRIVATE -> plans.filter { it.type == PlanType.PRIVATE }
            FilterType.ALL -> return plans
        }
    }
}

class CreatePlan @Inject constructor(
    private val planRepository: PlanRepository,
    private val userUseCases: UserUseCases,
    private val friendUseCases: FriendUseCases,
    private val imageUseCases: ImageUseCases,
) {
    suspend operator fun invoke(plan: Plan, myUid: String): Response<Boolean> {

        when (val resp = userUseCases.getUser(myUid).first()) {
            is Error -> return resp
            is Success -> {
                plan.host = resp.data.toPreview()
                plan.participants = listOf(myUid)
            }
        }

        if (plan.type == PlanType.PRIVATE) {
            when (val resp = friendUseCases.getFriends(myUid).first()) {
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

class DeletePlan @Inject constructor(
    private val planRepository: PlanRepository,
) {
    suspend operator fun invoke(planId: String): Response<Unit> {
        return when(val resp = planRepository.deletePlan(planId)) {
            is Error -> Error(Exception(DATABASE_ERROR))
            is Success -> resp
        }
    }
}

class DeleteParticipant @Inject constructor(
    private val planRepository: PlanRepository
) {
    suspend operator fun invoke(planId: String, uid: String): Response<Unit> =
        when (val resp = planRepository.deleteParticipant(planId, uid)) {
            is Error -> Error(Exception(DATABASE_ERROR))
            is Success -> resp
        }
}

class AddParticipant @Inject constructor(
    private val planRepository: PlanRepository,
    private val userUseCases: UserUseCases
) {
    suspend operator fun invoke(planId: String, uid: String): Response<Unit> {

        val user: UserPreview
        when (val resp = userUseCases.getUser(uid).first()) {
            is Error -> return resp
            is Success -> user = resp.data.toPreview()
        }

        return when (val resp = planRepository.addParticipant(planId = planId, user = user)) {
            is Error -> Error(Exception(DATABASE_ERROR))
            is Success -> resp
        }

    }
}

class GetChat @Inject constructor(
    private val planRepository: PlanRepository,
) {
    operator fun invoke(planId: String): Flow<Response<List<Message>>> {
        return planRepository.getChat(planId).map { resp ->
            when(resp) {
                is Error -> Error(Exception(DATABASE_ERROR))
                is Success -> resp
            }
        }
    }
}

class SendMessage @Inject constructor(
    private val planRepository: PlanRepository,
    private val userUseCases: UserUseCases
) {
    suspend operator fun invoke(planId: String, text: String, fromUid: String): Response<Unit> {

        var message = Message(
            timestamp = Timestamp.now(),
            message = text
        )

        when (val resp = userUseCases.getUser(fromUid).first()) {
            is Error -> return resp
            is Success -> message = message.copy(fromUser = resp.data.toPreview())
        }

        return when(val resp = planRepository.sendMessage(planId, message)) {
            is Error -> Error(Exception(DATABASE_ERROR))
            is Success -> resp
        }
    }
}


















