package study.querydsl;

import com.querydsl.core.QueryResults;
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

import java.util.List;

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

    @Test
    void 결과조회()
    {
        QMember member = QMember.member;

        List<Member> memberList = queryFactory.selectFrom(member)
                                                .fetch();

        Member one = queryFactory.selectFrom(member)
                                    .fetchOne();

        Member firstMember = queryFactory.selectFrom(member)
                                            .fetchFirst();

        QueryResults<Member> memberQueryResults = queryFactory.selectFrom(member)
                                                                .fetchResults();
        memberQueryResults.getTotal();
        List<Member> members = memberQueryResults.getResults();

        long fetchCount = queryFactory.selectFrom(member)
                                        .fetchCount();

    }

    /**
     * 1. 회원 나이 내림차순
     * 2. 회원 이름 오름치순
     * 단, 2에서 회원 이름이 없으면 마지막에 출력
     */
    @Test
    void 정렬()
    {
        entityManager.persist(new Member(null, 100));
        entityManager.persist(new Member("member5", 100));
        entityManager.persist(new Member("member6", 100));

        QMember member = QMember.member;

        List<Member> memberList = queryFactory.selectFrom(member)
                                                .where(member.age.eq(100))
                                                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                                                .fetch();

        Member member5 = memberList.get(0);
        Member member6 = memberList.get(1);
        Member memberNull = memberList.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    @Test
    void 페이징()
    {
        QMember member = QMember.member;

        List<Member> memberList = queryFactory.selectFrom(member)
                                                .orderBy(member.username.desc())
                                                .offset(1)
                                                .limit(2)
                                                .fetch();

        assertThat(memberList.size()).isEqualTo(2);
    }

    @Test
    void 페이징_전체수()
    {
        QMember member = QMember.member;

        QueryResults<Member> memberQueryResults = queryFactory.selectFrom(member)
                                                                .orderBy(member.username.desc())
                                                                .offset(1)
                                                                .limit(2)
                                                                .fetchResults();

        assertThat(memberQueryResults.getTotal()).isEqualTo(4);
        assertThat(memberQueryResults.getLimit()).isEqualTo(2);
        assertThat(memberQueryResults.getOffset()).isEqualTo(1);
        assertThat(memberQueryResults.getResults().size()).isEqualTo(2);
    }
}
