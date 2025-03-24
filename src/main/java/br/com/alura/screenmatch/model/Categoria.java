package br.com.alura.screenmatch.model;

public enum Categoria {
    ACAO("Action"),
    ROMANCE("Romance"),
    COMEDIA("Comedy"),
    DRAMA("Drama"),
    CRIME("Crime"),
    ANIMACAO("Animation"),
    DOCUMENTARIO("Documentary");

    private String categoriaOmdb;

    Categoria(String categoriaOmdb){
        this.categoriaOmdb = categoriaOmdb;
    }

    public static Categoria fromString(String texto){
        for(Categoria categoria : Categoria.values()){
            if(categoria.categoriaOmdb.equalsIgnoreCase(texto))
                return categoria;
        }

        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + texto);
    }
}
