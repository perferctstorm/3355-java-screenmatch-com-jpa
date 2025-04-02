package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=7661fe16";
    private List<DadosSerie> dadosSeries = new ArrayList<DadosSerie>();
    private SerieRepository repository;
    private List<Serie> series;
    Optional<Serie> serieBuscada;

    public Principal(SerieRepository repository) {
        this.repository = repository;
    }

    public void exibeMenu() {
        Integer opcao = -1;
        while(opcao != 0) {
            var menu = """
                    0 - Sair
                    1 - Buscar Séries
                    2 - Buscar Episódios
                    3 - Listar Séries
                    4 - Buscar Série por Nome
                    5 - Buscar Série por Nome do Ator
                    6 - 5 Séries Melhores Avaliadas
                    7 - Buscar Séries por Categoria
                    8 - Buscar Séries até Número Temporadas e Avaliação Maior ou Igual
                    9 - Buscar Episódio por Trecho
                    10 - Buscar Top 5 Episódios
                    11 - Buscar Episódios Lançados Após uma Data
                    """;
            System.out.println(menu);

            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:
                    filtrarSeriesPorTemporadaEAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    buscatTop5Episodios();
                    break;
                case 11:
                    buscarEpisodiosLancadosAPartirDe();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida\n");
            }
            System.out.println();
        }
    }

    private void buscarEpisodiosLancadosAPartirDe() {
        buscarSeriePorTitulo();
        if(serieBuscada.isPresent()){
            System.out.println("Informe uma data no formato DD/MM/YYYY: ");
            var dataStr = leitura.nextLine();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy") ;
            LocalDate data = LocalDate.parse(dataStr, formatter);

            List<Episodio> episodios = repository.episodiosLancadosAPartirDe(serieBuscada.get(), data);
            System.out.println(String.format("Lista dos episódios lançados após: ",
                    formatter.format(data)));
            episodios.forEach(e->
                    System.out.println(String.format("Título: %s, lançamento: %s, temporada: %d,  episódio: %d, avaliação: %.2f.",
                            e.getTitulo(), formatter.format(e.getDataLancamento()), e.getTemporada(), e.getNumeroEpisodio(), e.getAvaliacao()))
            );

        }

    }

    private void buscatTop5Episodios() {
        buscarSeriePorTitulo();
        if(serieBuscada.isPresent()){
            List<Episodio> episodios = repository.top5Episodios(serieBuscada.get());
            System.out.println("Lista dos 5 melhores episódios: ");
            episodios.forEach(e->
                    System.out.println(String.format("Título: %s, temporada: %d, episódio: %d, avaliação: %.2f.",
                            e.getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getAvaliacao()))
            );
        }
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Qual o nome do episódio para busca? ");
        var trecho = leitura.nextLine();

        List<Episodio> episodios = repository.episodioPorTrecho(trecho);
        System.out.println("");
        System.out.println(String.format("Lista de episódios cujo título encontramos a(s) palavra(s) %s: ",trecho));
        episodios.forEach(e -> {
            System.out.println(String.format("Episódio: %s, Série: %s, Temporada %d.",
                    e.getTitulo(), e.getSerie().getTitulo(), e.getTemporada()));
        });
    }

    private void filtrarSeriesPorTemporadaEAvaliacao() {
        System.out.println("Informe o Número Máximo de Temporadas: ");
        var temporadas = leitura.nextInt();
        System.out.println("Informe a Nota Mínima : ");
        var notaMinima = leitura.nextDouble();

        //List<Serie> series = repository.findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(temporadas, notaMinima);
        List<Serie> series = repository.seriesPorTemporadaEAvaliacao(temporadas, notaMinima);
        System.out.println(String.format("Séries com até %d temporadas e nota mínima de %.2f: ", temporadas, notaMinima));
        series.forEach(s->
                System.out.println(
                        String.format(" - %s, temporadas: %d, avaliação média:%.2f",
                                s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao())
                )
        );
    }

    private void buscarSeriePorCategoria() {
        System.out.println("Informe uma Categoria de Filme: ");
        var genero = leitura.nextLine();

        Categoria categoria = Categoria.fromPortugues(genero);
        List<Serie> series = repository.findByGenero(categoria);

        System.out.println(String.format("Séries da categoria %s: ", genero));
        series.forEach(s-> System.out.println(String.format(" - %s, avaliação média:%.2f", s.getTitulo(), s.getAvaliacao())));
    }

    private void buscarTop5Series() {
        List<Serie> series = repository.findTop5ByOrderByAvaliacaoDesc();
        System.out.println("5 séries melhores avaliadas: ");
        /*series.stream()
                .sorted(
                        Comparator.comparing(Serie::getAvaliacao).reversed()
                )
                .forEach(s-> System.out.println(String.format(" - %s, avaliação média:%.2f", s.getTitulo(), s.getAvaliacao())));
         */
        series.forEach(s-> System.out.println(String.format(" - %s, avaliação média:%.2f", s.getTitulo(), s.getAvaliacao())));
    }

    private void buscarSeriePorAtor() {
        System.out.println("Digite o nome do ator: ");
        var nomeAutor = leitura.nextLine();
        System.out.println("Agora, escolha uma nota de avaliação de corte: ");
        Double avaliacaoCorte = leitura.nextDouble();

        List<Serie> series = repository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAutor, avaliacaoCorte);

        System.out.println(String.format("Séries em que %s trabalhou: ", nomeAutor));
        series.forEach(s-> System.out.println(String.format(" - %s, avaliação média:%.2f", s.getTitulo(), s.getAvaliacao())));
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha a série pelo nome: ");
        String nomeSerie = leitura.nextLine();

        serieBuscada = repository.findByTituloContainingIgnoreCase(nomeSerie);

        if(serieBuscada.isPresent()){
            System.out.println(String.format("Dados da série %s.", serieBuscada.get()));
        }else{
            System.out.println("Série não encontrada.");
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        salvaSerieNoRepositorio(dados);
    }

    private Serie salvaSerieNoRepositorio(DadosSerie dados){
        Serie serie = new Serie(dados);
        System.out.println(dados);
        return repository.save(serie);
    };

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        DadosSerie dadosSerie = getDadosSerie();
        Optional<Serie> optSerie = repository.findByTituloContainingIgnoreCase(dadosSerie.titulo());
        Serie serie = optSerie.orElseGet(() -> salvaSerieNoRepositorio(dadosSerie));

        if(serie.getEpisodios().isEmpty()){
            List<DadosTemporada> temporadas = new ArrayList<>();
            for (int i = 1; i <= serie.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serie.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            //temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream().flatMap(t->t.episodios()
                    .stream().map(e-> new Episodio(t.numero(), e)))
                    .collect(Collectors.toList());
            episodios.stream().forEach(System.out::println);
            serie.setEpisodios(episodios);
            repository.save(serie);
        }else{
            serie.getEpisodios().stream().forEach(System.out::println);
        }
    }

    private void listarSeriesBuscadas(){
        series = repository.findAll();
        series.stream().sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }
}