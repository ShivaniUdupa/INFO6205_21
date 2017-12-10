package core;

import model.City;
import model.Population;
import udf.TriFunction;
import udf.UserDefinedFunction;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Created by deveshkandpal on 12/5/17.
 */
public class Driver {

    public static void main(String[] args) {
        String[] bag = {"A", "B"};
        int phenoTypeLength = 10;
        int populationSize = 1000;
        int genoTypeLength = 4;
        double cutoff = 0.2;

        Map<String, UserDefinedFunction> geneExprMapping = getGeneExprMapping();
        List<City> baseOrder = getBaseOrder(phenoTypeLength);
        Population population = new Population(cutoff, geneExprMapping, baseOrder);
        population.initPopulation(populationSize,
                genoTypeLength,phenoTypeLength, bag);
        population.sortPopulation();
        System.out.println("Generation 0"  + " fitness score :" + population.getGtList().get(0).getPhenotype().toString());
        IntStream.range(1,11)
                .forEach(generationNo -> {
                    population.regenerationAndCulling();
                    population.sortPopulation();
                    System.out.println("Generation " +generationNo  + " fitness score :" + population.getGtList().get(0).getPhenotype().toString());
                });

    }

    /*
    *
    * Creates random city objects. The total number of city
    * objects created equals the phenoTypeLength
    *
    * */
    public static List<City> getBaseOrder(int phenoTypeLength) {
        Random r = new Random();
        double min = -100;
        double max = 100;
        return IntStream.range(0, phenoTypeLength)
                .mapToObj(index -> {
                    double x = ThreadLocalRandom.current().nextDouble(min, max + 1);
                    double y = ThreadLocalRandom.current().nextDouble(min, max + 1);

                    City city = new City(x, y, "City"+index , index);
                    return city;
                }).collect(Collectors.toList());

    }


    /*
    *
    * Creates a map of a gene sequence element which is mapped
    * to a lambda function. The lambda function applies the
    * shifting operation on the list and returns the new list.
    *
    * */
    public static Map<String, UserDefinedFunction> getGeneExprMapping() {

        Map<String, UserDefinedFunction> geneExprMapping = new HashMap<>();

        TriFunction<List<City>, Integer, Integer, List<City>> geneExprA = (newList, a, b) -> {

            int indexToSwapWith = (a + 3 ) % newList.size();
            City temp = newList.get(b);
            newList.set(b, newList.get(indexToSwapWith));
            newList.set(indexToSwapWith, temp);
            return newList;
        };


        TriFunction<List<City>, Integer, Integer, List<City>> geneExprB = (newList, a, b) -> {
            int indexToSwapWith = (b + 7 ) % newList.size();
            City temp = newList.get(a);
            newList.set(a, newList.get(indexToSwapWith));
            newList.set(indexToSwapWith, temp);
            return newList;

        };

        geneExprMapping.put("A", geneExprA);
        geneExprMapping.put("B", geneExprB);

        return geneExprMapping;

    }










}
