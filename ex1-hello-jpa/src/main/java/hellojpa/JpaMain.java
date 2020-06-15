package hellojpa;

import javassist.expr.Instanceof;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
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

            /*
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
            */

            /*
            Member member = new Member();
            member.setUserName("testUser");
            member.setCreatedDate(LocalDateTime.now());

            entityManager.persist(member);

//            entityManager.flush();
//            entityManager.clear();
//
//            Movie findMovie = entityManager.find(Movie.class, movie.getId());
            */

            /*
            프록시
            // SELECT 쿼리 수행 안됨
            Member refMember = entityManager.getReference(Member.class, 1L);
            
            // SELECT 쿼리 수행됨
            System.out.println(refMember.getUserName());

            // Member 타입이 아님 Proxy임
            System.out.println(refMember.getClass() == Member.class);
            System.out.println(refMember instanceof Member);

            // find시에는 Member 타입
            Member member = entityManager.find(Member.class, 2L);
            System.out.println(member.getClass() == Member.class);
            System.out.println(member instanceof Member);

            // 이미 영속성 컨텍스트에 존재하는 엔티티는 Member 타입으로 반환
            Member againMember = entityManager.getReference(Member.class, 2L);
            System.out.println(againMember.getClass() == Member.class);
            System.out.println(againMember instanceof Member);

            // 이렇게 하면 둘 다 Proxy로 반환됨
            Member refMember2 = entityManager.getReference(Member.class, 100L);
            System.out.println(refMember2.getClass() == Member.class);
            System.out.println(refMember2 instanceof Member);
            Member findMember = entityManager.find(Member.class, 100L);
            System.out.println(findMember.getClass() == Member.class);
            System.out.println(findMember instanceof Member);


            // 준영속 상태의 엔티티 대상으로 프록시 초기화 시 org.hibernate.LazyInitializationException 발생
            Member refMember3 = entityManager.getReference(Member.class, 200L);
            entityManager.detach(refMember3);
            refMember3.getUserName();
            */

            /*
            LAZY LOADING
            Member member = entityManager.find(Member.class, 1L);
            System.out.println(member.getClass() == Member.class);
            System.out.println(member instanceof Member);
            System.out.println(member.getTeam().getClass());

            System.out.println(member.getTeam().getName());
            System.out.println(member.getTeam().getClass());
             */

            /*
            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            // 원래는 3번 persist 해야해
            entityManager.persist(parent);
            //entityManager.persist(child1);
            //entityManager.persist(child2);
            */

            /*
            Address address = new Address("zipcode", "city", "street");

            Member member = entityManager.find(Member.class, 1L);
            member.setAddress(address);
            */

            Member member = new Member();
            member.setUserName("test");
            member.setAddress(new Address("zipcode","city","street"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족발");
            member.getFavoriteFoods().add("피자");
            
            member.getAddressHistory().add(new Address("12345","oldcity","oldstreet"));
            member.getAddressHistory().add(new Address("67890","newcity","newstreet"));

            entityManager.persist(member);

            // 주소 변경
            member.setAddress(new Address("zipcode2","city2","street2"));
            
            // 치킨 -> 곱창
            member.getFavoriteFoods().remove("치킨");
            member.getFavoriteFoods().add("곱창");

            // 값 타입 제거
            // 선행조건 : 값 타입 객체의 equals가 정확하게 구현되어있어야함
            member.getAddressHistory().remove(new Address("12345","oldcity","oldstreet"));
            member.getAddressHistory().add(new Address("00000","oldcity","oldstreet"));
            
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
