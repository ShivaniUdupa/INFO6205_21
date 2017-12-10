package core;

import actors.MasterActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import model.City;
import org.apache.log4j.Logger;
import udf.Trifunction;
import udf.UserDefinedFunction;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by deveshkandpal on 12/9/17.
 */
public class ActorDriver {

    final static Logger logger = Logger.getLogger(ActorDriver.class);

    public static void main(String[] args) throws Exception {


        String[] geneExprBag = {"A","B"};
        int phenoTypeLength = 6;
        int populationSize = (6 * 5  * 3 * 2);
        int genoTypeLength = 6;
        double cutoff = 0.2;

        Map<String, UserDefinedFunction> geneExprMapping = getGeneExprMapping();
        List<City> baseOrder = getBaseOrder(phenoTypeLength);
        int stopGeneration = 10;

        logger.debug("======================================");
        logger.info("Parameters <Phenotype Length :" + phenoTypeLength + ">" +
                "<Population Size :" + populationSize +
                ">" + "<Genotype Length :" + genoTypeLength + ">" +
        "< Culling Cutoff :" + cutoff + ">");
        logger.debug("======================================");
        final ActorSystem system = ActorSystem.create("GA-System");

        final ActorRef masterActor =
                system.actorOf(MasterActor.props(
                        geneExprBag , phenoTypeLength , populationSize,genoTypeLength ,
                        cutoff,geneExprMapping,baseOrder,stopGeneration),
                        "MasterActor");

        masterActor.tell(new MasterActor.Init(), ActorRef.noSender());

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
            int indexToSwapWith = (a + 3 ) % newList.size();
            City temp = newList.get(b);
            newList.set(b, newList.get(indexToSwapWith));
            newList.set(indexToSwapWith, temp);
            return newList;
        };



        Trifunction<List<City>, Integer, Integer, List<City>> geneExprB = (newList, a, b) -> {
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
