package pl.kurs.figures.strategy;

import org.springframework.stereotype.Component;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Rectangle;
import java.math.BigDecimal;
import java.util.List;

@Component
public class CreateRectangle implements CreatingStrategy {

    @Override
    public AbstractFigure createFigure(List<BigDecimal> params) {
        Rectangle rectangle = new Rectangle(params.get(0), params.get(1));
        rectangle.setType("Rectangle");
        return rectangle;
    }

    @Override
    public String getStrategyName() {
        return "Rectangle";
    }

    @Override
    public int getNumberOfParamsOfSpecificFigure() {
        return 2;
    }
}
