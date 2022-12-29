package pl.kurs.figures.model;


import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue(value = "Circle")
public class Circle extends AbstractFigure implements Figure {


    private BigDecimal radius;


    public Circle() {
    }

    public Circle(BigDecimal radius) {
        this.radius = radius;
        this.setPerimeter(calculatePerimeter());
        this.setArea(calculateArea());
        this.setType("Circle");
    }

    @Override
    public BigDecimal calculatePerimeter() {
        return BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(PI)).multiply(radius);
    }

    @Override
    public BigDecimal calculateArea() {
        return BigDecimal.valueOf(PI).multiply(radius).multiply(radius);
    }


}
