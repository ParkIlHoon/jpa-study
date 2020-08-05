package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;


@Repository
@RequiredArgsConstructor	// entityManager 필드 생성자 injection
public class MemberRepositoryOld
{
	/*
	 * 원래 @PersistenceContext 어노테이션이 필요한데, Spring boot에서 자동 지원해 제거 가능
	 */
	private final EntityManager entityManager;

	/**
	 * 사용자 정보를 저장한다.
	 * @param member 저장할 Member 객체
	 */
	public void save(Member member)
	{
		entityManager.persist(member);
	}

	/**
	 * 특정 사용자 한 명을 조회한다.
	 * @param id 조회할 사용자의 id(member_id)
	 * @return id에 해당하는 사용자 Member 객체
	 */
	public Member findOne(Long id)
	{
		return entityManager.find(Member.class, id);
	}

	/**
	 * 전체 사용자 목록을 조회한다.
	 * @return 전체 사용자 목록
	 */
	public List<Member> findAll()
	{
		return entityManager.createQuery("select m from Member m", Member.class).getResultList();
	}

	/**
	 * 특정 이름을 가지는 사용자 목록을 조회한다.
	 * @param name 조회할 사용자 이름
	 * @return name을 이름으로 가지는 사용자 목록
	 */
	public List<Member> findByName(String name)
	{
		return entityManager.createQuery("select m from Member m where m.name = :name", Member.class)
				.setParameter("name", name)
				.getResultList();
	}
}
