package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest
{
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void memberTest()
    {
        Member member = new Member("1hoon");

        Member saveMember = memberRepository.save(member);
        Optional<Member> byId = memberRepository.findById(saveMember.getId());

        assertThat(byId.isEmpty()).isFalse();
        assertThat(byId.get().getId()).isEqualTo(saveMember.getId());
    }

    @Test
    void findByUsernameAndAgeGreaterThanTest()
    {
        Member member1 = new Member("1hoon", 10, null);
        Member member2 = new Member("1hoon", 20, null);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("1hoon", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("1hoon");
        assertThat(result.get(0).getAge()).isGreaterThan(15);
        assertThat(result.size()).isEqualTo(1);
    }
}