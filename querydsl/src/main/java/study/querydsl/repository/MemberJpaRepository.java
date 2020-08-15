package study.querydsl.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

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


    public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition)
    {
        BooleanBuilder builder = new BooleanBuilder();
        if(hasText(condition.getUsername()))
        {
            builder.and(member.username.eq(condition.getUsername()));
        }
        if(hasText(condition.getTeamName()))
        {
            builder.and(team.name.eq(condition.getTeamName()));
        }
        if(condition.getAgeGoe() != null)
        {
            builder.and(member.age.goe(condition.getAgeGoe()));
        }
        if(condition.getAgeLoe() != null)
        {
            builder.and(member.age.loe(condition.getAgeLoe()));
        }

        return queryFactory.select(new QMemberTeamDto(member.id.as("memberId"),
                                                        member.username,
                                                        member.age,
                                                        team.id.as("teamId"),
                                                        team.name.as("teamName")))
                            .from(member)
                            .leftJoin(member.team, team)
                            .where(builder)
                            .fetch();
    }
}
