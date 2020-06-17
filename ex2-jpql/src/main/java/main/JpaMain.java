package main;

import entities.Member;
import entities.Team;

import javax.persistence.*;
import java.util.List;

public class JpaMain
{
    public static void main(String[] args)
    {
        // 어플리케이션 실행할 때 한 번만 실행
        EntityManagerFactory factory =  Persistence.createEntityManagerFactory("hello");

        // 한 번의 트랜잭션 당 하나 생성 (Thread 간 공유 금지)
        EntityManager entityManager = factory.createEntityManager();
        // 트랜잭션 생성 및 시작 (모든 데이터 변경은 트랜잭션 안에서 실행)
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try
        {
            /*
            // 반환 타입이 명확할 때
            TypedQuery<Member> typedQuery = entityManager.createQuery("select m from Member m", Member.class);
            // 반환 타입이 명확하지 않을 때
            Query query = entityManager.createQuery("select m.username, m.age from Member m", Member.class);

            // 결과가 하나 이상일 때 리스트, 결과 없을 땐 빈 리스트
            List<Member> members = typedQuery.getResultList();
            // 결과가 있으면 단일 객체, 결과 없을 땐 Exception 발생, 결과 2개 이상이어도 Exception
            Member result = typedQuery.getSingleResult();


            Member paramResult = entityManager.createQuery("select m from Member m where m.username = :username", Member.class)
                                    .setParameter("username", "test")
                                    .getSingleResult();
            */

            /*
            프로젝션
            List<Member> members = entityManager.createQuery("select m from Member m", Member.class).getResultList();

            Member findMember = members.get(0);
            findMember.setUsername("변경됨?");

            List<Object[]> resultList = entityManager.createQuery("select m.username, m.age from Member m").getResultList();

            System.out.println(resultList.get(0)[0]);
            System.out.println(resultList.get(0)[1]);
            */

            /*
            페이징
            for (int idx = 0; idx < 100; idx++)
            {
                Member member = new Member();
                member.setUsername("testMember" + idx);
                member.setAge(idx);
                entityManager.persist(member);
            }

            entityManager.flush();
            entityManager.clear();

            List<Member> members = entityManager.createQuery("select m from Member m order by m.age desc", Member.class)
                                        .setFirstResult(0)
                                        .setMaxResults(10)
                                        .getResultList();

            for (Member mem : members)
            {
                System.out.println(mem.toString());
            }
            */

            Team team = new Team();
            team.setName("team");
            entityManager.persist(team);

            Member member = new Member();
            member.setUsername("testMember");
            member.setAge(10);
            member.setTeam(team);

            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            String query = "select m from Member m left join m.team t on t.name = :name";
            List<Member> members = entityManager.createQuery(query, Member.class)
                                        .setParameter("name", "team")
                                        .getResultList();

            // 트랜잭션 커밋
            transaction.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // 트랜잭션 롤백
            transaction.rollback();
        }
        finally
        {
            // 트랜잭션 종료
            entityManager.close();
        }
        factory.close();
    }
}
