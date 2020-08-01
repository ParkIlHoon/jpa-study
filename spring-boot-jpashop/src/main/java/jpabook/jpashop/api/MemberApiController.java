package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Member 정보를 수정한다.
     * @param id
     * @param request
     * @return
     */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request)
    {
        // query 와 update 를 분리
        memberService.update(id, request.getName(), request.getAddress());

        Member one = memberService.findOne(id);

        return new UpdateMemberResponse(one.getId(), one.getName());
    }

    /**
     * Member 목록을 조회한다.
     * 1. 불필요한 Member 의 필드들이 노출되는 부작용이 있다.
     * @return 전체 Member 목록
     */
    @GetMapping("/api/v1/members")
    public List<Member> getMembersV1()
    {
        return memberService.findMembers();
    }

    /**
     * Member 목록을 조회한다.
     * @return
     */
    @GetMapping("/api/v2/members")
    public Result getMembersV2()
    {
        List<Member> members = memberService.findMembers();

        List<MemberDto> collect = members.stream()
                                        .map(m -> new MemberDto(m.getName()))
                                        .collect(Collectors.toList());

        return new Result(collect);
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

    @Data
    static class UpdateMemberResponse
    {
        private Long id;
        private String name;

        public UpdateMemberResponse(Long id, String name)
        {
            this.id = id;
            this.name = name;
        }
    }

    @Data
    static class UpdateMemberRequest
    {
        private String name;
        private Address address;
    }

    @Data
    @AllArgsConstructor
    static class Result<T>
    {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto
    {
        private String name;
    }
}
