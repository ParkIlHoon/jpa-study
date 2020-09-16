package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

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
}