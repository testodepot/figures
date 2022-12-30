package pl.kurs.figures.strategy;

import org.springframework.stereotype.Component;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Rectangle;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CreateRectangle implements Strategy {

    @Override
    public AbstractFigure createFigure(String type, List<BigDecimal> params) {
        Rectangle rectangle = new Rectangle(params.get(0), params.get(1));
        BigDecimal area = rectangle.calculateArea();
        BigDecimal perimeter = rectangle.calculatePerimeter();
        rectangle.setArea(area);
        rectangle.setPerimeter(perimeter);
        rectangle.setType("Rectangle");
        return rectangle;
    }

    @Override
    public StrategyName getStrategyName() {
        return StrategyName.Rectangle;
    }
}