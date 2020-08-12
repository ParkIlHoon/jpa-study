package study.querydsl;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import java.util.List;

import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
public class QuerydslFunctionTest
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
    void sql_function_call()
    {
        List<String> stringList = queryFactory.select(Expressions.stringTemplate(
                                                                        "function('replace', {0}, {1}, {2})",
                                                                            member.username, "member", "멤버"))
                                                .from(member)
                                                .fetch();

        for (String data : stringList)
        {
            System.out.println(data);
        }
    }

    @Test
    void sql_function_call2()
    {
        queryFactory.select(member.username)
                    .from(member)
                    .where(member.username.eq(Expressions.stringTemplate("function('lower', {0})", member.username)))
                    .fetch();
    }
}
