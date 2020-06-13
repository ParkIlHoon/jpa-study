package hellojpa;

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
            멤버 객체 생성
            Member member = new Member();
            member.setId(2L);
            member.setName("helloB");

            멤버 객체 저장
            entityManager.persist(member);
            */

            /*
            멤버 데이터 조회
            Member member = entityManager.find(Member.class, 1L);
            */

            /*
            멤버 데이터 수정
            member.setName("helloJPA");
            */

            /*
            멤버 제거
            entityManager.remove(member);
            */

            /* 검색 쿼리를 위한 쿼리 작성 가능 (조회 대상은 테이블이 아닌 엔티티 객체임!!) => JPQL
            List<Member> list = entityManager.createQuery("SELECT m FROM Member as m", Member.class)
                                    .setFirstResult(0)      // 0번째 데이터 부터
                                    .setMaxResults(100)     // 100개 데이터를 가져와
                                    .getResultList();
            */

            /*
            단방향 연관관계
            Team team = new Team();
            team.setName("TeamA");
            entityManager.persist(team);

            Member member = new Member();
            member.setUserName("팀유저1");
            member.setTeam(team);
            entityManager.persist(member);

            Team findTeam = entityManager.find(Member.class, member.getId()).getTeam();
            System.out.println(findTeam.getName());
            */
            
            /*
            양방향 연관관계
            Team team = entityManager.find(Team.class, 1L);

            Member member = new Member();
            member.setUserName("팀유저3");
            member.changeTeam(team);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            Member findMember = entityManager.find(Member.class, member.getId());
            List<Member> members = findMember.getTeam().getMembers();
            for (Member mem : members)
            {
                System.out.println(mem.getUserName());
            }
            */
            
            Movie movie = new Movie();
            movie.setActor("서현진");
            movie.setDirector("봉준호");
            movie.setName("띵작4");
            movie.setPrice(10000);
            
            Album album = new Album();
            album.setArtist("서현진");
            album.setName("띵곡");
            album.setPrice(8000);
            
            Book book = new Book();
            book.setAuthor("서현진");
            book.setIsbn("1231213");
            book.setName("띵서");
            book.setPrice(8500);
            
            entityManager.persist(movie);
            entityManager.persist(album);
            entityManager.persist(book);

//            entityManager.flush();
//            entityManager.clear();
//
//            Movie findMovie = entityManager.find(Movie.class, movie.getId());

            // 트랜잭션 커밋
            transaction.commit();
        }
        catch (Exception e)
        {
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
