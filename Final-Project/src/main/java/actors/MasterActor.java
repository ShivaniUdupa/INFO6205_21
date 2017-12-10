package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import model.City;
import model.Genotype;
import udf.UserDefinedFunction;

import java.util.*;
import org.apache.log4j.Logger;
import java.util.stream.IntStream;

/**
 * Created by deveshkandpal on 12/8/17.
 */
public class MasterActor extends AbstractActor {

    final static Logger logger = Logger.getLogger(MasterActor.class);

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
            logger.info("Master Actor created : " + self());
            logger.info("Initializing Process");
            executeInitLogic();
        }).match(Result.class, result -> {
            executeResultLogic(result.genotype);
        }).build();
    }

    private void executeResultLogic(Genotype genotype) {
            this.resultGenotypes.add(genotype);
            if(this.resultGenotypes.size() == this.populationSize) {
                logger.debug("======================================");
                logger.info("All Results received for generation :" + this.currentGeneration);
                logger.debug("======================================");
                Collections.sort(this.resultGenotypes, this.genoTypeComparator);
                logger.info("Sorting Results for generation " + this.currentGeneration);
                logger.debug("======================================");
                logger.info("Generation " +this.currentGeneration  + " best distance computed :" + this.resultGenotypes.get(0).getPhenotype().getDistance());
                logger.debug("======================================");
                this.currentGeneration++;
                if(this.currentGeneration <= this.stopGeneration) {
                    // spawn and regn
                    logger.info("Spawning next generation");
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
                    logger.debug("======================================");
                    logger.info("Completed Time to terminate");
                    logger.debug("======================================");

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
        logger.debug("======================================");
        logger.info("Executing Regeneration for generation :" + this.currentGeneration);
        logger.debug("======================================");
        IntStream.range(0, this.populationSize)
                .forEach(index -> {
                    List<City> newBaseOrder = new ArrayList<>();
                    baseOrder.stream().forEach(bo -> newBaseOrder.add(bo));
                    //ActorRef child = getContext().actorOf(Props.create(WorkerActor.class),"Generation-"+this.currentGeneration+"Child-"+index);
                    ActorRef child = getContext().actorOf(WorkerActor.props(), "Generation-"+this.currentGeneration+"-Child-"+index);
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
        logger.debug("======================================");
        logger.info("Spawning Children for creating Generation 0");
        logger.debug("======================================");
        IntStream.range(0, this.populationSize)
                .forEach(index -> {

                    List<City> newBaseOrder = new ArrayList<>();
                    baseOrder.stream().forEach(bo -> newBaseOrder.add(bo));
                    ActorRef child = getContext().actorOf(WorkerActor.props(),"Generation-"+this.currentGeneration+"-Child-"+index);
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


            return gB.compareTo(gA);


        }

    };
}
