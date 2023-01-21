package pl.kurs.figures.model;

import org.hibernate.envers.Audited;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;


@Entity
@DiscriminatorValue(value = "Circle")
@Audited
public class Circle extends AbstractFigure {


    private BigDecimal radius;

    public Circle() {
    }

    public Circle(BigDecimal radius) {
        this.radius = radius;
    }


    @Override
    public BigDecimal calculateAreaForFigure() {
        return BigDecimal.valueOf(Math.PI).multiply(this.radius).multiply(this.radius);
    }

    @Override
    public BigDecimal calculatePerimeterForFigure() {
        return BigDecimal.valueOf(Math.PI).multiply(BigDecimal.valueOf(2)).multiply(this.radius);
    }


    public BigDecimal getRadius() {
        return radius;
    }

    public void setRadius(BigDecimal radius) {
        this.radius = radius;
    }






}
