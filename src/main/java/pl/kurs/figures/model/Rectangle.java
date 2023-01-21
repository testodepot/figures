package pl.kurs.figures.model;

import org.hibernate.envers.Audited;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue(value = "Rectangle")
@Audited
public class Rectangle extends AbstractFigure {

    private BigDecimal length;

    private BigDecimal width;


    public Rectangle() {
    }

    public Rectangle(BigDecimal length, BigDecimal width) {
        this.length = length;
        this.width = width;
    }

    @Override
    public BigDecimal calculateAreaForFigure() {
        return this.length.multiply(this.getWidth());
    }

    @Override
    public BigDecimal calculatePerimeterForFigure() {
        return this.length.multiply(BigDecimal.valueOf(2)).add(this.width.multiply(BigDecimal.valueOf(2)));
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
