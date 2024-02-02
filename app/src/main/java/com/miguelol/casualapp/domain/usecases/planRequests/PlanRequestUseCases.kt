package com.miguelol.casualapp.domain.usecases.planRequests

data class PlanRequestUseCases(
    val getRequests: GetPlanRequests,
    val createRequest: CreatePlanRequest,
    val declineRequest: DeclinePlanRequest,
    val acceptRequest: AcceptPlanRequest,
    val getRequestState: GetRequestState
)


