package org.hknu.swim_planer.repo;

import org.hknu.swim_planer.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByLeaveDateBetween(Date start, Date end);

    Optional<Object> findByName(String name);
}
