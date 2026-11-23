package org.hknu.swim_planer.repo;

import org.hknu.swim_planer.entity.FixedLesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface FixedLessonRepository extends JpaRepository<FixedLesson, Long> {
    // 특정 직원의 특정 요일 고정 강습 조회
    List<FixedLesson> findByMemberIdAndDayOfWeek(Long memberId, DayOfWeek dayOfWeek);
}
