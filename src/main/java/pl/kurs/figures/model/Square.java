package pl.kurs.figures.model;

import org.hibernate.envers.Audited;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.List;

@Entity
@DiscriminatorValue(value = "Square")
@Audited
public class Square extends AbstractFigure  {

    private BigDecimal side;

    public Square() {
    }

    public Square(BigDecimal side) {
        this.side = side;
    }


    @Override
    public BigDecimal calculateAreaForFigure(List<BigDecimal> params) {
        return params.get(0).multiply(params.get(0));
    }

    @Override
    public BigDecimal calculatePerimeterForFigure(List<BigDecimal> params) {
        return params.get(0).multiply(BigDecimal.valueOf(4));
    }

    @Override
    public BigDecimal calculateAreaForFigureDto(AbstractFigure figure) {
        Square square = (Square) figure;
        return square.getSide().multiply(square.getSide());
    }

    @Override
    public BigDecimal calculatePerimeterForFigureDto(AbstractFigure figure) {
        Square square = (Square) figure;
        return square.getSide().multiply(BigDecimal.valueOf(4));
    }

    @Override
    public void postLoad() {
        this.setArea(side.multiply(side));
        this.setPerimeter(BigDecimal.valueOf(4).multiply(side));
    }

    public BigDecimal getSide() {
        return side;
    }

    public void setSide(BigDecimal side) {
        this.side = side;
    }
}
