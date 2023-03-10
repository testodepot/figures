package pl.kurs.figures.strategy;

import pl.kurs.figures.model.AbstractFigure;

import java.math.BigDecimal;
import java.util.List;

public interface CreatingStrategy {

    AbstractFigure createFigure(String type, List<BigDecimal> params);
    String getStrategyName();
    int getNumberOfParamsOfSpecificFigure();
}
