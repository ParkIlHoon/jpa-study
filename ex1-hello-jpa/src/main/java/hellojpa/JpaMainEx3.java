package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMainEx3
{

    public static void main(String[] args)
    {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("hello");

        EntityManager manager = factory.createEntityManager();

        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();

        try
        {
            /*
            // 비영속 상태
            Member member = new Member();
            member.setId(100L);
            member.setName("비영속");

            // 영속 상태
            // 영속 상태가 된다고 해서 바로 SQL문이 실행되는 것은 아님
            System.out.println("=============== before persist ===============");
            manager.persist(member);
            System.out.println("=============== after persist ===============");

            // 영속성 컨텍스트에서 분리 -> 준영속 상태
            //manager.detach(member);

            // 1차 캐시 : DB가 아닌 영속성 컨텍스트에서 조회해옴
            Member findMember = manager.find(Member.class, 100L);
            System.out.println("findMember ID : " + findMember.getId());
            System.out.println("findMember NAME : " + findMember.getName());
            */

            /*
            System.out.println("=============== find 1 ===============");
            Member member1 = manager.find(Member.class, 100L);
            System.out.println("=============== find 2 ===============");
            // 1차 캐시로 인해 select 쿼리가 수행되지 않음
            Member member2 = manager.find(Member.class, 100L);
            System.out.println("=============== end ===============");
            // 영속 엔티티의 동일성 보장
            System.out.println(member1 == member2); // true
            */

            /* 쓰기지연 -> commit 시에 insert 쿼리 2개가 수행됨
            Member memberA = new Member();
            memberA.setId(110L);
            memberA.setName("MemberA");

            Member memberB = new Member();
            memberB.setId(120L);
            memberB.setName("MemberB");

            System.out.println("======== before persist A ========");
            manager.persist(memberA);
            System.out.println("======== before persist B ========");
            manager.persist(memberB);
            System.out.println("======== after persist ========");
            */

            /* dirty Checking : 객체의 변경을 감지해 자동으로 update 문이 수행됨
            // 영속성 컨텍스트에서 commit 수행 시 엔티티와 스냅샷을 비교해 변경을 감지함!!
            Member member = manager.find(Member.class, 110L);
            member.setName("dirtyChecking");
            */


            // commit 시에 SQL문 실행
            transaction.commit();
        }
        catch (Exception e)
        {
            transaction.rollback();
        }
        finally
        {
            manager.close();
        }

        factory.close();
    }

}
