package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberCustomRepository
{
    private final EntityManager entityManager;

    @Override
    public List<Member> findMemberCustom()
    {
        return entityManager.createQuery("select m from Member m").getResultList();
    }
}
