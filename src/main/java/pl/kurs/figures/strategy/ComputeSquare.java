package pl.kurs.figures.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ComputeSquare implements ComputingStrategy {

    @Override
    public BigDecimal computeArea(List<BigDecimal> params) {
        return params.get(0).multiply(params.get(0));
    }

    @Override
    public BigDecimal computePerimeter(List<BigDecimal> params) {
        return params.get(0).multiply(BigDecimal.valueOf(4));
    }

    @Override
    public String getStrategyName() {
        return "ComputeSquare";
    }
}
