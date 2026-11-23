package org.hknu.swim_planer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DailyWorkSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member worker; // 실제 근무를 수행하는 직원 (대타 투입 시 B가 들어감)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replaced_member_id")
    private Member replacedMember; // 연차 등으로 빠진 원 근무자 (초과근무일 경우 A 기록, 일반 근무는 null)

    @Column(nullable = false)
    private LocalDate workDate; // 근무 날짜

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enum.WorkType workType; // LESSON(강습), SAFETY(안전근무), OVERTIME(초과근무-대타)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enum.ScheduleStatus status; // ACTIVE(정상), CANCELED_BY_LEAVE(연차로 인한 취소)

    public void cancelByLeave() {
        this.status = Enum.ScheduleStatus.CANCELED_BY_LEAVE;
    }
}