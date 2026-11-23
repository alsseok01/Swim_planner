package org.hknu.swim_planer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate leaveDate; // 연차/공가 날짜

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enum.LeaveType leaveType; // ANNUAL(연차), OFFICIAL(공가)

    // private String reason;       // 사유 (선택)
}