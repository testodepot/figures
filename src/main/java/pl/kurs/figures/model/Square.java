package pl.kurs.figures.model;

import org.springframework.stereotype.Component;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue(value = "Square")
@Component("Square")
public class Square extends AbstractFigure implements Figure{

    private BigDecimal side;

    public Square() {
    }

    public Square(BigDecimal side) {
        this.side = side;
        this.setPerimeter(calculatePerimeter());
        this.setArea(calculateArea());
        this.setType("Square");
    }

    @Override
    public BigDecimal calculatePerimeter() {
        return side.multiply(BigDecimal.valueOf(4));
    }

    @Override
    public BigDecimal calculateArea() {
        return side.multiply(side);
    }


    public BigDecimal getSide() {
        return side;
    }

    public void setSide(BigDecimal side) {
        this.side = side;
    }
}
