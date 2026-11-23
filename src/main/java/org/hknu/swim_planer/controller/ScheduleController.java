package org.hknu.swim_planer.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hknu.swim_planer.DTO.DailyWorkScheduleResponseDto; // DTO 임포트
import org.hknu.swim_planer.entity.LeaveType;
import org.hknu.swim_planer.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List; // List 임포트 추가

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 1. 안전 근무 배정
    @PostMapping("/safety")
    public ResponseEntity<Long> assignSafetyWork(@RequestBody SafetyWorkRequest request) {
        Long scheduleId = scheduleService.assignSafetyWork(
                request.getWorkerId(),
                request.getWorkDate(),
                request.getStartTime(),
                request.getEndTime()
        );
        return ResponseEntity.ok(scheduleId);
    }

    // 2. 연차 / 공가 신청
    @PostMapping("/leave")
    public ResponseEntity<Void> registerLeave(@RequestBody LeaveRequestDto request) {
        scheduleService.registerLeave(
                request.getMemberId(),
                request.getLeaveDate(),
                request.getLeaveType(),
                request.getReason()
        );
        return ResponseEntity.ok().build();
    }

    // 3. 초과근무(대타) 배정
    @PostMapping("/overtime")
    public ResponseEntity<Long> assignOvertimeWork(@RequestBody OvertimeRequest request) {
        Long scheduleId = scheduleService.assignOvertimeWork(
                request.getSubstituteMemberId(),
                request.getCanceledScheduleId()
        );
        return ResponseEntity.ok(scheduleId);
    }

    // 4. 월별 전체 스케줄 조회
    @GetMapping
    public ResponseEntity<List<DailyWorkScheduleResponseDto>> getSchedules(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        List<DailyWorkScheduleResponseDto> schedules = scheduleService.getSchedulesBetween(startDate, endDate);
        return ResponseEntity.ok(schedules);
    }

    // --- 내부 요청 DTO 클래스들 ---
    @Getter
    public static class SafetyWorkRequest {
        private Long workerId;
        private LocalDate workDate;
        private LocalTime startTime;
        private LocalTime endTime;
    }

    @Getter
    public static class LeaveRequestDto {
        private Long memberId;
        private LocalDate leaveDate;
        private LeaveType leaveType;
        private String reason;
    }

    @Getter
    public static class OvertimeRequest {
        private Long substituteMemberId; // 대타로 들어가는 직원 ID
        private Long canceledScheduleId; // 연차로 인해 취소된 기존 스케줄 ID
    }
}