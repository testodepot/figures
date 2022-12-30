package pl.kurs.figures.strategy;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class StrategyFactory {

    private Map<StrategyName, Strategy> strategies;

    public StrategyFactory(Set<Strategy> strategySet) {
        createStrategy(strategySet);
    }

    private void createStrategy(Set<Strategy> strategySet) {
        strategies = new HashMap<StrategyName, Strategy>();
        strategySet
                .forEach(strategy -> strategies.put(strategy.getStrategyName(), strategy));
    }

    public Strategy findStrategy(StrategyName strategyName) {
        return strategies.get(strategyName);
    }


}
