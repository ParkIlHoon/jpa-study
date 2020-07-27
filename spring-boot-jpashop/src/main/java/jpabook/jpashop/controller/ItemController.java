package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class ItemController
{
	private final ItemService itemService;

	@GetMapping("/items/new")
	public String createForm(Model model)
	{
		model.addAttribute("form", new BookForm());
		return "items/createItemForm";
	}

	@PostMapping("/items/new")
	public String createItem(BookForm bookForm)
	{
		Book book = new Book();
		book.setAuthor(bookForm.getAuthor());
		book.setIsbn(bookForm.getIsbn());
		book.setName(bookForm.getName());
		book.setPrice(bookForm.getPrice());
		book.setStockQuantity(bookForm.getStockQuantity());

		itemService.saveItem(book);

		return "redirect:/items";
	}

	@GetMapping("/items")
	public String itemList(Model model)
	{
		List<Item> allItems = itemService.findAllItems();
		model.addAttribute("items", allItems);
		return "items/itemList";
	}

	@GetMapping("/items/{itemId}/edit")
	public String updateItemForm(@PathVariable("itemId") Long itemId, Model model)
	{
		Book book = (Book) itemService.findOne(itemId);

		BookForm bookForm = new BookForm();
		bookForm.setId(book.getId());
		bookForm.setName(book.getName());
		bookForm.setPrice(book.getPrice());
		bookForm.setAuthor(book.getAuthor());
		bookForm.setStockQuantity(book.getStockQuantity());
		bookForm.setIsbn(book.getIsbn());

		model.addAttribute("form", bookForm);
		return "items/updateItemForm";
	}

	@PostMapping("/items/{itemId}/edit")
	public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") BookForm bookForm)
	{
		/*
		 * merge 방식
		Book book = new Book();
		book.setId(bookForm.getId());
		book.setAuthor(bookForm.getAuthor());
		book.setIsbn(bookForm.getIsbn());
		book.setName(bookForm.getName());
		book.setPrice(bookForm.getPrice());
		book.setStockQuantity(bookForm.getStockQuantity());

		itemService.saveItem(book);
		*/

		/*
		 * 변경 감지 방식
		 * DTO를 사용해도 괜찮아!
		 */
		itemService.updateItem(itemId, bookForm.getName(), bookForm.getPrice(), bookForm.getStockQuantity());

		return "redirect:/items";
	}
}
