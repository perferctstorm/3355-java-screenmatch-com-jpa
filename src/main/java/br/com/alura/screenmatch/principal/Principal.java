package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

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
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida\n");
            }
            System.out.println();
        }
    }

    private void filtrarSeriesPorTemporadaEAvaliacao() {
        System.out.println("Informe o Número Máximo de Temporadas: ");
        var temporadas = leitura.nextInt();
        System.out.println("Informe a Nota Mínima : ");
        var notaMinima = leitura.nextDouble();

        List<Serie> series = repository.findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(temporadas, notaMinima);
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

        Optional<Serie> serieBuscada = repository.findByTituloContainingIgnoreCase(nomeSerie);

        if(serieBuscada.isPresent()){
            System.out.println(String.format("Dados da série %s.", serieBuscada.get()));
        }else{
            System.out.println("Série não encontrada.");
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repository.save(serie);
        System.out.println(dados);
    }

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
        System.out.println("Escolha a série pelo nome: ");
        String nomeSerie = leitura.next();

        Optional<Serie> serie = repository.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serie.get().getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serie.get().getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            //temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream().flatMap(t->t.episodios()
                    .stream().map(e-> new Episodio(t.numero(), e)))
                    .collect(Collectors.toList());
            episodios.stream().forEach(System.out::println);
            serie.get().setEpisodios(episodios);
            repository.save(serie.get());
        }else{
            System.out.println("Série não encontrada.");
        }
    }

    private void listarSeriesBuscadas(){
        series = repository.findAll();
        series.stream().sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }
}