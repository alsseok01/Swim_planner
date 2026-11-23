package org.hknu.swim_planer.service;

import lombok.RequiredArgsConstructor;
import org.hknu.swim_planer.repo.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;

    /**
     * 이름으로 바로 로그인 처리
     */
    public MemberResponseDto login(String name) {
        Member member = memberRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이름입니다. 관리자에게 문의하세요."));

        return new MemberResponseDto(member);
    }
}