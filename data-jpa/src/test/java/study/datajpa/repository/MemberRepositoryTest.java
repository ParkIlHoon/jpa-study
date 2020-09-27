package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Arrays;
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

    @Test
    void findByNamesTest()
    {
        Member member1 = new Member("AAA", 10, null);
        Member member2 = new Member("BBB", 20, null);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(1).getUsername()).isEqualTo("BBB");
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void findByNamesOptionalTest()
    {
        Member member1 = new Member("AAA", 10, null);
        Member member2 = new Member("BBB", 20, null);

        memberRepository.save(member1);
        memberRepository.save(member2);

        Optional<Member> byNamesOptional = memberRepository.findByNamesOptional(Arrays.asList("CCC", "DDD"));

        assertThat(byNamesOptional.isEmpty()).isTrue();
    }

    @Test
    void paging()
    {
        memberRepository.save(new Member("member1", 10, null));
        memberRepository.save(new Member("member2", 10, null));
        memberRepository.save(new Member("member3", 10, null));
        memberRepository.save(new Member("member4", 10, null));
        memberRepository.save(new Member("member5", 10, null));
        memberRepository.save(new Member("member6", 10, null));
        memberRepository.save(new Member("member7", 10, null));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> byAge = memberRepository.findByAge(10, pageRequest);

        // DTO로 바꾸기
        Page<MemberDto> map = byAge.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        List<Member> content = byAge.getContent();
        assertThat(content.size()).isEqualTo(3);
        assertThat(byAge.getTotalElements()).isEqualTo(7);
        assertThat(byAge.getNumber()).isEqualTo(0);
        assertThat(byAge.getTotalPages()).isEqualTo(3);
        assertThat(byAge.isFirst()).isTrue();
        assertThat(byAge.hasNext()).isTrue();
    }

    @Test
    void pagingSlice()
    {
        memberRepository.save(new Member("member1", 10, null));
        memberRepository.save(new Member("member2", 10, null));
        memberRepository.save(new Member("member3", 10, null));
        memberRepository.save(new Member("member4", 10, null));
        memberRepository.save(new Member("member5", 10, null));
        memberRepository.save(new Member("member6", 10, null));
        memberRepository.save(new Member("member7", 10, null));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Slice<Member> byAge = memberRepository.findAllByAge(10, pageRequest);

        List<Member> content = byAge.getContent();
        assertThat(content.size()).isEqualTo(3);
//        assertThat(byAge.getTotalElements()).isEqualTo(7);
        assertThat(byAge.getNumber()).isEqualTo(0);
//        assertThat(byAge.getTotalPages()).isEqualTo(3);
        assertThat(byAge.isFirst()).isTrue();
        assertThat(byAge.hasNext()).isTrue();
    }
}