package pl.kurs.figures.model;

import org.hibernate.envers.Audited;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue(value = "Square")
@Audited
public class Square extends AbstractFigure {

    private BigDecimal side;

    public Square() {
    }

    public Square(BigDecimal side) {
        this.side = side;
    }

    public BigDecimal getSide() {
        return side;
    }

    public void setSide(BigDecimal side) {
        this.side = side;
    }
}
