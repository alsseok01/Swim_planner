package org.hknu.swim_planer.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.management.relation.Role;
import java.time.LocalTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // 이름으로 로그인하므로 unique 처리

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // ADMIN, STAFF

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enum.ShiftType shiftType; // MORNING(오전반), AFTERNOON(오후반)

    @Column(nullable = false)
    private LocalTime defaultStartTime; // 기본 출근 시간

    @Column(nullable = false)
    private LocalTime defaultEndTime;   // 기본 퇴근 시간
}