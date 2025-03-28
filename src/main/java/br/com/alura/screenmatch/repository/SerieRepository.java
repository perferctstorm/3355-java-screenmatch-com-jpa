package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAutor, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(Integer maxTemporadas, Double avaliacao);

    @Query(value = "select s from Serie s where totalTemporadas<=:maxTemporadas and avaliacao>=:avaliacao")
    List<Serie> seriesPorTemporadaEAvaliacao(Integer maxTemporadas, Double avaliacao);

    @Query("SELECT e FROM Serie s INNER JOIN s.episodios e WHERE e.titulo ILIKE %:trecho%")
    List<Episodio> episodioPorTrecho(String trecho);

    @Query("SELECT e FROM Episodio e WHERE e.serie = :serie ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> top5Episodios(Serie serie);

    @Query("SELECT e FROM Episodio e WHERE e.serie = :serie AND e.dataLancamento >= :data")
    List<Episodio> episodiosLancadosAPartirDe(Serie serie, LocalDate data);
}
