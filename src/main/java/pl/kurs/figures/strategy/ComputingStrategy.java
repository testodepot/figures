package pl.kurs.figures.strategy;

import java.math.BigDecimal;
import java.util.List;

public interface ComputingStrategy {

    BigDecimal computeArea(List<BigDecimal> params);
    BigDecimal computePerimeter(List<BigDecimal> params);

    String getStrategyName();

}
