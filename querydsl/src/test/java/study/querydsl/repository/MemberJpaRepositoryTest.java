package study.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest
{
    @Autowired
    EntityManager entityManager;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    void 멤버_추가()
    {
        // given
        String userName = "1hoon";
        Member member = new Member(userName);
        memberJpaRepository.save(member);

        // when
        Member findMember = memberJpaRepository.findById(member.getId()).get();
        // then
        assertThat(member).isEqualTo(findMember);
        assertThat(member.getUsername()).isEqualTo(findMember.getUsername());

        // when
        List<Member> all = memberJpaRepository.findAll();
        // then
        assertThat(all).containsExactly(member);

        // when
        List<Member> byName = memberJpaRepository.findByName(userName);
        // then
        assertThat(byName).containsExactly(member);
    }

    @Test
    void 멤버_찾기_queryDSL()
    {
        // given
        String userName = "1hoon";
        Member member = new Member(userName);
        memberJpaRepository.save(member);

        // when
        Member findMember = memberJpaRepository.findById(member.getId()).get();
        // then
        assertThat(member).isEqualTo(findMember);
        assertThat(member.getUsername()).isEqualTo(findMember.getUsername());

        // when
        List<Member> all = memberJpaRepository.findAll_queryDsl();
        // then
        assertThat(all).containsExactly(member);

        // when
        List<Member> byName = memberJpaRepository.findByName_queryDsl(userName);
        // then
        assertThat(byName).containsExactly(member);
    }

    @Test
    void 쿼리dql_dto()
    {
        // given
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

        // when
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(condition);

        // then
        assertThat(result).extracting("username").containsExactly("member4");
    }

    @Test
    void 쿼리dsl()
    {
        // given
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

        // when
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        List<MemberTeamDto> result = memberJpaRepository.search(condition);

        // then
        assertThat(result).extracting("username").containsExactly("member4");
    }
}