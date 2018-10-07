package org.ufsc.si.livraria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.ufsc.si.livraria.model.Book;
import org.ufsc.si.livraria.model.User;
import org.ufsc.si.livraria.service.BookService;

@Controller
@RequestMapping("/livraria")
public class IndexController {
	
	@Autowired
	private BookService bookService;

	@RequestMapping(value= {"", "/"})
	public ModelAndView index() {
		return new ModelAndView("index");
	}
	
	@RequestMapping("/books-by-user")
	public @ResponseBody List<User> listUsers() {
		bookService.listBooks();
		return bookService.listUsers();
	}
	
	@RequestMapping("/books-by-category")
	public @ResponseBody List<Book> listBooks() {
		return bookService.listBooks();
	}
}
