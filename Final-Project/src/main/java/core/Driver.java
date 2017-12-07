package core;

import model.City;
import model.Population;
import udf.Trifunction;
import udf.UserDefinedFunction;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Created by deveshkandpal on 12/5/17.
 */
public class Driver {

    public static void main(String[] args) {
        String[] bag = {"A", "B"};
        int phenoTypeLength = 4;
        int populationSize = 10;
        int genoTypeLength = 8;

        Map<String, UserDefinedFunction> geneExprMapping = getGeneExprMapping();
        List<City> baseOrder = getBaseOrder(phenoTypeLength);
        Population population = new Population(0.1, geneExprMapping, baseOrder);
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



    public static Map<String, UserDefinedFunction> getGeneExprMapping() {

        Map<String, UserDefinedFunction> geneExprMapping = new HashMap<>();

        Trifunction<List<City>, Integer, Integer, List<City>> geneExprA = (newList, a, b) -> {
            //List<City> newList = new ArrayList<>();
            //to.stream().forEach(el -> newList.add(el));
            int indexToSwapWith = (a + 3 ) % newList.size();
            City temp = newList.get(b);
            newList.set(b, newList.get(indexToSwapWith));
            newList.set(indexToSwapWith, temp);
            return newList;
        };



        Trifunction<List<City>, Integer, Integer, List<City>> geneExprB = (newList, a, b) -> {
            //List<City> newList = new ArrayList<>();
            //to.stream().forEach(el -> newList.add(el));
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
