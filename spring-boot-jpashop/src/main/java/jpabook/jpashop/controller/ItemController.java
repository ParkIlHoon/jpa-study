package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
}
