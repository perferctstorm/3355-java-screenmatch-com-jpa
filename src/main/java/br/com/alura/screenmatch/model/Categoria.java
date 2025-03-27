package br.com.alura.screenmatch.model;

public enum Categoria {
    ACAO("Action","Ação"),
    ROMANCE("Romance","Romance"),
    COMEDIA("Comedy","Comédia"),
    DRAMA("Drama","Drama"),
    CRIME("Crime", "Crime"),
    ANIMACAO("Animation","Animação" ),
    DOCUMENTARIO("Documentary", "Documentário");

    private String categoriaOmdb;
    private String categoriaPortugues;

    Categoria(String categoriaOmdb, String categoriaPortugues){
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaPortugues = categoriaPortugues;
    }

    public static Categoria fromString(String texto){
        for(Categoria categoria : Categoria.values()){
            if(categoria.categoriaOmdb.equalsIgnoreCase(texto))
                return categoria;
        }

        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + texto);
    }

    public static Categoria fromPortugues(String texto){
        for(Categoria categoria : Categoria.values()){
            if(categoria.categoriaPortugues.equalsIgnoreCase(texto))
                return categoria;
        }

        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + texto);
    }
}
