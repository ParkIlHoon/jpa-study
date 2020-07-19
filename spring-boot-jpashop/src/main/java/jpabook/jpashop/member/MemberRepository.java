package jpabook.jpashop.member;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository
{
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * 멤버를 저장한다.
	 * @param member
	 * @return
	 */
	public Long save(Member member)
	{
		entityManager.persist(member);
		return member.getId();
	}

	/**
	 * 멤버를 찾는다.
	 * @param id 찾을 멤버의 아이디
	 * @return
	 */
	public Member find(Long id)
	{
		return entityManager.find(Member.class, id);
	}
}
