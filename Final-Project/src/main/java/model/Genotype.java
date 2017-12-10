package model;

import udf.TriFunction;
import udf.UserDefinedFunction;

import java.util.List;
import java.util.Map;

/**
 * Created by deveshkandpal on 12/6/17.
 */
public class Genotype implements Comparable<Genotype> {

    private String[] representation;
    private Phenotype phenotype;


    public Genotype(int memberId) {
        this.phenotype = new Phenotype(memberId);
    }
    public Genotype(int genotypeLength, int memberId) {
        this.representation = new String[genotypeLength];
        this.phenotype = new Phenotype(memberId);
    }

    public Phenotype getPhenotype() {
        return phenotype;
    }

    public void setPhenotype(Phenotype phenotype) {
        this.phenotype = phenotype;
    }

    public String[] getRepresentation() {
        return representation;
    }

    public void setRepresentation(String[] representation) {
        this.representation = representation;
    }



    /*
    * Given GeneExpressionMapping a Base Order
    * defined for traversing all the cities and
    * genotype sequence (eg : A 0 1 B 2 3 B 4 5 A 1 2), we
    * generate a Phenotype against this sequence
    *
    * */
    public void generatePhenotype(Map<String, UserDefinedFunction> geneExprMapping, List<City> baseOrder) {

        transformation(0, this.representation.length, baseOrder, geneExprMapping);
        this.phenotype.setTraversalOrder(baseOrder);
        // compute fitness and complete rest of the stuff
        this.phenotype.computeFitnessScore();

    }

    /*
    * Recursively goes through the entire genotype sequence
    * and gets the function stored in geneExprMapping and
    * gets the new phenotype
    *
    * */
    public void transformation(int startIndex, int endIndex, List<City> baseOrder, Map<String, UserDefinedFunction> geneExprMapping) {
        if(startIndex == endIndex) {
            return;
        }else {
            String geneFuncId = representation[startIndex];
            int a = Integer.valueOf(representation[startIndex + 1]);
            int b= Integer.valueOf(representation[startIndex + 2]);

            TriFunction<List<City>, Integer, Integer, List<City>> geneExprFunc = (TriFunction<List<City>, Integer, Integer, List<City>>) geneExprMapping.get(geneFuncId);
            baseOrder = geneExprFunc.apply(baseOrder, a, b);
            transformation(startIndex + 3, endIndex, baseOrder, geneExprMapping);
        }
    }

    /*
    *
    * CompareTo function defined to compare two genotypes
    * on basis of their fitness score
    * */
    public int compareTo(Genotype other) {

        return other.phenotype.getFitnessScore() < this.phenotype.getFitnessScore() ? 1
                : (other.phenotype.getFitnessScore() == this.phenotype.getFitnessScore() ? 0 : -1);
    }

}
