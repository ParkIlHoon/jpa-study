package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
public class QuerydslBulkTest
{
    @Autowired
    EntityManager entityManager;

    JPAQueryFactory queryFactory;

    @BeforeEach
    void before()
    {
        queryFactory = new JPAQueryFactory(entityManager);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        entityManager.persist(teamA);
        entityManager.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        entityManager.persist(member1);
        entityManager.persist(member2);
        entityManager.persist(member3);
        entityManager.persist(member4);
    }

    @Test
    void bulk_update()
    {
        // member1 -> 비회원
        // member2 -> 비회원
        // member3 -> 유지
        // member4 -> 유지

        long execute = queryFactory.update(member)
                                    .set(member.username, "비회원")
                                    .where(member.age.lt(20))
                                    .execute();

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void bulk_add()
    {
        queryFactory.update(member)
                    .set(member.age, member.age.add(1))
                    .execute();

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void bulk_delete()
    {
        queryFactory.delete(member)
                    .where(member.age.gt(18))
                    .execute();

        entityManager.flush();
        entityManager.clear();
    }
}
