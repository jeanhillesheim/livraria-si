package org.ufsc.si.livraria.model;

import java.util.List;

public class User {

    private String name;

    private List<Book> booksRead;

    private List<Book> recomendations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public List<Book> getBooksRead() {
		return booksRead;
	}

	public void setBooksRead(List<Book> booksRead) {
		this.booksRead = booksRead;
	}

	public List<Book> getRecomendations() {
		return recomendations;
	}

	public void setRecomendations(List<Book> recomendations) {
		this.recomendations = recomendations;
	}

}
