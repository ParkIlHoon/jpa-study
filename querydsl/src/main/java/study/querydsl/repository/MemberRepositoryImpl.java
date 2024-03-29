package study.querydsl.repository;


import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;
import study.querydsl.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

public class MemberRepositoryImpl implements MemberRepositoryCustom
{
    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager entityManager)
    {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<MemberTeamDto> search(MemberSearchCondition condition)
    {
        return queryFactory.select(new QMemberTeamDto(member.id.as("memberId"),
                                                        member.username,
                                                        member.age,
                                                        team.id.as("teamId"),
                                                        team.name.as("teamName")))
                            .from(member)
                            .leftJoin(member.team, team)
                            .where(
                                    usernameEq(condition.getUsername()),
                                    teamNameEq(condition.getTeamName()),
                                    ageGoe(condition.getAgeGoe()),
                                    ageLoe(condition.getAgeLoe())
                                    )
                            .fetch();
    }

    @Override
    public Page searchPageSimple(MemberSearchCondition condition, Pageable pageable)
    {
        QueryResults<MemberTeamDto> results = queryFactory.select(new QMemberTeamDto(member.id.as("memberId"),
                    member.username,
                    member.age,
                    team.id.as("teamId"),
                    team.name.as("teamName")))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MemberTeamDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page searchPageComplex(MemberSearchCondition condition, Pageable pageable)
    {
        List<MemberTeamDto> fetch = queryFactory.select(new QMemberTeamDto(member.id.as("memberId"),
                member.username,
                member.age,
                team.id.as("teamId"),
                team.name.as("teamName")))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 페이징 쿼리 최적화 -> content 사이즈가 페이지 사이즈보다 작을 땐 count 쿼리를 실행시키지 않는다.
        JPAQuery<Member> jpaQuery = queryFactory.select(member)
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                );

//        return new PageImpl<>(fetch, pageable, count);
        return PageableExecutionUtils.getPage(fetch, pageable, () -> jpaQuery.fetchCount());
    }

    private BooleanExpression ageLoe(Integer ageLoe)
    {
        return ageLoe != null? member.age.loe(ageLoe) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe)
    {
        return ageGoe != null? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression teamNameEq(String teamName)
    {
        return hasText(teamName)? team.name.eq(teamName) : null;
    }

    private BooleanExpression usernameEq(String username)
    {
        return hasText(username)? member.username.eq(username) : null;
    }


}
