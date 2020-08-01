package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDb
{
    private final InitService initService;

    @PostConstruct
    public void init()
    {
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService
    {
        private final EntityManager entityManager;

        public void dbInit()
        {
            Member userA = createMember("userA");
            Member userB = createMember("userB");

            entityManager.persist(userA);
            entityManager.persist(userB);

            Book book1 = createBook("JPA Book1", 1000, 100);
            Book book2 = createBook("JPA Book2", 2000, 100);

            entityManager.persist(book1);
            entityManager.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 1000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 2000, 2);
            OrderItem orderItem3 = OrderItem.createOrderItem(book1, 1000, 1);
            OrderItem orderItem4 = OrderItem.createOrderItem(book2, 2000, 2);

            Delivery delivery1 = createDelivery(userA);
            Delivery delivery2 = createDelivery(userB);

            Order order1 = Order.createOrder(userA, delivery1, orderItem1, orderItem2);
            entityManager.persist(order1);

            Order order2 = Order.createOrder(userB, delivery2, orderItem3, orderItem4);
            entityManager.persist(order2);
        }

        private Member createMember(String name)
        {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address("서울", "몰라", "12345"));

            return member;
        }

        private Book createBook(String name, int price, int count)
        {
            Book book = new Book();
            book.setName(name);
            book.setPrice(price);
            book.setStockQuantity(count);

            return book;
        }

        private Delivery createDelivery(Member member)
        {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());

            return delivery;
        }
    }
}