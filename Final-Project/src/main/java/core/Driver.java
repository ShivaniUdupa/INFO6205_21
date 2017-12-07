package core;

import model.City;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Created by deveshkandpal on 12/5/17.
 */
public class Driver {

    public static void main(String[] args) {

        Map<Character, BiFunction<Integer, Integer, List<City>>> commands = new HashMap<>();

        List<City> baseOrder = null;

        BiFunction<Integer, Integer, List<City>> aFunc = (a,b) -> {
            List<City> newList = new ArrayList<>();
            baseOrder.stream().forEach(el -> newList.add(el));
            //int randomNum = ThreadLocalRandom.current().nextInt(0, newList.size());
            int indexToSwapWith = (a + 3 ) % newList.size();
            City temp = newList.get(b);
            newList.set(b, newList.get(indexToSwapWith));
            newList.set(indexToSwapWith, temp);
            return newList;
        };

        Function<Integer, Function<Integer, List<City>>> bFunc = (a) ->  b -> {
            List<City> newList = new ArrayList<>();
            baseOrder.stream().forEach(el -> newList.add(el));
            int indexToSwapWith = (b + 13 ) % newList.size();
            City temp = newList.get(a);
            newList.set(a, newList.get(indexToSwapWith));
            newList.set(indexToSwapWith, temp);
            return newList;
        };

        List<City> ab = aFunc.apply(2,3);












    }






}
