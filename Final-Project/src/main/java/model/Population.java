package model;

import udf.UserDefinedFunction;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by deveshkandpal on 12/6/17.
 */
public class Population {

    private List<Genotype> gtList;
    private double cutoff;
    private Map<String, UserDefinedFunction> geneExprMapping;
    private List<City> baseOrder;


    /*
    *
    * Comparator defined to compare and sort
    * all genotypes in a population stored in
    * gtList (genotypeList)
    *
    * */
    public Comparator<Genotype> genoTypeComparator
            = new Comparator<Genotype>() {

        public int compare(Genotype gA, Genotype gB) {
            return gB.compareTo(gA);
        }

    };

    public Population(double cutoff, Map<String, UserDefinedFunction> geneExprMapping,
                      List<City> baseOrder) {
        this.gtList = new ArrayList<>();
        this.cutoff = cutoff;
        this.baseOrder = baseOrder;
        this.geneExprMapping = geneExprMapping;

    }

    public List<Genotype> getGtList() {
        return gtList;
    }

    public void setGtList(List<Genotype> gtList) {
        this.gtList = gtList;
    }


    /*This takes a given population size, genotypeLength, phenotypeLength and
    * a geneExprBag (an array to choose from different gene sequences A,B etc)
    * and randomly creates the first generation of genotype population
    * Thus each each genotype is characterised with a sequence that looks quite similar to :
     * A 0 1 B 3 4 B 1 3 A 63
    * */
    public void initPopulation(int populationSize, int genotypeLength,
                                int phenoTypeLength,
                                String[] geneExprBag) {



        Random r = new Random();
        this.gtList = IntStream.range(0, populationSize).mapToObj(p -> new Genotype(genotypeLength * 3, p)).collect(Collectors.toList());

        this.gtList.stream().forEach(genotype -> {

            IntStream.range(0, genotypeLength * 3).forEach(index -> {
                if(index % 3 == 0) {
                    // pick a random alphabet
                    int randInt = r.nextInt((geneExprBag.length));
                    String geneExpr = geneExprBag[randInt];
                    genotype.getRepresentation()[index] = geneExpr;
                } else {
                    // pick a random integer in the range of phenotype length
                    int randInt = r.nextInt((phenoTypeLength));
                    genotype.getRepresentation()[index] = String.valueOf(randInt);

                }
            });

        });

        this.gtList.stream().forEach(genotype -> {
            List<City> newBaseOrder = new ArrayList<>();
            baseOrder.stream().forEach(bo -> newBaseOrder.add(bo));
            genotype.generatePhenotype(geneExprMapping, newBaseOrder);
        });
    }

    public void sortPopulation() {
        Collections.sort(this.gtList, this.genoTypeComparator);
    }


    /*
    *
    * This method takes 80% - 90% of the sorted
    * genotype population from the current generation
    * to create a new population of genotypes. The children
    * created are created as a result of cross over of two randomly
    * selected parents from the current generation
    *
    * */

    public void regenerationAndCulling() {
        Random r = new Random();
        int upperbound = (int)((1 - this.cutoff) * this.gtList.size());

        List<Genotype> newGeneration = IntStream.range(0, this.gtList.size())
                .mapToObj(index -> {

                    int firstParent = getRandomParentIndex(upperbound, r);
                    int secondParent = getRandomParentIndex(upperbound, r);

                    while(firstParent == secondParent) {
                        secondParent = getRandomParentIndex(upperbound, r);
                    }
                    Genotype child = crossover(firstParent, secondParent, index);
                    return child;
                }).collect(Collectors.toList());


        this.gtList = newGeneration;

    }


    /*
    *
    * Given Parent1 Genotype and Parent2 Genotype, first half of the
    * child gene sequence comes from Parent1 and the other half comes
    * from Parent2
    *
    * */
    public Genotype crossover(int firstParent, int secondParent, int newMemberId) {
        Genotype genoFirst = this.gtList.get(firstParent);
        Genotype genoSecond = this.gtList.get(secondParent);
        String[] childRepresentation = new String[genoFirst.getRepresentation().length];
        IntStream.range(0, childRepresentation.length)
                .forEach(index -> {
                    if(index < childRepresentation.length / 2) {
                        childRepresentation[index] = genoFirst.getRepresentation()[index];
                    } else {
                        childRepresentation[index] = genoSecond.getRepresentation()[index];
                    }
                });

        Genotype child = new Genotype(newMemberId);
        child.setRepresentation(childRepresentation);
        child.generatePhenotype(this.geneExprMapping, this.baseOrder);
        return child;
    }

    private int getRandomParentIndex(int upperbound, Random r) {

        int randInt = r.nextInt((upperbound));
        return randInt;
    }






}
