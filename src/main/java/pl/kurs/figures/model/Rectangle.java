package pl.kurs.figures.model;

import org.hibernate.envers.Audited;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.List;

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
    public BigDecimal calculateAreaForFigure(List<BigDecimal> params) {
        return params.get(0).multiply(params.get(1));
    }

    @Override
    public BigDecimal calculatePerimeterForFigure(List<BigDecimal> params) {
        return params.get(0).multiply(BigDecimal.valueOf(2)).add(params.get(1).multiply(BigDecimal.valueOf(2)));
    }

    @Override
    public BigDecimal calculateAreaForFigureDto(AbstractFigure figure) {
        Rectangle rectangle = (Rectangle) figure;
        return rectangle.getWidth().multiply(rectangle.getLength());
    }

    @Override
    public BigDecimal calculatePerimeterForFigureDto(AbstractFigure figure) {
        Rectangle rectangle = (Rectangle) figure;
        return rectangle.getWidth().multiply(BigDecimal.valueOf(2)).add(rectangle.getLength().multiply(BigDecimal.valueOf(2)));
    }

    @Override
    public void postLoad() {
        this.setArea(width.multiply(length));
        this.setPerimeter(BigDecimal.valueOf(2).multiply(length).add(BigDecimal.valueOf(2).multiply(width)));
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
