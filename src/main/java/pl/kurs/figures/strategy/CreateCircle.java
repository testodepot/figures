package pl.kurs.figures.strategy;

import org.springframework.stereotype.Component;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Circle;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CreateCircle implements Strategy {


    @Override
    public AbstractFigure createFigure(String type, List<BigDecimal> params) {
        Circle circle = new Circle(params.get(0));
        BigDecimal area = circle.calculateArea();
        BigDecimal perimeter = circle.calculatePerimeter();
        circle.setArea(area);
        circle.setPerimeter(perimeter);
        circle.setType("Circle");
        return circle;
    }

    @Override
    public StrategyName getStrategyName() {
        return StrategyName.Circle;
    }
}
