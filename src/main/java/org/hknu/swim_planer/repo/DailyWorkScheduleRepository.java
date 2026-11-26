package org.hknu.swim_planer.repo;

import org.hknu.swim_planer.entity.DailyWorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyWorkScheduleRepository extends JpaRepository<DailyWorkSchedule, Long> {
    List<DailyWorkSchedule> findByWorkerIdAndWorkDateAndStatus(Long workerId, LocalDate workDate, Enum.ScheduleStatus status);
    List<DailyWorkSchedule> findByWorkDateBetween(LocalDate startDate, LocalDate endDate);
}
