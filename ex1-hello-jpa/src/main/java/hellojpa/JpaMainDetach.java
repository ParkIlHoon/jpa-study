package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMainDetach
{
    public static void main(String[] args)
    {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("hello");

        EntityManager manager = factory.createEntityManager();

        EntityTransaction transaction = manager.getTransaction();

        transaction.begin();

        try
        {
            Member member = manager.find(Member.class, 100L);
            member.setName("영속상태");

            // member 엔티티만 준영속 상태로 지정
            manager.detach(member);
            // manager의 모든 컨텍스트를 초기화 -> 1차 캐시도 사라짐
            manager.clear();

            Member member1 = manager.find(Member.class, 100L);
            
            // 커밋 시 select 문만 수행되고 update 문은 수행안됨
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
