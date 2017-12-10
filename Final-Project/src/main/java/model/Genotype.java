package model;

import udf.Trifunction;
import udf.UserDefinedFunction;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Created by deveshkandpal on 12/6/17.
 */
public class Genotype implements Comparable<Genotype> {

    private String[] representation;
    private TraversalOrder phenotype;


    public Genotype(int memberId) {
        this.phenotype = new TraversalOrder(memberId);
    }
    public Genotype(int genotypeLength, int memberId) {
        this.representation = new String[genotypeLength];
        this.phenotype = new TraversalOrder(memberId);
    }

    public TraversalOrder getPhenotype() {
        return phenotype;
    }

    public void setPhenotype(TraversalOrder phenotype) {
        this.phenotype = phenotype;
    }

    public String[] getRepresentation() {
        return representation;
    }

    public void setRepresentation(String[] representation) {
        this.representation = representation;
    }

    // always pass a new copy to base order and not the reference,
    // create a copy as done in UDF map part
    public void generatePhenotype(Map<String, UserDefinedFunction> geneExprMapping, List<City> baseOrder) {

        transformation(0, this.representation.length, baseOrder, geneExprMapping);
        this.phenotype.setTraversalOrder(baseOrder);
        // compute fitness and complete rest of the stuff
        this.phenotype.computeFitnessScore();

    }

    public void transformation(int startIndex, int endIndex, List<City> baseOrder, Map<String, UserDefinedFunction> geneExprMapping) {
        if(startIndex == endIndex) {
            return;
        }else {
            String geneFuncId = representation[startIndex];
            int a = Integer.valueOf(representation[startIndex + 1]);
            int b= Integer.valueOf(representation[startIndex + 2]);

            Trifunction<List<City>, Integer, Integer, List<City>> geneExprFunc = (Trifunction<List<City>, Integer, Integer, List<City>>) geneExprMapping.get(geneFuncId);
            baseOrder = geneExprFunc.apply(baseOrder, a, b);
            transformation(startIndex + 3, endIndex, baseOrder, geneExprMapping);
        }
    }

    public int compareTo(Genotype other) {

        return other.phenotype.getFitnessScore() < this.phenotype.getFitnessScore() ? 1
                : (other.phenotype.getFitnessScore() == this.phenotype.getFitnessScore() ? 0 : -1);
    }

}
