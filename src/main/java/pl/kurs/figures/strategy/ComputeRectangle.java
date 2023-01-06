package pl.kurs.figures.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ComputeRectangle implements ComputingStrategy {

    @Override
    public BigDecimal computeArea(List<BigDecimal> params) {
        return params.get(0).multiply(params.get(1));
    }

    @Override
    public BigDecimal computePerimeter(List<BigDecimal> params) {
        return params.get(0).multiply(BigDecimal.valueOf(2)).add(params.get(1).multiply(BigDecimal.valueOf(2)));
    }

    @Override
    public String getStrategyName() {
        return "ComputeRectangle";
    }
}
