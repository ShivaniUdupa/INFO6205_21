package model;


import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


/**
 * Created by deveshkandpal on 12/9/17.
 */
public class CityTest {

    @Test
    public void testClassProperty() {
        City city = new City(100, 50, "City1", 0);

        assertThat(city, hasProperty("name", is("City1")));
        assertThat(city, hasProperty("x", is(100.0)));
        assertThat(city, hasProperty("y", is(50.0)));
        assertThat(city, hasProperty("id", is(0)));

    }
}
