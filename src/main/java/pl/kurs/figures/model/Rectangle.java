package pl.kurs.figures.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue(value = "Rectangle")
public class Rectangle extends AbstractFigure implements Figure {

    private BigDecimal length;

    private BigDecimal width;


    public Rectangle() {
    }

    public Rectangle(BigDecimal length, BigDecimal width) {
        this.length = length;
        this.width = width;
        this.setPerimeter(calculatePerimeter());
        this.setArea(calculateArea());
        this.setType("Rectangle");
    }

    @Override
    public BigDecimal calculatePerimeter() {
        return width.multiply(BigDecimal.valueOf(2)).add(length.multiply(BigDecimal.valueOf(2)));
    }

    @Override
    public BigDecimal calculateArea() {
        return width.multiply(length);
    }


    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

}
