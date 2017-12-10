package model;

import core.Driver;
import org.junit.Test;
import udf.UserDefinedFunction;
import utility.TestUtility;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by deveshkandpal on 12/9/17.
 */
public class PopulationTest {

    @Test
    public void testPopulationLogic() {


        int phenoTypeLength = 4;

        Genotype genotype = new Genotype(0);
        Genotype genotype2 = new Genotype(1);
        Map<String, UserDefinedFunction> geneExprMapping = Driver.getGeneExprMapping();
        List<City> baseOrder = TestUtility.getBaseOrder(phenoTypeLength);
        List<City> baseOrder2 = TestUtility.getBaseOrder(phenoTypeLength);

        genotype.setRepresentation(new String[]{"A","0","1","B","0","2"});
        genotype.generatePhenotype(geneExprMapping, baseOrder);
        genotype.getPhenotype().computeFitnessScore();

        genotype2.setRepresentation(new String[]{"B","0","0","B","1","2"});
        genotype2.generatePhenotype(geneExprMapping, baseOrder2);
        genotype2.getPhenotype().computeFitnessScore();

        Population population = new Population(0.1, geneExprMapping, baseOrder);
        population.getGtList().add(genotype);
        population.getGtList().add(genotype2);
        population.sortPopulation();

        Genotype child = population.crossover(0,1,0);

        assertThat(population.getGtList(), hasSize(2));
        assertThat(population.getGtList().get(0), is(genotype));
        assertThat(population.getGtList().get(0).getPhenotype().getDistance(), is(11.31370849898476));
        assertThat(population.getGtList().get(0).getPhenotype().getFitnessScore(), is(0.08838834764831845));
        assertThat(Arrays.toString(child.getRepresentation()), is("[A, 0, 1, B, 1, 2]"));


    }
}
