package org.hknu.swim_planer.service;

import lombok.RequiredArgsConstructor;
import org.hknu.swim_planer.DTO.MemberRegisterRequestDto;
import org.hknu.swim_planer.entity.FixedLesson;
import org.hknu.swim_planer.entity.Member;
import org.hknu.swim_planer.repo.FixedLessonRepository;
import org.hknu.swim_planer.repo.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRepository memberRepository;
    private final FixedLessonRepository fixedLessonRepository;

    @Transactional
    public Long registerMember(MemberRegisterRequestDto request) {
        // 1. 중복 이름 검증
        if (memberRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalStateException("이미 등록된 이름입니다.");
        }

        // 2. 직원 엔티티 생성 및 저장 (Builder 활용)
        Member member = Member.builder()
                .name(request.getName())
                .role(request.getRole())
                .shiftType(request.getShiftType())
                .defaultStartTime(request.getDefaultStartTime())
                .defaultEndTime(request.getDefaultEndTime())
                .build();

        Member savedMember = memberRepository.save(member);

        // 3. 고정 강습 시간이 함께 입력되었다면 저장
        if (request.getFixedLessons() != null && !request.getFixedLessons().isEmpty()) {
            for (var lessonDto : request.getFixedLessons()) {
                FixedLesson lesson = FixedLesson.builder()
                        .member(savedMember)
                        .dayOfWeek(lessonDto.getDayOfWeek())
                        .startTime(lessonDto.getStartTime())
                        .endTime(lessonDto.getEndTime())
                        .lessonName(lessonDto.getLessonName())
                        .build();
                fixedLessonRepository.save(lesson);
            }
        }

        return savedMember.getId();
    }
}