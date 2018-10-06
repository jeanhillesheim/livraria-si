package org.ufsc.si.livraria.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.stereotype.Service;
import org.ufsc.si.livraria.helper.OntologyHelper;
import org.ufsc.si.livraria.model.Book;
import org.ufsc.si.livraria.model.User;;

@Service
public class BookService {

    private final String ONTOLOGY_PATH =  this.getClass().getResource("/ontology/livraria.owl").getPath();
    private static final String USUARIO = "Usuario";
    private static final String LIVRO = "Livro";
    private static final String ALUGOU = "alugou";
	
	private OntologyHelper helper;
	
	@PostConstruct
	public void loadOntology() throws OWLOntologyCreationException {
		helper = new OntologyHelper(ONTOLOGY_PATH); 
	}

	public List<User> list() {
		
		List<OWLNamedIndividual> usuarios = helper.getIndividualsOf(USUARIO);
        List<OWLNamedIndividual> livros = helper.getIndividualsOf(LIVRO);

        Map<OWLNamedIndividual, List<OWLObjectPropertyAssertionAxiom>> alugueisPorUsuario = helper.mapAxioms(usuarios, ALUGOU);
        
        List<User> users = new ArrayList<>();
        alugueisPorUsuario.forEach((u, a) -> {
        		User user = new User();
        		user.setName(u.getIRI().getShortForm());
        		List<Book> books = new ArrayList<>();
        		a.forEach(l -> {
        			Book book = new Book();
        			book.setTitle(l.getObject().toString());
        			books.add(book);
        		});
        		user.setBooks(books);
        		users.add(user);
        });
        
        
        
        
        
//        Map<OWLNamedIndividual, String> livrosPorCategoria = helper.mapCategorias(livros);
		
		
		
		
//        Map<OWLNamedIndividual, List<OWLObjectPropertyAssertionAxiom>> booksByAuthor = getBooksByAuthor(term);
//        List<Book> books = new ArrayList<>();
//        booksByAuthor.forEach((a, b) -> {
//        		Book book = new Book();
//        		book.setAuthor(a.getIRI().getShortForm());
//        		book.setTitle(b.get(0).getObject().toString());
//        		books.add(book);
//        });
//		return books;
		return users;
	}
}