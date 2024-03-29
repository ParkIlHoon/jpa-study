package jpabook.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.api.SimpleOrderDto;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
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

	public List<Order> findAllByCriteria(OrderSearch orderSearch)
	{
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> cq = cb.createQuery(Order.class);
		Root<Order> o = cq.from(Order.class);
		Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
		List<Predicate> criteria = new ArrayList<>();
		//주문 상태 검색
		if (orderSearch.getOrderStatus() != null) {
			Predicate status = cb.equal(o.get("status"),
					orderSearch.getOrderStatus());
			criteria.add(status);
		}
		//회원 이름 검색
		if (StringUtils.hasText(orderSearch.getMemberName())) {
			Predicate name =
					cb.like(m.<String>get("name"), "%" +
							orderSearch.getMemberName() + "%");
			criteria.add(name);
		}
		cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
		TypedQuery<Order> query = entityManager.createQuery(cq).setMaxResults(1000); //최대 1000건
		return query.getResultList();
	}

	public List<Order> findAll(OrderSearch orderSearch)
	{
		QOrder order = QOrder.order;
		QMember member = QMember.member;

		JPAQueryFactory factory = new JPAQueryFactory(entityManager);
		return factory.select(order)
					.from(order)
					.join(order.member, member)
					.where(statusEq(orderSearch.getOrderStatus()), member.name.like(orderSearch.getMemberName()))
					.limit(1000)
					.fetch();
	}

	private BooleanExpression statusEq(OrderStatus statusCond)
	{
		if(statusCond == null)
		{
			return null;
		}
		return QOrder.order.status.eq(statusCond);
	}

	/**
	 * 패치 조인으로 Order+Member+Delivery 를 조회한다.
	 * @return
	 */
    public List<Order> findAllWithMemberDelivery()
	{
		return entityManager.createQuery(
				"select o from Order o" +
						" join fetch o.member m" +
						" join fetch o.delivery d", Order.class
		).getResultList();
    }

	/**
	 * DTO 로 데이터를 조회한다.
	 * @return
	 */
	public List<SimpleOrderDto> findOrderDtos()
	{
		return entityManager.createQuery(
				"select new jpabook.jpashop.api.SimpleOrderDto(o.id, m.name, o.orderDate, o.status, d.address)" +
						" from Order o" +
						" join o.member m" +
						" join o.delivery d", SimpleOrderDto.class
		).getResultList();
	}

    public List<Order> findAllWithItem()
	{
		return entityManager.createQuery(
				"select distinct o" +
						" from Order o" +
						" join fetch o.member m" +
						" join fetch o.delivery d" +
						" join fetch o.orderItems oi" +
						" join fetch oi.item i", Order.class).getResultList();
    }

	public List<Order> findAllWithMemberDelivery(int offset, int limit)
	{
		return entityManager.createQuery(
				"select o from Order o" +
						" join fetch o.member m" +
						" join fetch o.delivery d", Order.class)
				.setFirstResult(offset)
				.setMaxResults(limit)
				.getResultList();
	}
}
