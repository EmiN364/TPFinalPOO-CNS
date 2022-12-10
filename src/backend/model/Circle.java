package backend.model;

public class Circle extends Ellipse {
private static final String NAME="Círculo";
    public Circle(Point centerPoint, double radius) {
        super(centerPoint, 2 * radius, 2 * radius);
    }
    public String getName(){return NAME;}
    @Override
    public String toString() {
        return String.format("Círculo [Centro: %s, Radio: %.2f]", getCenterPoint(), getRadius());
    }
    public double getRadius() {
        return getsMayorAxis() / 2;
    }

}

