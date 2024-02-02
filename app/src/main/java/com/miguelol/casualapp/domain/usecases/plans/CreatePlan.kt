package com.miguelol.casualapp.domain.usecases.plans

import androidx.core.net.toUri
import com.miguelol.casualapp.domain.model.Plan
import com.miguelol.casualapp.domain.model.PlanType
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Response.Success
import com.miguelol.casualapp.domain.model.Response.Error
import com.miguelol.casualapp.domain.repositories.PlanRepository
import com.miguelol.casualapp.domain.usecases.friends.FriendUseCases
import com.miguelol.casualapp.domain.usecases.images.ImageUseCases
import com.miguelol.casualapp.domain.usecases.users.UserUseCases
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

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