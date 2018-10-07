package org.ufsc.si.livraria.service;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.stereotype.Service;
import org.ufsc.si.livraria.helper.OntologyHelper;
import org.ufsc.si.livraria.model.Book;
import org.ufsc.si.livraria.model.User;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

;

@Service
public class BookService {

    private final String ONTOLOGY_PATH = this.getClass().getResource("/ontology/livraria.owl").getPath();
    private static final String USUARIO = "Usuario";
    private static final String LIVRO = "Livro";
    private static final String ALUGOU = "alugou";

    private OntologyHelper helper;

    @PostConstruct
    public void loadOntology() throws OWLOntologyCreationException {
        helper = new OntologyHelper(ONTOLOGY_PATH);
    }

    public Map<OWLNamedIndividual, Set<OWLNamedIndividual>> getRecomendations() {
        List<OWLNamedIndividual> usuarios = helper.getIndividualsOf(USUARIO);
        Map<OWLNamedIndividual, String> mostReadCategoryByUser = fetchMostReadCategoryByUser();
        Map<OWLNamedIndividual, List<OWLObjectPropertyAssertionAxiom>> alugueisPorUsuario = helper.mapAxioms(usuarios, ALUGOU);
        Map<OWLNamedIndividual, Set<OWLNamedIndividual>> recomendations = new HashMap<>();

        usuarios.forEach(usuario -> {
            String category = mostReadCategoryByUser.get(usuario);
            Map<OWLNamedIndividual, String> booksOfCategory = fetchBooksOfCategory(category);

            Set<OWLNamedIndividual> booksOfCategorySet = booksOfCategory.keySet();

            List<OWLObjectPropertyAssertionAxiom> alugueis = alugueisPorUsuario.get(usuario);

            List<OWLIndividual> livrosAlugados = new ArrayList<>();
            alugueis.forEach(entry -> livrosAlugados.add(entry.getObject()));


            Set<OWLNamedIndividual> recommendedBooks = new HashSet<>(booksOfCategorySet);
            for (OWLIndividual book : booksOfCategorySet) {
                livrosAlugados.forEach(livroAlugado -> {
                    if (livroAlugado.toString().equals(book.toString())) {
                        recommendedBooks.remove(book);
                    }
                });
            }

            recomendations.put(usuario, recommendedBooks);
        });

        return recomendations;
    }

    public Map<OWLNamedIndividual, Map<String, Integer>> matchUsersPreferredCategories() {
        List<OWLNamedIndividual> usuarios = helper.getIndividualsOf(USUARIO);
        List<OWLNamedIndividual> livros = helper.getIndividualsOf(LIVRO);

        Map<OWLNamedIndividual, List<OWLObjectPropertyAssertionAxiom>> alugueisPorUsuario = helper.mapAxioms(usuarios, ALUGOU);
        Map<OWLNamedIndividual, String> categoriasPorLivro = helper.mapCategorias(livros);

        Map<OWLNamedIndividual, Map<String, Integer>> categoriaPreferidaPorUsuario = new HashMap<>();

        alugueisPorUsuario.forEach((usuario, alugueis) -> {
            categoriaPreferidaPorUsuario.put(usuario, new HashMap<>());

            alugueis.forEach(aluguel -> {
                String categoria = categoriasPorLivro.get(aluguel.getObject());

                Map<String, Integer> countPorCategoria = categoriaPreferidaPorUsuario.get(usuario);

                if (countPorCategoria.containsKey(categoria)) {
                    countPorCategoria.put(categoria, countPorCategoria.get(categoria) + 1);
                } else {
                    countPorCategoria.put(categoria, 1);
                }
            });
        });

        return categoriaPreferidaPorUsuario;
    }

    public Map<OWLNamedIndividual, String> fetchMostReadCategoryByUser() {
        List<OWLNamedIndividual> usuarios = helper.getIndividualsOf(USUARIO);
        Map<OWLNamedIndividual, Map<String, Integer>> usersPreferredCategories = matchUsersPreferredCategories();
        Map<OWLNamedIndividual, String> preferredCategoryByUser = new HashMap<>();

        usuarios.forEach(usuario -> {
            Map<String, Integer> preferredCategories = usersPreferredCategories.get(usuario);

            String preferredCategory = "";
            Integer preferredCategoryReadingCount = 0;

            for (String category : preferredCategories.keySet()) {
                Integer count = preferredCategories.get(category);
                if (count > preferredCategoryReadingCount) {
                    preferredCategoryReadingCount = count;
                    preferredCategory = category;
                }
            }

            preferredCategoryByUser.put(usuario, preferredCategory);
        });

        return preferredCategoryByUser;
    }

    public Map<OWLNamedIndividual, String> fetchBooksOfCategory(String category) {
        List<OWLNamedIndividual> livros = helper.getIndividualsOf(LIVRO);
        Map<OWLNamedIndividual, String> categoriasPorLivro = helper.mapCategorias(livros);

        return categoriasPorLivro
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(category))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public List<User> listUsers() {
        List<OWLNamedIndividual> usuarios = helper.getIndividualsOf(USUARIO);
        Map<OWLNamedIndividual, List<OWLObjectPropertyAssertionAxiom>> alugueisPorUsuario = helper.mapAxioms(usuarios, ALUGOU);
        List<User> users = new ArrayList<>();
        alugueisPorUsuario.forEach((u, a) -> {
            User user = new User();
            user.setName(u.getIRI().getShortForm());
            List<Book> books = new ArrayList<>();
            a.forEach(l -> {
                String title = l.getObject().toString();
                System.out.println();

                Book book = new Book();
                book.setTitle(title.substring(title.indexOf("#") + 1, title.length() - 1));
                books.add(book);
            });
            user.setBooks(books);
            users.add(user);
        });
        return users;
    }

    public List<Book> listBooks() {
        List<OWLNamedIndividual> livros = helper.getIndividualsOf(LIVRO);
        Map<OWLNamedIndividual, String> livrosPorCategoria = helper.mapCategorias(livros);
        List<Book> books = new ArrayList<>();
        livrosPorCategoria.forEach((i, c) -> {
            Book book = new Book();
            book.setTitle(i.getIRI().getShortForm());
            String category = c.substring(1);
            book.setCategory(category.substring(0, category.indexOf("\"")));
            books.add(book);
        });
        return books;
    }
}
