package utility;

import model.City;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by deveshkandpal on 12/9/17.
 *
 * Utility class for getting base order of traversing cities.
 * This is just needed for testing purpose
 */
public class TestUtility {


    public static List<City> getBaseOrder(int len) {
        return IntStream.range(0, len)
                .mapToObj(index -> {
                    City city = new City((double)index,(double)index, "City-"+index, index);
                    return city;
                }).collect(Collectors.toList());
    }
}
