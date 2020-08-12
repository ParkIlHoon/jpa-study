package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
public class QuerydslDeeperTest
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
    void simple_projection()
    {
        List<String> result = queryFactory.select(member.username)
                                            .from(member)
                                            .fetch();

        for (String userName : result)
        {
            System.out.println(userName);
        }
    }

    @Test
    void tuple_projection()
    {
        List<Tuple> tuples = queryFactory.select(member.username, member.age)
                                            .from(member)
                                            .fetch();

        for (Tuple tuple : tuples)
        {
            System.out.println("username : " + tuple.get(member.username));
            System.out.println("age : " + tuple.get(member.age));
        }
    }

    @Test
    void dto_projection_setter()
    {
        List<MemberDto> memberDtoList = queryFactory
                                            .select(
                                                    Projections.bean(MemberDto.class,
                                                                    member.username,
                                                                    member.age)
                                                    )
                                            .from(member)
                                            .fetch();

        for (MemberDto memberDto : memberDtoList)
        {
            System.out.println(memberDto);
        }
    }

    /**
     * Getter, Setter 가 없어도 가능
     */
    @Test
    void dto_projection_field()
    {
        List<MemberDto> memberDtoList = queryFactory
                                            .select(
                                                    Projections.fields(MemberDto.class,
                                                                    member.username,
                                                                    member.age)
                                                    )
                                            .from(member)
                                            .fetch();

        for (MemberDto memberDto : memberDtoList)
        {
            System.out.println(memberDto);
        }
    }

    /**
     * 생성자 이용 방식.
     * 생성자의 파라미터와 타입이 일치해야함
     */
    @Test
    void dto_projection_constructor()
    {
        List<MemberDto> memberDtoList = queryFactory
                                            .select(
                                                    Projections.constructor(MemberDto.class,
                                                                    member.username,
                                                                    member.age)
                                                    )
                                            .from(member)
                                            .fetch();

        for (MemberDto memberDto : memberDtoList)
        {
            System.out.println(memberDto);
        }
    }

    /**
     * 다른 DTO로 바로 조회하기.
     * 필드명이 다르면 null 값이 들어가므로 별도 처리 필요
     */
    @Test
    void dto_projection_otherDto_field()
    {
        QMember qMember = new QMember("sub");

        List<UserDto> userDtoList = queryFactory
                                        .select(
                                                Projections.fields(UserDto.class,
                                                        member.username.as("name"),
                                                        ExpressionUtils.as(JPAExpressions.select(qMember.age.max())
                                                                                            .from(qMember), "age"))
                                                )
                                        .from(member)
                                        .fetch();

        for (UserDto userDto : userDtoList)
        {
            System.out.println(userDto);
        }
    }

    @Test
    void dto_projection_otherDto_constructor()
    {
        QMember qMember = new QMember("sub");

        List<UserDto> userDtoList = queryFactory
                                        .select(
                                                Projections.constructor(UserDto.class,
                                                        member.username,
                                                        member.age)
                                                )
                                        .from(member)
                                        .fetch();

        for (UserDto userDto : userDtoList)
        {
            System.out.println(userDto);
        }
    }

    /**
     * Dto 의 생성자에 @Projection 어노테이션을 붙여 사용하는 방법
     * 생성자 방식은 런타임시에만 오류 감지가 가능하나, 이 방식은 코딩 중에 오류 감지 가능
     * BUT,
     */
    @Test
    void queryProjection()
    {
        List<MemberDto> memberDtoList = queryFactory.select(new QMemberDto(member.username, member.age))
                                                    .from(member)
                                                    .fetch();

        for (MemberDto dto : memberDtoList)
        {
            System.out.println(dto);
        }
    }


    @Test
    void dynamicQuery_booleanBuilder()
    {
        String usernameParam  = "member1";
        int ageParam = 10;

        List<Member> result = searchMember1(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember1(String usernameParam, Integer ageParam)
    {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (usernameParam != null)
        {
            booleanBuilder.and(member.username.eq(usernameParam));
        }
        if (ageParam != null)
        {
            booleanBuilder.and(member.age.eq(ageParam));
        }

        return queryFactory.selectFrom(member)
                            .where(booleanBuilder)
                            .fetch();
    }


    @Test
    void dynamicQuery_whereParam()
    {
        String usernameParam  = "member1";
        int ageParam = 10;

        List<Member> result = searchMember2(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember2(String usernameParam, Integer ageParam)
    {
        return queryFactory.selectFrom(member)
                            .where(usernameEq(usernameParam), ageEq(ageParam))
                            .fetch();
    }

    private Predicate usernameEq(String usernameParam)
    {
        return usernameParam != null ? member.username.eq(usernameParam) : null;
    }

    private Predicate ageEq(Integer ageParam)
    {
        return ageParam != null ? member.age.eq(ageParam) : null;
    }
}