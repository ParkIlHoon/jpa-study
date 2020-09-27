package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest
{
    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
    void testMember()
    {
        Member member = new Member("1hoon");
        Member saveMember = memberJpaRepository.saveMember(member);
        Member findMember = memberJpaRepository.find(saveMember.getId());

        assertThat(findMember.getId()).isEqualTo(saveMember.getId());
    }

    @Test
    void paging()
    {
        memberJpaRepository.saveMember(new Member("member1", 10, null));
        memberJpaRepository.saveMember(new Member("member2", 10, null));
        memberJpaRepository.saveMember(new Member("member3", 10, null));
        memberJpaRepository.saveMember(new Member("member4", 10, null));
        memberJpaRepository.saveMember(new Member("member5", 10, null));
        memberJpaRepository.saveMember(new Member("member6", 10, null));
        memberJpaRepository.saveMember(new Member("member7", 10, null));

        List<Member> byPage = memberJpaRepository.findByPage(10, 0, 3);

        assertThat(byPage.size()).isEqualTo(3);
    }
}