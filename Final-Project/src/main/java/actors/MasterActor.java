package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import model.City;
import model.Genotype;
import udf.UserDefinedFunction;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by deveshkandpal on 12/8/17.
 */
public class MasterActor extends AbstractActor {

    static public Props props(String[] geneExprBag , int phenoTypeLength ,
                              int populationSize,
                              int genoTypeLength ,
                              double cutoff,
                              Map<String, UserDefinedFunction> geneExprMapping,
                              List<City> baseOrder,
                              int stopGeneration) {
        return Props.create(MasterActor.class, () -> new MasterActor(phenoTypeLength,
                populationSize, genoTypeLength, cutoff, geneExprMapping,
                baseOrder, geneExprBag, stopGeneration));
    }


    // Messages this actor can handle
    static public class Init {

        public Init() {}
    }

    static public class Result {

        public final Genotype genotype;

        public Result(Genotype genotype) {
            this.genotype = genotype;
        }
    }


    // Messages End

    // attributes of master

    private final int phenoTypeLength;
    private final int populationSize;
    private final int genoTypeLength;
    private final double cutoff;
    private final Map<String, UserDefinedFunction> geneExprMapping;
    private final List<City> baseOrder;
    private final String[] geneExprBag;
    private List<Genotype> gtList;
    private List<Genotype> resultGenotypes;
    private final int stopGeneration;
    private int currentGeneration = 0;
    // constructor for master
    public MasterActor(int phenoTypeLength, int populationSize,
                       int genoTypeLength, double cutoff,
                       Map<String, UserDefinedFunction> geneExprMapping,
                       List<City> baseOrder,
                       String[] geneExprBag,
                       int stopGeneration) {

        this.geneExprBag = geneExprBag;
        this.phenoTypeLength = phenoTypeLength;
        this.populationSize = populationSize;
        this.genoTypeLength = genoTypeLength;
        this.cutoff = cutoff;
        this.geneExprMapping = geneExprMapping;
        this.baseOrder = baseOrder;
        this.gtList = new ArrayList<>();
        this.stopGeneration = stopGeneration;
        this.resultGenotypes = new ArrayList<>();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Init.class, init -> {
           // System.out.println("Received Init Message");
            executeInitLogic();
        }).match(Result.class, result -> {
            executeResultLogic(result.genotype);
        }).build();
    }

    private void executeResultLogic(Genotype genotype) {
            this.resultGenotypes.add(genotype);
            if(this.resultGenotypes.size() == this.populationSize) {
                System.out.println("All Results received for generation :" + this.currentGeneration);
                Collections.sort(this.resultGenotypes, this.genoTypeComparator);
                this.currentGeneration++;
                System.out.println("Generation " +this.currentGeneration  + " fitness score :" + this.resultGenotypes.get(0).getPhenotype().getFitnessScore());
                if(this.currentGeneration <= this.stopGeneration) {
                    // spawn and regn
                    this.gtList = new ArrayList<>();
                    this.resultGenotypes.stream().forEach(obj -> gtList.add(obj));
                    this.resultGenotypes.clear();
                    int upperBound = (int)((1 - this.cutoff) * this.gtList.size());

                    executeRegeneration(upperBound,
                            gtList,
                            genoTypeLength,
                            geneExprMapping,
                            phenoTypeLength,
                            baseOrder
                            );


                } else {
                    System.out.println("Completed " + this.currentGeneration + ". Time to terminate");
                    context().stop(getSelf());
                    context().system().terminate();
                }


            }
    }

    private void executeRegeneration(
            int upperBound,
            List<Genotype> parentGeneration,

            int genotypeLength,
            Map<String, UserDefinedFunction> geneExprMapping,
            int phenoTypeLength,
            List<City> baseOrder) {

        IntStream.range(0, this.populationSize)
                .forEach(index -> {
                    List<City> newBaseOrder = new ArrayList<>();
                    baseOrder.stream().forEach(bo -> newBaseOrder.add(bo));
                    ActorRef child = getContext().actorOf(Props.create(WorkerActor.class),"Generation-"+this.currentGeneration+"Child-"+index);
                    WorkerActor.RegenerateGenotype message =
                            new WorkerActor.RegenerateGenotype(upperBound,
                                                    parentGeneration,
                                                    index,
                                                    genotypeLength,
                                                    geneExprMapping,
                                                    phenoTypeLength, newBaseOrder);


                    child.tell(message, getSelf());
                });


    }

    private void executeInitLogic() {
        IntStream.range(0, this.populationSize)
                .forEach(index -> {

                    List<City> newBaseOrder = new ArrayList<>();
                    baseOrder.stream().forEach(bo -> newBaseOrder.add(bo));
                    ActorRef child = getContext().actorOf(Props.create(WorkerActor.class),"Generation-"+this.currentGeneration+"Child-"+index);
                    WorkerActor.CreateGenotype message =
                            new WorkerActor.CreateGenotype(index, genoTypeLength,
                                    geneExprMapping,
                                    phenoTypeLength,
                                    geneExprBag,
                                    newBaseOrder);

                    child.tell(message, getSelf());
                });
    }


    public Comparator<Genotype> genoTypeComparator
            = new Comparator<Genotype>() {

        public int compare(Genotype gA, Genotype gB) {

            double fitnessA = gA.getPhenotype().getFitnessScore();
            double fitnessB = gB.getPhenotype().getFitnessScore();

            //ascending order
            return gB.compareTo(gA);

            //descending order
            //return fruitName2.compareTo(fruitName1);
        }

    };
}
