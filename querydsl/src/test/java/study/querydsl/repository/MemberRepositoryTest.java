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
class MemberRepositoryTest
{
    @Autowired
    EntityManager entityManager;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 멤버_추가()
    {
        // given
        String userName = "1hoon";
        Member member = new Member(userName);
        memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(member.getId()).get();
        // then
        assertThat(member).isEqualTo(findMember);
        assertThat(member.getUsername()).isEqualTo(findMember.getUsername());

        // when
        List<Member> all = memberRepository.findAll();
        // then
        assertThat(all).containsExactly(member);

        // when
        List<Member> byName = memberRepository.findByUsername(userName);
        // then
        assertThat(byName).containsExactly(member);
    }
}