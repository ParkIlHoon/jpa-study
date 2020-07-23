package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class OrderRepository
{
	private final EntityManager entityManager;

	/**
	 * 주문을 저장한다.
	 * @param order
	 */
	public void save(Order order)
	{
		entityManager.persist(order);
	}

	/***
	 * 주문을 조회한다.
	 * @param id
	 * @return
	 */
	public Order findOne(Long id)
	{
		return entityManager.find(Order.class, id);
	}

//	public List<Order> findAll(OrderSearch orderSearch)
//	{
//
//	}
}
