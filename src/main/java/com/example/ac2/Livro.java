package com.example.ac2;

public class Livro {

    private String id;
    private String titulo;
    private String autor;
    private String genero;
    private int anoPublicacao;
    private String statusLeitura;
    private boolean favorito;

    // Construtor vazio: necessário para o Firebase
    public Livro() {
    }

    // Construtor com todos os atributos
    public Livro(String titulo, String autor, String genero,
                 int anoPublicacao, String statusLeitura,
                 boolean favorito) {

        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.anoPublicacao = anoPublicacao;
        this.statusLeitura = statusLeitura;
        this.favorito = favorito;
    }

    // Getters e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(int anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public String getStatusLeitura() {
        return statusLeitura;
    }

    public void setStatusLeitura(String statusLeitura) {
        this.statusLeitura = statusLeitura;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }
}