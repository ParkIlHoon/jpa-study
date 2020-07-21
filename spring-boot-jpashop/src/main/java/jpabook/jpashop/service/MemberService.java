package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)		// 조회 성능 향상을 위한 readOnly 설정
@RequiredArgsConstructor			// final 필드만을 사용한 생성자 자동 생성
public class MemberService
{
	private final MemberRepository memberRepository;

	/*
	 * setter 방식 injection 주입 방법
	 * 장점 : 테스트 시에 직접 mocking 가능함
	 * 단점 : 런타임 시점에 이 setter를 호출해 중간에 repository가 의도치 않게 변경될 수 있음
	@Autowired
	public void setMemberRepository(MemberRepository memberRepository)
	{
		this.memberRepository = memberRepository;
	}
	 */

	/*
	 * 생성자 방식 injection 주입
	 * 장점 : Service Bean 생성 시 초기화되기 때문에 런타임시에도 변경되지 않음
	 *       테스트 작성 시 service의 repository 의존성을 코드 작성 단계에서 확인 가능함
	 * lombok의 @RequiredArgsConstructor 어노테이션으로 자동 작성 가능
	public MemberService(MemberRepository memberRepository) 
	{
		this.memberRepository = memberRepository;
	}
	 */

	/**
	 * 회원 가입
	 * @param member
	 * @return
	 */
	@Transactional		// 쓰기 작업에는 readOnly=true 하면 안됨
	public Long join(Member member)
	{
		// 중복 회원 검증
		validateDuplicatedMember(member);
		// 회원 저장
		memberRepository.save(member);
		return member.getId();
	}

	/**
	 * 중복 회원 여부를 검증한다.
	 * @param member 검증 대상
	 * @throws IllegalStateException 중복될 경우
	 */
	private void validateDuplicatedMember(Member member)
	{
		List<Member> byName = memberRepository.findByName(member.getName());
		if (!byName.isEmpty())
		{
			throw new IllegalStateException("이미 존재하는 회원입니다.");
		}
	}

	/**
	 * 전체 사용자를 조회한다.
	 * @return 전체 사용자 목록
	 */
	public List<Member> findMembers()
	{
		return memberRepository.findAll();
	}

	/**
	 * 아이디로 사용자를 조회한다.
	 * @param memberId 조회할 아이디
	 * @return id에 해당하는 사용자 Member 객체
	 */
	public Member findOne(Long memberId)
	{
		return memberRepository.findOne(memberId);
	}
}
