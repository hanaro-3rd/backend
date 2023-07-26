package com.example.travelhana.Controller;

import com.example.travelhana.Dto.PlanDto;
import com.example.travelhana.Dto.PlanSuccessDto;
import com.example.travelhana.Service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController //api
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;


    @PostMapping("/travelbudget")
    public PlanSuccessDto createPlan(@RequestBody PlanDto planDto) {
        return planService.savePlan(planDto);
    }

    @GetMapping(value = "/travelbudget/{id}")
    public void getTravelBudget(@PathVariable Long id) {
        //카테고리별 {id:1, balance:}
        //{
        //   id:
        //   title:
        //   startDate:
        //   endDate:
        //   country:
        //   city:
        //  category:[
        //   {
        //      id:1, (식비)
        //      history:
        //         [ {id,balnace,time},{id,balnce,time}…]
        //    },
        //     id:2, (쇼핑)
        //      history:
        //         [ {id,balnace,time},{id,balnce,time}…]
        //    …
        //   ]
        //
        //}
    }
}
