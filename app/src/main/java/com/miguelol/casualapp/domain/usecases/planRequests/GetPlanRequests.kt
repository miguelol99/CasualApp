package com.miguelol.casualapp.domain.usecases.planRequests

import com.miguelol.casualapp.domain.model.PlanRequest
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.repositories.PlanRequestRepository
import com.miguelol.casualapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPlanRequests @Inject constructor(private val requestRepo: PlanRequestRepository) {
    operator fun invoke(uid: String): Flow<Response<List<PlanRequest>>> =
        requestRepo.getPlanRequests(uid).map { resp ->
            when(resp){
                is Error -> Error(Exception(Constants.DATABASE_ERROR))
                is Success -> resp
            }
        }
}