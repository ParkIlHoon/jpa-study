package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.*;
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

    @Test
    void 집합()
    {
        QMember member = QMember.member;

        List<Tuple> fetch = queryFactory.select(
                                                member.count(),
                                                member.age.sum(),
                                                member.age.avg(),
                                                member.age.max(),
                                                member.age.min()
                                                )
                                        .from(member)
                                        .fetch();

        Tuple tuple = fetch.get(0);

        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    /**
     * 팀의 이름과 각 팀의 평균 연령을 구하라
     */
    @Test
    void 집합_그룹()
    {
        QTeam team = QTeam.team;
        QMember member = QMember.member;

        List<Tuple> tupleList = queryFactory.select(
                                                    team.name,
                                                    member.age.avg()
                                                    )
                                            .from(member)
                                            .join(member.team, team)
                                            .groupBy(team.name)
                                            .fetch();

        Tuple tuple1 = tupleList.get(0);
        Tuple tuple2 = tupleList.get(1);

        assertThat(tuple1.get(team.name)).isEqualTo("teamA");
        assertThat(tuple1.get(member.age.avg())).isEqualTo(15);
        assertThat(tuple2.get(team.name)).isEqualTo("teamB");
        assertThat(tuple2.get(member.age.avg())).isEqualTo(35);
    }

    /**
     * teamA 에 소속된 모든 회원 조회
     */
    @Test
    void 기본조인()
    {
        QTeam team = QTeam.team;
        QMember member = QMember.member;

        List<Member> teamA = queryFactory.selectFrom(member)
                                            .leftJoin(member.team, team)
                                            .where(team.name.eq("teamA"))
                                            .fetch();

        assertThat(teamA)
                .extracting("username")
                .containsExactly("member1", "member2");
    }

    @Test
    void 세타조인()
    {
        entityManager.persist(new Member("teamA"));
        entityManager.persist(new Member("teamB"));
        entityManager.persist(new Member("teamC"));

        QTeam team = QTeam.team;
        QMember member = QMember.member;

        List<Member> members = queryFactory.select(member)
                                            .from(member, team)
                                            .where(member.username.eq(team.name))
                                            .fetch();

        assertThat(members)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

    /**
     * 회원과 팀을 조인하면서, 팀 이름이 teamA 인 팀만 조인, 회원은 모두 조회
     */
    @Test
    void 조인_ON절()
    {
        QTeam team = QTeam.team;
        QMember member = QMember.member;

        List<Tuple> memberList = queryFactory.select(member, team)
                                                .from(member)
                                                .leftJoin(member.team, team)
                                                    .on(team.name.eq("teamA"))
                                                .fetch();

        for(Tuple tuple : memberList)
        {
            System.out.println("tuple : " + tuple);
        }
    }

    @Test
    void 연관관계_없는_외부조인()
    {
        entityManager.persist(new Member("teamA"));
        entityManager.persist(new Member("teamB"));
        entityManager.persist(new Member("teamC"));

        QTeam team = QTeam.team;
        QMember member = QMember.member;

        List<Tuple> members = queryFactory.select(member, team)
                                            .from(member)
                                            .leftJoin(team).on(member.username.eq(team.name))
                                            .fetch();

        for(Tuple tuple : members)
        {
            System.out.println("tuple : " + tuple);
        }
    }

    @PersistenceUnit
    EntityManagerFactory entityManagerFactory;

    @Test
    void 페치조인_NO()
    {
        entityManager.flush();
        entityManager.clear();

        QTeam team = QTeam.team;
        QMember member = QMember.member;

        Member member1 = queryFactory.selectFrom(member)
                                        .where(member.username.eq("member1"))
                                        .fetchOne();

        boolean isLoaded = entityManagerFactory.getPersistenceUnitUtil().isLoaded(member1.getTeam());
        assertThat(isLoaded).isFalse();
    }

    @Test
    void 페치조인()
    {
        entityManager.flush();
        entityManager.clear();

        QTeam team = QTeam.team;
        QMember member = QMember.member;

        Member member1 = queryFactory.selectFrom(member)
                                        .join(member.team, team).fetchJoin()
                                        .where(member.username.eq("member1"))
                                        .fetchOne();

        boolean isLoaded = entityManagerFactory.getPersistenceUnitUtil().isLoaded(member1.getTeam());
        assertThat(isLoaded).isTrue();
    }

    /**
     * 나이가 가장 많은 회원을 조회
     */
    @Test
    void 서브쿼리()
    {
        QTeam team = QTeam.team;
        QMember member = QMember.member;
        QMember subMember = new QMember("sub");

        Member fetchOne = queryFactory.selectFrom(member)
                                        .where(member.age.eq(
                                                            select(subMember.age.max())
                                                                            .from(subMember)
                                                            ))
                                        .fetchOne();

        assertThat(fetchOne.getAge()).isEqualTo(40);
    }

    /**
     * 나이가 평균 이상인 회원을 조회
     */
    @Test
    void 서브쿼리_이상()
    {
        QMember member = QMember.member;
        QMember subMember = new QMember("sub");

        List<Member> members = queryFactory.selectFrom(member)
                                            .where(member.age.goe(
                                                                    select(subMember.age.avg())
                                                                                    .from(subMember)
                                                                ))
                                            .fetch();

        assertThat(members)
                .extracting("age")
                .containsExactly(30, 40);
    }

    @Test
    void 서브쿼리_IN()
    {
        QMember member = QMember.member;
        QMember subMember = new QMember("sub");

        List<Member> members = queryFactory.selectFrom(member)
                                            .where(member.age.in(
                                                                    select(subMember.age)
                                                                                    .from(subMember)
                                                                                    .where(subMember.age.gt(10))
                                                                ))
                                            .fetch();

        assertThat(members)
                .extracting("age")
                .containsExactly(20, 30, 40);
    }

    @Test
    void SELECT_서브쿼리()
    {
        QMember member = QMember.member;
        QMember subMember = new QMember("sub");

        List<Tuple> tupleList = queryFactory.select(
                                                    member.username,
                                                    select(subMember.age.avg())
                                                            .from(subMember)
                                                    )
                                            .from(member)
                                            .fetch();
        for(Tuple tuple : tupleList)
        {
            System.out.println("tuple : " + tuple);
        }
    }

    @Test
    void CASE문_단순()
    {
        QMember member = QMember.member;

        List<String> fetch = queryFactory.select(
                                                member.age
                                                        .when(10).then("열살")
                                                        .when(20).then("스무살")
                                                        .otherwise("기타")
                                                )
                                            .from(member)
                                            .fetch();

        for(String data : fetch)
        {
            System.out.println(data);
        }
    }

    @Test
    void CASE문_복잡()
    {
        QMember member = QMember.member;

        List<String> fetch = queryFactory.select(
                                                new CaseBuilder().when(member.age.between(0, 20)).then("0~20살")
                                                                .when(member.age.between(21, 30)).then("21~30살")
                                                                .otherwise("기타")
                                                )
                                            .from(member)
                                            .fetch();

        for(String data : fetch)
        {
            System.out.println(data);
        }
    }

    @Test
    void 상수처리()
    {
        QMember member = QMember.member;

        List<Tuple> tuples = queryFactory.select(
                                                member.username,
                                                Expressions.constant("A")
                                                )
                                            .from(member)
                                            .fetch();

        for (Tuple tuple : tuples)
        {
            System.out.println(tuple);
        }
    }

    @Test
    void 문자_더하기()
    {
        QMember member = QMember.member;

        List<String> fetch = queryFactory.select(member.username.concat("_").concat(member.age.stringValue()))
                                            .from(member)
                                            .fetch();

        for (String data : fetch)
        {
            System.out.println(data);
        }
    }
}
