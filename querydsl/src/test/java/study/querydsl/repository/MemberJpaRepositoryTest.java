package study.querydsl.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;

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
}