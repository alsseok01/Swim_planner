package org.hknu.swim_planer.service;

import lombok.RequiredArgsConstructor;
import org.hknu.swim_planer.DTO.DailyWorkScheduleResponseDto;
import org.hknu.swim_planer.entity.*;
import org.hknu.swim_planer.repo.DailyWorkScheduleRepository;
import org.hknu.swim_planer.repo.FixedLessonRepository;
import org.hknu.swim_planer.repo.LeaveRequestRepository;
import org.hknu.swim_planer.repo.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final DailyWorkScheduleRepository scheduleRepository;
    private final FixedLessonRepository fixedLessonRepository;
    private final MemberRepository memberRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    /**
     * 1. 안전 근무 배정 (중복 시간 검증 포함)
     */
    @Transactional
    public Long assignSafetyWork(Long workerId, LocalDate workDate, LocalTime startTime, LocalTime endTime) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직원입니다."));

        // (1) 시간 중복 검증 (해당 날짜에 이미 잡힌 근무와 겹치는지 확인)
        validateTimeOverlap(workerId, workDate, startTime, endTime);

        // (2) 고정 강습 시간과 겹치는지 검증 (요일 기준)
        validateFixedLessonOverlap(workerId, workDate, startTime, endTime);

        // (3) 안전 근무 생성 및 저장
        DailyWorkSchedule safetySchedule = DailyWorkSchedule.builder()
                .worker(worker)
                .workDate(workDate)
                .startTime(startTime)
                .endTime(endTime)
                .workType(Enum.WorkType.SAFETY)
                .status(Enum.ScheduleStatus.ACTIVE)
                .build();

        return scheduleRepository.save(safetySchedule).getId();
    }

    /**
     * 2. 연차 등록 및 기존 스케줄 취소 처리
     */
    @Transactional
    public void registerLeave(Long memberId, LocalDate leaveDate, Enum.LeaveType leaveType, String reason) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직원입니다."));

        // (1) 연차 신청 저장
        LeaveRequest leaveRequest = LeaveRequest.builder()
                .member(member)
                .leaveDate(leaveDate)
                .leaveType(leaveType)
                .reason(reason)
                .build();
        leaveRequestRepository.save(leaveRequest);

        // (2) 해당 날짜에 잡혀있던 해당 직원의 활성 스케줄(강습 등)을 CANCELED_BY_LEAVE 상태로 변경
        List<DailyWorkSchedule> activeSchedules = scheduleRepository
                .findByWorkerIdAndWorkDateAndStatus(memberId, leaveDate, Enum.ScheduleStatus.ACTIVE);

        for (DailyWorkSchedule schedule : activeSchedules) {
            schedule.cancelByLeave(); // Entity 내부에 상태 변경 메서드 존재 가정
        }
    }

    /**
     * 3. 연차자로 인해 비어버린 시간에 다른 직원(대타) 초과근무 배정
     */
    @Transactional
    public Long assignOvertimeWork(Long substituteMemberId, Long canceledScheduleId) {
        Member substitute = memberRepository.findById(substituteMemberId)
                .orElseThrow(() -> new IllegalArgumentException("대타 직원을 찾을 수 없습니다."));

        // (1) 연차로 인해 취소된 기존 스케줄 조회
        DailyWorkSchedule canceledSchedule = scheduleRepository.findById(canceledScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("취소된 스케줄 정보를 찾을 수 없습니다."));

        LocalDate workDate = canceledSchedule.getWorkDate();
        LocalTime startTime = canceledSchedule.getStartTime();
        LocalTime endTime = canceledSchedule.getEndTime();
        Member originalWorker = canceledSchedule.getWorker(); // 연차 쓴 원 근무자 (A)

        // (2) 대타 직원(B)의 해당 시간대 중복 근무 검증
        validateTimeOverlap(substituteMemberId, workDate, startTime, endTime);
        validateFixedLessonOverlap(substituteMemberId, workDate, startTime, endTime);

        // (3) 초과근무(대타) 스케줄 생성 (누가 빠져서 들어갔는지 replacedMember에 A 기록)
        DailyWorkSchedule overtimeSchedule = DailyWorkSchedule.builder()
                .worker(substitute)                 // 실제로 일하는 사람 (B)
                .replacedMember(originalWorker)     // 대타 대상이 된 사람 (A)
                .workDate(workDate)
                .startTime(startTime)
                .endTime(endTime)
                .workType(Enum.WorkType.OVERTIME)
                .status(Enum.ScheduleStatus.ACTIVE)
                .build();

        return scheduleRepository.save(overtimeSchedule).getId();
    }

    // --- 공통 검증 메서드 ---

    private void validateTimeOverlap(Long workerId, LocalDate workDate, LocalTime startTime, LocalTime endTime) {
        List<DailyWorkSchedule> existingSchedules = scheduleRepository
                .findByWorkerIdAndWorkDateAndStatus(workerId, workDate, Enum.ScheduleStatus.ACTIVE);

        boolean hasOverlap = existingSchedules.stream().anyMatch(schedule ->
                isTimeOverlapping(startTime, endTime, schedule.getStartTime(), schedule.getEndTime())
        );

        if (hasOverlap) {
            throw new IllegalStateException("해당 시간대에 이미 다른 근무가 존재합니다.");
        }
    }

    private void validateFixedLessonOverlap(Long workerId, LocalDate workDate, LocalTime startTime, LocalTime endTime) {
        DayOfWeek dayOfWeek = workDate.getDayOfWeek();
        List<FixedLesson> fixedLessons = fixedLessonRepository.findByMemberIdAndDayOfWeek(workerId, dayOfWeek);

        boolean hasOverlap = fixedLessons.stream().anyMatch(lesson ->
                isTimeOverlapping(startTime, endTime, lesson.getStartTime(), lesson.getEndTime())
        );

        if (hasOverlap) {
            throw new IllegalStateException("해당 시간대에 고정 강습이 존재하여 배정할 수 없습니다.");
        }
    }

    // 시간 겹침 판단 알고리즘: (StartA < EndB) && (EndA > StartB)
    private boolean isTimeOverlapping(LocalTime startA, LocalTime endA, LocalTime startB, LocalTime endB) {
        return startA.isBefore(endB) && endA.isAfter(startB);
    }

    // 월별/기간별 스케줄 조회
    public List<DailyWorkScheduleResponseDto> getSchedulesBetween(LocalDate startDate, LocalDate endDate) {
        List<DailyWorkSchedule> schedules = scheduleRepository.findByWorkDateBetween(startDate, endDate);
        return schedules.stream()
                .map(DailyWorkScheduleResponseDto::new)
                .collect(Collectors.toList());
    }
}
