package pl.kurs.figures.model;

import org.hibernate.envers.Audited;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.List;

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
    public BigDecimal calculateAreaForFigure(List<BigDecimal> params) {
        return BigDecimal.valueOf(Math.PI).multiply(params.get(0)).multiply(params.get(0));
    }

    @Override
    public BigDecimal calculatePerimeterForFigure(List<BigDecimal> params) {
        return BigDecimal.valueOf(Math.PI).multiply(BigDecimal.valueOf(2)).multiply(params.get(0));
    }

    @Override
    public BigDecimal calculateAreaForFigureDto(AbstractFigure figure) {
        Circle circle = (Circle) figure;
        return BigDecimal.valueOf(Math.PI).multiply(circle.getRadius()).multiply(circle.getRadius());
    }

    @Override
    public BigDecimal calculatePerimeterForFigureDto(AbstractFigure figure) {
        Circle circle = (Circle) figure;
        return BigDecimal.valueOf(Math.PI).multiply(BigDecimal.valueOf(2)).multiply(circle.getRadius());
    }

    @Override
    public void postLoad() {
        this.setArea(BigDecimal.valueOf(Math.PI).multiply(radius).multiply(radius));
        this.setPerimeter(BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(Math.PI).multiply(radius)));
    }

    public BigDecimal getRadius() {
        return radius;
    }

    public void setRadius(BigDecimal radius) {
        this.radius = radius;
    }






}
