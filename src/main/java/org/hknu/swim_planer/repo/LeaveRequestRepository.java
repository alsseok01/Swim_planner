package org.hknu.swim_planer.repo;

import org.hknu.swim_planer.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByMemberIdAndLeaveDate(Long memberId, LocalDate leaveDate);
}
