package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {
    @Autowired
    private SerieRepository repositorio;

    public List<SerieDTO> obterTodasAsSeries(){
        return converteDados(repositorio .findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return converteDados ( repositorio.findTop5ByOrderByAvaliacaoDesc() );
    }

    public List<SerieDTO> obterLancamentos() {
        return converteDados(repositorio.encontrarEpisodiosMaisRecentes());
    }

    private List<SerieDTO> converteDados(List<Serie> series){
        return series.stream()
                .map(s-> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(),
                        s.getAvaliacao(), s.getGenero(), s.getAtores(),s.getPoster(),s.getSinopse()))
                .collect(Collectors.toList());
    }

    public SerieDTO obterPorId(Long id) {
        Optional<Serie> serie = repositorio.findById(id);
        if(serie.isPresent()){
            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(),
                    s.getAvaliacao(), s.getGenero(), s.getAtores(),s.getPoster(),s.getSinopse());
        }
        return null;
    }

    public List<EpisodioDTO> obterTemporadas(Long id) {
        Optional<Serie> serie = repositorio.findById(id);
        if(serie.isPresent()){
            List<Episodio> episodios = serie.get().getEpisodios();
            return episodios
                    .stream()
                    .map(e-> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroEpisodio()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodioDTO> obterTemporada(Long id, Long numero) {
        List<Episodio> episodios = repositorio.obterTemporada(id, numero);
        return episodios.stream()
                .map(e-> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroEpisodio()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obterSeries(String genero) {
        Categoria categoria = Categoria.fromPortugues(genero);

        List<Serie> series
                = repositorio.findByGenero(categoria);
        return converteDados(series);
    }

    public List<EpisodioDTO> obterTop5Episodios(Long id) {
        Optional<Serie> serie = repositorio.findById(id);

        if(serie.isPresent()) {
            List<EpisodioDTO> episodioDTOS =
                    repositorio.top5Episodios(serie.get())
                            .stream()
                            .map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroEpisodio()))
                            .collect(Collectors.toList());
            return episodioDTOS;
        }
        return null;
    }
}
