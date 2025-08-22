package com.exemplo.entitys;

import java.io.Serializable;

public class Livro implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String isbn; //é um número padrão para identificação de publicações monográficas
    private final String titulo;
    private final String autor;
    private final int anoPublicacao;
    private boolean disponivel;

    public Livro(String isbn, String titulo, String autor, int anoPublicacao) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.anoPublicacao = anoPublicacao;
        this.disponivel = true;
    }

    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getAnoPublicacao() { return anoPublicacao; }
    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }

    @Override
    public String toString() {
        return "ISBN: " + isbn + "| Título: " + titulo + "| Autor: " + autor +
                "| Ano: " + anoPublicacao + "| Disponível: " + (disponivel ? "Sim" : "Não");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Livro livro = (Livro) obj;
        return isbn.equals(livro.isbn);
    }

    @Override
    public int hashCode() {
        return isbn.hashCode();
    }
}