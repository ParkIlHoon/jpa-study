package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    public Member saveMember (Member member)
    {
        entityManager.persist(member);
        return member;
    }

    public Member find(Long id)
    {
        return entityManager.find(Member.class, id);
    }

    public void delete (Member member)
    {
        entityManager.remove(member);
    }

    public List<Member> findAll()
    {
        return entityManager.createQuery("select m from Member m", Member.class).getResultList();
    }

    public Optional<Member> findById(Long id)
    {
        Member member = entityManager.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count()
    {
        return entityManager.createQuery("select count(m) from Member m", Long.class).getSingleResult();
    }

    public List<Member> findByUsernameAndAgeGreaterThen(String username, int age)
    {
        return entityManager.createQuery("select m from Member m where m.username = :username and m.age > :age")
                            .setParameter("username", username)
                            .setParameter("age", age)
                            .getResultList();
    }

    public List<Member> findByPage(int age, int offset, int limit)
    {
        return entityManager.createQuery("select m from Member m where m.age = :age order by m.username desc")
                            .setParameter("age", age)
                            .setFirstResult(offset)
                            .setMaxResults(limit)
                            .getResultList();
    }

    public int bulkAgePlus (int age)
    {
        return entityManager.createQuery("update Member m set m.age = m.age + 1 where m.age = :age")
                            .setParameter("age", age)
                            .executeUpdate();
    }
}
