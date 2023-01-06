package pl.kurs.figures.strategy;


import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ComputeCircle implements ComputingStrategy {

    double PI = 3.14;


    @Override
    public BigDecimal computeArea(List<BigDecimal> params) {
        BigDecimal area = BigDecimal.valueOf(PI).multiply(params.get(0)).multiply(params.get(0));
        return area;
    }

    @Override
    public BigDecimal computePerimeter(List<BigDecimal> params) {
        BigDecimal perimeter = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(PI)).multiply(params.get(0));
        return perimeter;
    }

    @Override
    public String getStrategyName() {
        return "ComputeCircle";
    }
}
