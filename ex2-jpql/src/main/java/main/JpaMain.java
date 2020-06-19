package main;

import entities.Member;
import entities.Team;
import types.MemberType;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
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
			team.setName("teamA");
			entityManager.persist(team);

			Team team2 = new Team();
			team2.setName("teamB");
			entityManager.persist(team2);

			Member member = new Member();
			member.setUsername("회원1");
			member.setAge(10);
			member.setType(MemberType.ADMIN);
			member.setTeam(team);

			Member member2 = new Member();
			member2.setUsername("회원2");
			member2.setTeam(team);

			Member member3 = new Member();
			member3.setUsername("회원3");
			member3.setTeam(team2);

			Member member4 = new Member();
			member4.setUsername("회원4");

			entityManager.persist(member);
			entityManager.persist(member2);
			entityManager.persist(member3);
			entityManager.persist(member4);

			entityManager.flush();
			entityManager.clear();
            /*
            조인
            String query = "select m from Member m left join m.team t on t.name = :name";
            List<Member> members = entityManager.createQuery(query, Member.class)
                                        .setParameter("name", "team")
                                        .getResultList();

            for (Member mem : members)
            {
                System.out.println(mem.toString());
            }
            */

            /*
            타입
            String query = "select m from Member m where m.type = types.MemberType.ADMIN";
            List<Member> members = entityManager.createQuery(query, Member.class)
                    .getResultList();
            */

            /*
            String query =   "select case when m.age <= 10 then '학생요금'"
                           + "            when m.age >= 60 then '경로요금'"
                           + "            else '일반요금'"
                           + "       end"
                           + "  from Member m";
            List<String> resultList = entityManager.createQuery(query, String.class).getResultList();

            for (String result : resultList)
            {
                System.out.println(result);
            }
            */

            /*
            String query = "select coalesce(m.username, '이름 없는 회원') from Member m";
            List<String> resultList = entityManager.createQuery(query, String.class).getResultList();

            for (String result : resultList)
            {
                System.out.println(result);
            }
            */

            /*
            String query = "select nullif(m.username, 'testMember') from Member m";
            List<String> resultList = entityManager.createQuery(query, String.class).getResultList();

            for (String result : resultList)
            {
                System.out.println(result);
            }
             */

			/*
			묵시적 내부 조인
			String query = "select m.team.name from Member m";
			List<String> results = entityManager.createQuery(query, String.class).getResultList();

			for (String result : results)
			{
				System.out.println(result);
			}
			 */

			/*
			 * fetch join
			 * SQL 조인이 아님. JPQL 전용 기능.
			 * 연관된 엔티티나 컬렉션을 SQL 한 번에 함께 조회
			 */
			/*
			String query = "select m from Member m join fetch m.team";
			List<Member> memberList = entityManager.createQuery(query, Member.class).getResultList();

			for (Member m : memberList)
			{
				System.out.println(m.toString());
				System.out.println(m.getTeam().getName());
			}
			*/

			/*
			* 컬렉션 fetch join
			* 일대다
			*/
			/*
			String query = "select t from Team t join fetch t.members";
			List<Team> teamList = entityManager.createQuery(query, Team.class).getResultList();

			for (Team t : teamList)
			{
				System.out.println(t.toString());
				System.out.println(t.getMembers().size());
			}
			*/

			/*
			* SQL의 DISTINCT + 애플리케이션의 DISTINCT
			*/
			String query = "select distinct t from Team t join fetch t.members";
			List<Team> teamList = entityManager.createQuery(query, Team.class).getResultList();

			for (Team t : teamList)
			{
				System.out.println(t.toString());
				System.out.println(t.getMembers().size());
			}

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
