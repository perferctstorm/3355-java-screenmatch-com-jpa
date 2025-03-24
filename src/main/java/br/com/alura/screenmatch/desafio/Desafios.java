package br.com.alura.screenmatch.desafio;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class Desafios {
    public static void main(String args[]){
        //1 - Imagine que você tem uma lista de strings. Algumas das strings são números, mas outras não.
        // Queremos converter a lista de strings para uma lista de números. Se a conversão falhar,
        // você deve ignorar o valor. Por exemplo, na lista a seguir, a saída deve ser [10, 20]:

        System.out.println("Questão 1: ");
        List<String> input = Arrays.asList("10", "abc", "20", "30x");
        input
                .stream()
                .filter(s->s.matches("\\d+"))
                .map(s->Integer.valueOf(s))
                .forEach(System.out::println);
        System.out.println();

        System.out.println("Questão 2: ");
        //2 - Implemente um método que recebe um número inteiro dentro de um Optional.
        // Se o número estiver presente e for positivo, calcule seu quadrado. Caso contrário, retorne Optional.empty.

        OptionalInt optional = OptionalInt.empty();
        optional.ifPresentOrElse (n-> System.out.println(n*n), ()-> System.out.println("Empty"));
        System.out.println();
    }
}
