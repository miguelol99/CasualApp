package com.miguelol.casualapp.domain.usecases.plans

import com.miguelol.casualapp.domain.model.Plan
import com.miguelol.casualapp.domain.model.PlanType
import com.miguelol.casualapp.presentation.screens.plans.FilterType
import javax.inject.Inject

class FilterPlans @Inject constructor() {
    operator fun invoke(plans: List<Plan>, type: FilterType): List<Plan> {
        return when (type) {
            FilterType.PUBLIC -> plans.filter { it.type == PlanType.PUBLIC }
            FilterType.PRIVATE -> plans.filter { it.type == PlanType.PRIVATE }
            FilterType.ALL -> return plans
        }
    }
}