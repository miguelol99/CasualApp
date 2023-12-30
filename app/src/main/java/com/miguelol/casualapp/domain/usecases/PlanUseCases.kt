package com.miguelol.casualapp.domain.usecases.plans

import android.net.Uri
import android.util.Log
import com.miguelol.casualapp.domain.model.PlanPreview
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.repositories.ImageRepository
import com.miguelol.casualapp.domain.repositories.PlanRepository
import com.miguelol.casualapp.domain.repositories.PlansPreviewResponse
import com.miguelol.casualapp.utils.Constants.PRIVATE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

data class PlanUseCases(
    val getAllPlans: GetAllPlans,
    val getMyPlans: GetMyPlans,
    val filterPlans: FilterPlans,
    val createPlan: CreatePlan,
)

class GetAllPlans @Inject constructor(private val planRepository: PlanRepository) {
    operator fun invoke(uid: String): Flow<PlansPreviewResponse> {
        return planRepository.getVisiblePlans(uid).map { response ->
            when (response) {
                is Response.Success -> {
                    val filtered = response.data.filter { planDetail ->
                        !planDetail.participants.contains(uid)
                    }
                    Response.Success(filtered)
                }
                else -> response
            }
        }
    }
}

class GetMyPlans @Inject constructor(private val planRepository: PlanRepository) {
    operator fun invoke(uid: String): Flow<Response<List<PlanPreview>>> {
        return planRepository.getUserPlans(uid)
    }
}

class FilterPlans @Inject constructor() {
    operator fun invoke(plans: List<PlanPreview>?, type: String): List<PlanPreview>? {
        if (type != PRIVATE)
            return plans

        return plans?.filter { it.type == type }
    }
}

class CreatePlan @Inject constructor(
    private val planRepository: PlanRepository,
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(plan: PlanPreview, uid: String): Response<Boolean> {

        if (plan.image.isNotBlank()) {
            val uri = Uri.parse(plan.image)
            when (val response = imageRepository.saveImage(uri, UUID.randomUUID().toString())) {
                is Response.Error -> return response
                is Response.Success -> {
                    val downloadUri = response.data
                    plan.image = downloadUri.toString()
                }
            }
        }

        return planRepository.createPlan(plan)
    }
}
