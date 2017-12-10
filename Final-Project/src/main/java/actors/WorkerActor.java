package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import model.City;
import model.Genotype;
import org.apache.log4j.Logger;
import udf.UserDefinedFunction;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by deveshkandpal on 12/9/17.
 */
public class WorkerActor extends AbstractActor {

    final static Logger logger = Logger.getLogger(WorkerActor.class);

    static public Props props() {
        return Props.create(WorkerActor.class, () -> new WorkerActor());
    }


    static public class CreateGenotype {

        public final int memberId;
        public final int genotypeLength;
        public final Map<String, UserDefinedFunction> geneExprMapping;
        public final int phenoTypeLength;
        public final String[] geneExprBag;
        public final List<City> baseOrder;

        public CreateGenotype(int memberId,
                              int genotypeLength,
                              Map<String, UserDefinedFunction> geneExprMapping,
                              int phenoTypeLength,
                              String[] geneExprBag,
                              List<City> baseOrder) {
            this.memberId = memberId;
            this.genotypeLength = genotypeLength;
            this.geneExprMapping = geneExprMapping;
            this.phenoTypeLength = phenoTypeLength;
            this.geneExprBag = geneExprBag;
            this.baseOrder = baseOrder;


        }


    }

    static public class RegenerateGenotype {

        public final int upperBound;
        public final List<Genotype> parentGeneration;
        public final int memberId;
        public final Map<String, UserDefinedFunction> geneExprMapping;
        public final List<City> baseOrder;

        public RegenerateGenotype(
                int upperBound,
                List<Genotype> parentGeneration,
                int memberId,
                int genotypeLength,
                Map<String, UserDefinedFunction> geneExprMapping,
                int phenoTypeLength,
                List<City> baseOrder)  {

            this.upperBound = upperBound;
            this.parentGeneration = parentGeneration;
            this.memberId = memberId;
            this.geneExprMapping = geneExprMapping;
            this.baseOrder = baseOrder;
        }


    }


    public WorkerActor() {

    }


    @Override
    public Receive createReceive() {
        return receiveBuilder().match(CreateGenotype.class, request -> {
            logger.debug("======================================");
            logger.info("Child Actor Received Message Generation:" + self());
            logger.debug("======================================");
            Genotype genotype = executeCreateGenotype(request.memberId, request.genotypeLength,
                    request.phenoTypeLength, request.geneExprBag,
                    request.baseOrder,
                    request.geneExprMapping);

            // send the response back
            sender().tell(new MasterActor.Result(genotype), getSelf());
            context().stop(getSelf());
        }).match(RegenerateGenotype.class, request -> {
            logger.debug("======================================");
           logger.info("Child Actor Received Message for Regeneration : " +self());
            logger.debug("======================================");
            Genotype child = executeRegenerate(request.upperBound,
                    request.parentGeneration,
                    request.memberId,
                    request.geneExprMapping,
                    request.baseOrder);



            sender().tell(new MasterActor.Result(child), getSelf());
            context().stop(getSelf());

        }).build();
    }

    private Genotype executeRegenerate(int upperBound,
                                       List<Genotype> parentGeneration,
                                       int memberId,
                                       Map<String, UserDefinedFunction> geneExprMapping,
                                       List<City> baseOrder) {
        Random r = new Random();
        int firstParent = getRandomParentIndex(upperBound, r);
        int secondParent = getRandomParentIndex(upperBound, r);

        while(firstParent == secondParent) {
            secondParent = getRandomParentIndex(upperBound, r);
        }
        logger.debug("======================================");
        logger.debug("Creating Crossover child");
        logger.debug("======================================");
        Genotype child = crossover(firstParent, secondParent, memberId,
                parentGeneration, geneExprMapping, baseOrder);
        logger.debug("======================================");
        logger.debug("Created Genotype " + memberId);
        logger.debug("Distance :" + child.getPhenotype().getDistance());
        logger.debug("Fitness Score : " + child.getPhenotype().getFitnessScore());
        logger.debug("Genotype Sequence :" + Arrays.toString(child.getRepresentation())
                .replace("[","").replace("]","").replace(",",""));
        logger.debug("======================================");
        return child;

    }

    private int getRandomParentIndex(int upperBound, Random r) {

        int randInt = r.nextInt((upperBound));
        return randInt;
    }


    private Genotype crossover(int firstParent, int secondParent, int memberId,
                               List<Genotype> parentGeneration,
                               Map<String, UserDefinedFunction> geneExprMapping,
                               List<City> baseOrder) {

        Genotype genoFirst = parentGeneration.get(firstParent);
        Genotype genoSecond = parentGeneration.get(secondParent);
        logger.debug("======================================");
        logger.debug("Parent 1 Genotype Sequence :" + Arrays.toString(genoFirst.getRepresentation())
                .replace("[","").replace("]","").replace(",",""));
        logger.debug("Parent 2 Genotype Sequence :" + Arrays.toString(genoSecond.getRepresentation())
                .replace("[","").replace("]","").replace(",",""));
        logger.debug("======================================");

        String[] childRepresentation = new String[genoFirst.getRepresentation().length];
        IntStream.range(0, childRepresentation.length)
                .forEach(index -> {
                    if(index < childRepresentation.length / 2) {
                        childRepresentation[index] = genoFirst.getRepresentation()[index];
                    } else {
                        childRepresentation[index] = genoSecond.getRepresentation()[index];
                    }
                });

        Genotype child = new Genotype(memberId);
        child.setRepresentation(childRepresentation);
        child.generatePhenotype(geneExprMapping, baseOrder);
        return child;
    }


    private Genotype executeCreateGenotype(int memberId, int genotypeLength, int phenoTypeLength,
                                    String[] geneExprBag,
                                    List<City> baseOrder,
                                    Map<String, UserDefinedFunction> geneExprMapping) {
        Genotype genotype = new Genotype(genotypeLength * 3, memberId);
        Random r = new Random();
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


        List<City> newBaseOrder = new ArrayList<>();
        baseOrder.stream().forEach(bo -> newBaseOrder.add(bo));
        genotype.generatePhenotype(geneExprMapping, newBaseOrder);
        logger.debug("======================================");
        logger.debug("Created Genotype " + memberId);
        logger.debug("Distance :" + genotype.getPhenotype().getDistance());
        logger.debug("Fitness Score : " + genotype.getPhenotype().getFitnessScore());
        logger.debug("Genotype Sequence :" + Arrays.toString(genotype.getRepresentation())
                .replace("[","").replace("]","").replace(",",""));
        logger.debug("======================================");
        return genotype;
    }




}
