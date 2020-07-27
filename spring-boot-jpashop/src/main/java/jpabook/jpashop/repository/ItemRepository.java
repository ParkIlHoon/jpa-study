package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository
{
	private final EntityManager entityManager;

	/**
	 * 상품을 저장한다.
	 * merge 방식
	 * 단점 : 엔티티의 모든 필드가 override 되어 update 된다. -> 의도치 않게 변경하지 않을 필드가 null로 변경될 수 있음.
	 * 변경감지 방식을 사용하는 것이 안전함.
	 * @param item 저장할 상품
	 */
	public void save(Item item)
	{
		// 신규 데이터
		if (item.getId() == null)
		{
			entityManager.persist(item);
		}
		// 기존 데이터
		else
		{
			entityManager.merge(item);
		}
	}

	/**
	 * 하나의 상품을 조회한다.
	 * @param id 조회할 상품의 아이디
	 * @return 아이디에 해당하는 상품
	 */
	public Item findOne(Long id)
	{
		return entityManager.find(Item.class, id);
	}

	/**
	 * 전체 상품 목록을 조회한다.
	 * @return 전체 상품 목록
	 */
	public List<Item> findAll()
	{
		return entityManager.createQuery("select i from Item i", Item.class).getResultList();
	}


}
