package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import model.City;
import model.Genotype;
import udf.UserDefinedFunction;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by deveshkandpal on 12/9/17.
 */
public class WorkerActor extends AbstractActor {

    static public Props props(String[] bag , int phenoTypeLength ,
                              int populationSize,
                              int genoTypeLength ,
                              double cutoff,
                              Map<String, UserDefinedFunction> geneExprMapping,
                              List<City> baseOrder ) {
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

        @Override
        public String toString() {
            return "CreateGenotype{" +
                    "memberId=" + memberId +
                    ", genotypeLength=" + genotypeLength +
                    ", geneExprMapping=" + geneExprMapping +
                    ", phenoTypeLength=" + phenoTypeLength +
                    ", geneExprBag=" + Arrays.toString(geneExprBag) +
                    ", baseOrder=" + baseOrder +
                    '}';
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

        @Override
        public String toString() {
            return "RegenerateGenotype{" +
                    "upperBound=" + upperBound +
                    ", parentGeneration=" + parentGeneration +
                    ", memberId=" + memberId +
                    ", geneExprMapping=" + geneExprMapping +
                    ", baseOrder=" + baseOrder +
                    '}';
        }
    }


    public WorkerActor() {

    }


    @Override
    public Receive createReceive() {
        return receiveBuilder().match(CreateGenotype.class, request -> {
           // System.out.println("Child Received Message : " + request.toString());
            Genotype genotype = executeCreateGenotype(request.memberId, request.genotypeLength,
                    request.phenoTypeLength, request.geneExprBag,
                    request.baseOrder,
                    request.geneExprMapping);

            // send the response back
            sender().tell(new MasterActor.Result(genotype), getSelf());
            context().stop(getSelf());
        }).match(RegenerateGenotype.class, request -> {
           // System.out.println("Received request for regeneration :" + request.toString());
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
        Genotype child = crossover(firstParent, secondParent, memberId,
                parentGeneration, geneExprMapping, baseOrder);
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

        return genotype;
    }




}
