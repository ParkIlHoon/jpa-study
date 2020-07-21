package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest
{
	@Autowired
	EntityManager entityManager;

	@Autowired
	OrderService orderService;

	@Autowired
	OrderRepository orderRepository;

	@Test
	void 상품주문() throws Exception
	{
		//given
		int orderCount = 2;
		int price = 1000;
		int stockQuantity = 10;

		Member member = createMember();
		Item item = createItem(price, stockQuantity);

		//when
		Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

		//then
		Order findOrder = orderRepository.findOne(orderId);

		assertEquals(OrderStatus.ORDER, findOrder.getStatus(), "상품 주문 시 상태는 ORDER");
		assertEquals(findOrder.getOrderItems().size(), 1, "주문한 상품 종류 수가 정확해야한다.");
		assertEquals(1000 * orderCount, findOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다.");
		assertEquals(10 - orderCount, item.getStockQuantity(), "주문 수량만큼 재고가 줄어야한다.");
	}

	@Test
	void 주문취소() throws Exception
	{
		//given
		int orderCount = 2;
		int price = 1000;
		int stockQuantity = 10;

		Member member = createMember();
		Item item = createItem(price, stockQuantity);
		Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

		//when
		Order findOrder = orderRepository.findOne(orderId);
		findOrder.cancel();

		//then
		assertEquals(OrderStatus.CANCEL, findOrder.getStatus(), "주문이 취소되면 주문 상태가 CANCEL 이어야한다.");
		assertEquals(item.getStockQuantity(), stockQuantity, "주문이 취소되면 재고 수량이 복구되어야한다.");
	}

	@Test
	void 상품주문_재고수량초과() throws Exception
	{
		//given
		int orderCount = 20;
		int price = 1000;
		int stockQuantity = 10;

		Member member = createMember();
		Item item = createItem(price, stockQuantity);

		//when & then
		NotEnoughStockException thrown = assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), item.getId(), orderCount));
	}


	private Member createMember()
	{
		Member member = new Member();
		member.setName("1hoon");
		member.setAddress(new Address("대전", "갈마", "12345"));
		entityManager.persist(member);
		return member;
	}

	private Item createItem(int price, int stockQuantity)
	{
		Item item = new Book();
		item.setName("책");
		item.setPrice(price);
		item.setStockQuantity(stockQuantity);
		entityManager.persist(item);
		return item;
	}

}