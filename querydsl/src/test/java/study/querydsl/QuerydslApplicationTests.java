package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Hello;
import study.querydsl.entity.QHello;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class QuerydslApplicationTests {

    @Autowired
    EntityManager entityManager;

    @Test
    void contextLoads()
    {
        // given
        Hello hello = new Hello();
        entityManager.persist(hello);

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QHello qHello = QHello.hello;

        // when
        Hello fetchOne = queryFactory.selectFrom(qHello)
                                        .fetchOne();

        // then
        assertThat(fetchOne).isEqualTo(hello);
        assertThat(fetchOne.getId()).isEqualTo(hello.getId());
    }

}
