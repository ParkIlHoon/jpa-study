package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
public abstract class Item
{
	@Id
	@GeneratedValue
	@Column(name = "item_id")
	private Long id;

	private String name;
	private int price;
	private int stockQuantity;

	@ManyToMany
	@JoinTable(name = "category_item", joinColumns = @JoinColumn(name = "item_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
	private List<Category> categories = new ArrayList();


	/*******************************************************************************************************************
	 * 비즈니스 로직
	 ******************************************************************************************************************/
	/**
	 * 재고 수량 증가
	 * @param quantity 증가량
	 */
	public void addStock(int quantity)
	{
		this.stockQuantity += quantity;
	}

	/**
	 * 재고 수량 감소
	 * @param quantity 감소량
	 */
	public void removeStock(int quantity)
	{
		int restStock = this.stockQuantity - quantity;
		if (restStock < 0)
		{
			throw new NotEnoughStockException("need more stock");
		}
		this.stockQuantity = restStock;
	}
}
