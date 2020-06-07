package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMainFlush
{
    public static void main(String[] args)
    {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("hello");

        EntityManager manager = factory.createEntityManager();

        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();

        try
        {
            Member member = new Member();
            member.setId(200L);
            //member.setName("flushTest");

            manager.persist(member);

            // SQL 즉시 실행
            manager.flush();

            System.out.println("---------------------------------");

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
