package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest
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
    void startJQPL()
    {
        // member1 찾기
        String memberName = "member1";
        Member singleResult = entityManager.createQuery(
                                            "select m" +
                                                    " from Member m" +
                                                    " where m.username = :username", Member.class)
                                            .setParameter("username", memberName)
                                            .getSingleResult();

        assertThat(singleResult.getUsername()).isEqualTo(memberName);
    }

    @Test
    void startQueryDSL()
    {
        // member1 찾기
        String memberName = "member1";
        QMember member = QMember.member;

        Member fetchOne = queryFactory.select(member)
                                        .from(member)
                                        .where(member.username.eq(memberName))
                                        .fetchOne();

        assertThat(fetchOne.getUsername()).isEqualTo(memberName);
    }

    @Test
    void 검색조건()
    {
        // member1 찾기
        String memberName = "member1";
        QMember member = QMember.member;

        Member fetchOne = queryFactory.selectFrom(member)
                                        .where(member.username.eq(memberName)
                                                .and(member.age.eq(10)))
                                        .fetchOne();

        assertThat(fetchOne.getUsername()).isEqualTo(memberName);
        assertThat(fetchOne.getAge()).isEqualTo(10);
    }

    @Test
    void 검색조건_AND()
    {
        // member1 찾기
        String memberName = "member1";
        QMember member = QMember.member;

        Member fetchOne = queryFactory.selectFrom(member)
                .where(member.username.eq(memberName), (member.age.eq(10)))
                .fetchOne();

        assertThat(fetchOne.getUsername()).isEqualTo(memberName);
        assertThat(fetchOne.getAge()).isEqualTo(10);
    }
}
