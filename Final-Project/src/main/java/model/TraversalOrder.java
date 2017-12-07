package model;

import java.util.List;
import java.util.stream.IntStream;


/**
 * Created by deveshkandpal on 12/6/17.
 */
public class TraversalOrder implements Comparable<TraversalOrder> {
    private int memberId;
    private List<City> traversalOrder;
    private double distance;
    private double fitnessScore;

    public TraversalOrder(List<City> traversalOrder) {
        this.traversalOrder = traversalOrder;
    }

    public List<City> getTraversalOrder() {
        return traversalOrder;
    }

    public void setTraversalOrder(List<City> traversalOrder) {
        this.traversalOrder = traversalOrder;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getFitnessScore() {
        return fitnessScore;
    }

    public void setFitnessScore(double fitnessScore) {
        this.fitnessScore = fitnessScore;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<Order " + this.memberId + " : ");

        IntStream.range(0, this.traversalOrder.size())
                .forEach(iter -> {
                    sb.append(this.traversalOrder.get(iter).toString());
                    if(iter != this.traversalOrder.size() - 1)
                        sb.append("->");
                });

        sb.append(">");
        sb.append("< distance : " + this.distance + ">");
        sb.append("< fitness score : " + this.fitnessScore + ">");

        return sb.toString();
    }

    public int compareTo(TraversalOrder other) {

        return other.fitnessScore < this.fitnessScore ? 1 : (other.fitnessScore == this.fitnessScore ? 0 : -1);
    }

    public void computeFitnessScore() {

        this.distance = IntStream.range(0, this.traversalOrder.size() - 1)
                .mapToDouble(iter -> computeDistance(iter))
                .sum();

        this.fitnessScore = 1 / this.distance;
    }

    public double computeDistance(int iter) {

        City first = this.traversalOrder.get(iter);
        City second = null;
        if(iter != this.traversalOrder.size() - 1) {

            second = this.traversalOrder.get(iter + 1);

        } else {
            second = this.traversalOrder.get(0);
        }
        double distance = pairWiseDistance(first, second);
        System.out.println("Computed Distance for iter :" + iter + " " + distance);
        return distance;
    }

    public double pairWiseDistance(City first, City second) {
        return Math.sqrt(Math.pow(first.getX() - second.getX(), 2) + Math.pow(first.getY() - second.getY(), 2));
    }



}
