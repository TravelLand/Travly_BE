package com.travelland.swagger;

import com.travelland.dto.plan.DayPlanDto;
import com.travelland.dto.plan.PlanCommentDto;
import com.travelland.dto.plan.PlanDto;
import com.travelland.dto.plan.UnitPlanDto;
import com.travelland.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "여행 플랜 API", description = "여행 전 플랜 관련 API")
public interface PlanControllerDocs {//

    @Operation(summary = "Plan 작성", description = "시간은 yyyy-MM-dd 포맷")
    ResponseEntity createPlan(@RequestBody PlanDto.Create request);

    @Operation(summary = "Plan 한방 작성", description = "Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재\n Plan/DayPlan의 시간은 yyyy-MM-dd 포맷, UnitPlan은 HH:mm 포맷")
    ResponseEntity createPlanAllInOne(@RequestBody PlanDto.CreateAllInOne request);

    @Operation(summary = "Plan 상세/단일 조회", description = "상세조회, planId로 조회")
    ResponseEntity readPlan(@PathVariable Long planId);

    @Operation(summary = "(마이페이지용) Plan 유저별 상세/단일 조회", description = "상세조회, planId로 조회")
    ResponseEntity readPlanForMember(@PathVariable Long planId);

    @Operation(summary = "Plan 한방 상세/단일 조회", description = "Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재")
    ResponseEntity readPlanAllInOne(@PathVariable Long planId);

    @Operation(summary = "(마이페이지용) Plan 유저별 한방 상세/단일 조회", description = "Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재")
    ResponseEntity readPlanAllInOneForMember(@PathVariable Long planId);

    @Operation(summary = "Plan 전체목록 조회", description = "예시: /plans?page=1&size=20&sortBy=createdAt&isAsc=false, page는 1부터")
    ResponseEntity readPlanList(@RequestParam int page, @RequestParam int size, @RequestParam String sortBy, @RequestParam boolean isAsc);

    @Operation(summary = "(마이페이지용) Plan 유저별 전체목록 조회", description = "예시: /plans?page=1&size=20&sortBy=createdAt&isAsc=false, page는 1부터")
    ResponseEntity readPlanListForMember(@RequestParam int page, @RequestParam int size, @RequestParam String sortBy, @RequestParam boolean isAsc);

    @Operation(summary = "Plan 전체목록 조회", description = " ")
    ResponseEntity readPlanListRedis(@RequestParam Long lastId, @RequestParam int size, @RequestParam String sortBy, @RequestParam boolean isAsc);

    @Operation(summary = "Plan 수정", description = " ")
    ResponseEntity updatePlan(@PathVariable Long planId, @RequestBody PlanDto.Update request);

    @Operation(summary = "Plan 한방 수정", description = "Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재")
    ResponseEntity updatePlanAllInOne(@PathVariable Long planId, @RequestBody PlanDto.UpdateAllInOne request);

    @Operation(summary = "Plan 한방 삭제 (구 API 주소)", description = "API 주소만 예전주소일뿐 동작은 똑같이 가능")
    ResponseEntity deletePlan(@PathVariable Long planId);

    @Operation(summary = "Plan 한방 삭제", description = " ")
    ResponseEntity deletePlanAllInOne(@PathVariable Long planId);










    @Operation(summary = "DayPlan 작성", description = " ")
    ResponseEntity createDayPlan(@PathVariable Long planId, @RequestBody DayPlanDto.Create request);

    @Operation(summary = "DayPlan 조회", description = "planId로 조회")
    ResponseEntity readDayPlan(@PathVariable Long planId);

    @Operation(summary = "DayPlan 수정", description = " ")
    ResponseEntity updateDayPlan(@PathVariable Long dayPlanId, @RequestBody DayPlanDto.Update request);

    @Operation(summary = "DayPlan 삭제", description = " ")
    ResponseEntity deleteDayPlan(@PathVariable Long dayPlanId);










    @Operation(summary = "UnitPlan 작성", description = " ")
    ResponseEntity createUnitPlan(@PathVariable Long dayPlanId, @RequestBody UnitPlanDto.Create request);

    @Operation(summary = "UnitPlan 조회", description = "dayPlanId로 조회")
    ResponseEntity readUnitPlan(@PathVariable Long dayPlanId);

    @Operation(summary = "UnitPlan 수정", description = " ")
    ResponseEntity updateUnitPlan(@PathVariable Long unitPlanId, @RequestBody UnitPlanDto.Update request);

    @Operation(summary = "UnitPlan 삭제", description = " ")
    ResponseEntity deleteUnitPlan(@PathVariable Long unitPlanId);










    @Operation(summary = "Plan 댓글 등록", description = " ")
    ResponseEntity createPlanComment(@PathVariable Long planId, @RequestBody PlanCommentDto.Create request);

    @Operation(summary = "Plan 댓글 조회", description = " ")
    ResponseEntity readPlanCommentList(@PathVariable Long planId, @RequestParam int page, @RequestParam int size, @RequestParam String sortBy, @RequestParam boolean isAsc);

    @Operation(summary = "Plan 댓글 수정", description = " ")
    ResponseEntity updatePlanComment(@PathVariable Long planId, @PathVariable Long commentId, @RequestBody PlanCommentDto.Update request);

    @Operation(summary = "Plan 댓글 삭제", description = " ")
    ResponseEntity deletePlanComment(@PathVariable Long planId, @PathVariable Long commentId);










    @Operation(summary = "Plan 좋아요 등록", description = "선택한 Plan 좋아요를 등록하는 API")
    ResponseEntity createPlanLike(@PathVariable Long planId) ;

    @Operation(summary = "Plan 좋아요 취소", description = "선택한 Plan 좋아요를 취소하는 API")
    ResponseEntity deletePlanLike(@PathVariable Long planId);

    @Operation(summary = "Plan 좋아요 전체목록 조회", description = "좋아요을 누른 Plan 목록을 페이지별로 조회하는 API")
    ResponseEntity getPlanLikeList(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "20") int size,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(summary = "Plan 스크랩 등록", description = "선택한 Plan 스크랩에 추가하는 API")
    ResponseEntity createPlanScrap(@PathVariable Long planId);

    @Operation(summary = "Plan 스크랩 취소", description = "선택한 Plan 스크랩에서 삭제하는 API")
    ResponseEntity deletePlanScrap(@PathVariable Long planId);

    @Operation(summary = "Plan 스크랩 전체목록 조회", description = "스크랩한 Plan 목록을 페이지별로 조회하는 API")
    ResponseEntity getPlanScrapList(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "20") int size, @AuthenticationPrincipal UserDetailsImpl userDetails);










    @Operation(summary = "(성찬전용) HTTPS 기능", description = "HTTPS 수신상태가 양호함을 AWS 와 통신하는 API")
    String healthcheck();
}