package pl.kurs.figures.strategy;

import org.springframework.stereotype.Component;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Square;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CreateSquare implements Strategy {

    @Override
    public AbstractFigure createFigure(String type, List<BigDecimal> params) {
        Square square = new Square(params.get(0));
        BigDecimal area = square.calculateArea();
        BigDecimal perimeter = square.calculatePerimeter();
        square.setArea(area);
        square.setPerimeter(perimeter);
        square.setType("Square");
        return square;
    }


    @Override
    public StrategyName getStrategyName() {
        return StrategyName.Square;
    }
}
