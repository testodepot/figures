package pl.kurs.figures.strategy;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class StrategyFactory {

    private Map<String, Strategy> strategies;

    public StrategyFactory(Set<Strategy> strategySet) {
        createStrategy(strategySet);
    }

    private void createStrategy(Set<Strategy> strategySet) {
        strategies = new HashMap<String, Strategy>();
        strategySet
                .forEach(strategy -> strategies.put(strategy.getStrategyName(), strategy));
    }

    public Strategy findStrategy(String figureType) {
        return strategies.get(figureType);
    }


}
