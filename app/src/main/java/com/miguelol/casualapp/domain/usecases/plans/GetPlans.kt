package com.miguelol.casualapp.domain.usecases.plans

import com.miguelol.casualapp.domain.model.Plan
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Response.Success
import com.miguelol.casualapp.domain.model.Response.Error
import com.miguelol.casualapp.domain.repositories.PlanRepository
import com.miguelol.casualapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

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
                is Error -> Error(Exception(Constants.DATABASE_ERROR))
            }
        }
    }
}