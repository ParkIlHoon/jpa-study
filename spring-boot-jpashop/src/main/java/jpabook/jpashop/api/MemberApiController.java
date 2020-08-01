package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberApiController
{
    private final MemberService memberService;

    /**
     * 새 Member를 생성한다.
     * 1. Member Entity 의 필드명이 변경되면 API 전체의 스펙이 변경되는 부작용이 있다.
     * 2. Member Entity 가 노출되는 부작용이 있다.
     * @param member
     * @return 생성된 Member id
     * @version 1.0
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member)
    {
        Long join = memberService.join(member);
        return new CreateMemberResponse(join);
    }

    /**
     * 새 Member 를 생성한다.
     * @param request
     * @return
     * @verion 2.0
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request)
    {
        Member newMember = new Member();
        newMember.setName(request.getName());
        newMember.setAddress(request.getAddress());

        Long join = memberService.join(newMember);
        return new CreateMemberResponse(join);
    }

    @Data
    static class CreateMemberResponse
    {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class CreateMemberRequest
    {
        private String name;
        private Address address;
    }
}
