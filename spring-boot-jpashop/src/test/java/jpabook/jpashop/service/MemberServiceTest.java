package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest
{
	@Autowired
	MemberService memberService;

	@Autowired
	MemberRepository memberRepository;

	@Test
	void 회원가입() throws Exception
	{
		//given
		Member member = new Member();
		member.setName("1hoon");

		//when
		Long joinId = memberService.join(member);

		//then
		assertEquals(member, memberRepository.findOne(joinId));
	}

	@Test
	void 중복_회원_예외() throws Exception
	{
		//given
		String name = "1hoon";

		Member memberA = new Member();
		memberA.setName(name);

		Member memberB = new Member();
		memberB.setName(name);

		//when
		memberService.join(memberA);

		//then
		IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> memberService.join(memberB));
		assertEquals("이미 존재하는 회원입니다.", thrown.getMessage());
	}
}