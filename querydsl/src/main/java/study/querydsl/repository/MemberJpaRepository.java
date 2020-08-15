package study.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static study.querydsl.entity.QMember.member;

@Repository
public class MemberJpaRepository
{
    private final EntityManager entityManager;

    private final JPAQueryFactory queryFactory;

    public MemberJpaRepository(EntityManager entityManager)
    {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }


    public void save(Member member)
    {
        entityManager.persist(member);
    }

    public Optional<Member> findById(Long id)
    {
        Member find = entityManager.find(Member.class, id);
        return Optional.ofNullable(find);
    }

    public List<Member> findAll()
    {
        return entityManager.createQuery("select m from Member  m", Member.class).getResultList();
    }

    public List<Member> findAll_queryDsl()
    {
        return queryFactory.selectFrom(member).fetch();
    }

    public List<Member> findByName(String name)
    {
        return entityManager.createQuery("select m from Member m where m.username = :username", Member.class)
                        .setParameter("username", name)
                        .getResultList();
    }
    public List<Member> findByName_queryDsl(String name)
    {
        return queryFactory.selectFrom(member)
                            .where(member.username.eq(name))
                            .fetch();
    }
}
