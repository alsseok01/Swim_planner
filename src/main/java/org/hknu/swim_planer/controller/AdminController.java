package org.hknu.swim_planer.controller;

import lombok.RequiredArgsConstructor;
import org.hknu.swim_planer.DTO.MemberRegisterRequestDto;
import org.hknu.swim_planer.service.AdminMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminMemberService adminMemberService;

    @PostMapping("/members")
    public ResponseEntity<Long> registerMember(@RequestBody MemberRegisterRequestDto request) {
        Long memberId = adminMemberService.registerMember(request);
        return ResponseEntity.ok(memberId);
    }
}