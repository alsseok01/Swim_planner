package org.hknu.swim_planer.DTO;

import lombok.Getter;

import javax.management.relation.Role;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Getter
public class MemberRegisterRequestDto {
    private String name;
    private Role role; // ADMIN, STAFF
    private Enum.ShiftType shiftType; // MORNING, AFTERNOON
    private LocalTime defaultStartTime;
    private LocalTime defaultEndTime;

    // 직원이 담당하는 고정 강습 목록 (선택 사항)
    private List<FixedLessonDto> fixedLessons;

    @Getter
    public static class FixedLessonDto {
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private String lessonName;
    }
}
