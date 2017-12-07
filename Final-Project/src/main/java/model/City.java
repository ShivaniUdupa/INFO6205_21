package model;

/**
 * Created by deveshkandpal on 12/6/17.
 */
public class City {

    private double x;
    private double y;
    private String name;
    private int id;

    public City(double x, double y, String name, int id) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                '}';
    }
}
