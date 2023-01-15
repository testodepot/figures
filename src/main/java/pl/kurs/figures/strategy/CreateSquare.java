package pl.kurs.figures.strategy;

import org.springframework.stereotype.Component;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Square;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CreateSquare implements CreatingStrategy {

    @Override
    public AbstractFigure createFigure(String type, List<BigDecimal> params) {
        Square square = new Square(params.get(0));
        square.setType("Square");
        return square;
    }


    @Override
    public String getStrategyName() {
        return "Square";
    }

    @Override
    public int getNumberOfParamsOfSpecificFigure() {
        return 1;
    }
}
