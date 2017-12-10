package model;

import core.Driver;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;
import udf.UserDefinedFunction;
import utility.TestUtility;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Created by deveshkandpal on 12/9/17.
 * GenotypeTest class to validate all logic associated with a genotype
 *such as phenotype mapping, distance calculation as well as calculation of fitness score
 *
 *
 */
public class GenotypeTest {


    @Test
    public void testClassProperty() {

        int memberId = 0;
        int phenoTypeLength = 3;
        Genotype genotype = new Genotype(memberId);
        Map<String, UserDefinedFunction> geneExprMapping = Driver.getGeneExprMapping();
        List<City> baseOrder = TestUtility.getBaseOrder(phenoTypeLength);
        genotype.setRepresentation(new String[]{"A","0","1","B","1","2"});
        genotype.getPhenotype().setTraversalOrder(baseOrder);
        genotype.getPhenotype().computeFitnessScore();


        assertThat(baseOrder, hasSize(3));
        assertThat(baseOrder, not(IsEmptyCollection.empty()));
        assertThat(genotype.getPhenotype().getTraversalOrder().get(0),
                hasProperty("name", is("City-0")));
        assertThat(genotype.getPhenotype(),
                hasProperty("distance", is(5.656854249492381)));
        assertThat(genotype.getPhenotype(),
                hasProperty("fitnessScore", is(0.17677669529663687)));

        assertThat(genotype.getPhenotype().pairWiseDistance(new City(1.0,2.0,"",0),
                new City(4.0,5.0,"",1)), is(4.242640687119285));



    }






}
