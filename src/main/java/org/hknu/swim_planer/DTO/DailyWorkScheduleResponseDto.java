package org.hknu.swim_planer.DTO;

import lombok.Getter;
import org.hknu.swim_planer.entity.DailyWorkSchedule;
import org.hknu.swim_planer.entity.ScheduleStatus;
import org.hknu.swim_planer.entity.WorkType;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class DailyWorkScheduleResponseDto {
    private Long id;
    private Long workerId;
    private String workerName;
    private Long replacedMemberId;
    private String replacedMemberName;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private WorkType workType;
    private ScheduleStatus status;

    public DailyWorkScheduleResponseDto(DailyWorkSchedule schedule) {
        this.id = schedule.getId();
        this.workerId = schedule.getWorker().getId();
        this.workerName = schedule.getWorker().getName();

        if (schedule.getReplacedMember() != null) {
            this.replacedMemberId = schedule.getReplacedMember().getId();
            this.replacedMemberName = schedule.getReplacedMember().getName();
        }

        this.workDate = schedule.getWorkDate();
        this.startTime = schedule.getStartTime();
        this.endTime = schedule.getEndTime();
        this.workType = schedule.getWorkType();
        this.status = schedule.getStatus();
    }
}