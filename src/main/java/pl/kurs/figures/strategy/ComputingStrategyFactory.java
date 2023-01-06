package pl.kurs.figures.strategy;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ComputingStrategyFactory {

    private Map<String, ComputingStrategy> strategies;

    public ComputingStrategyFactory(Set<ComputingStrategy> computingStrategySet) {
        createStrategy(computingStrategySet);
    }

    private void createStrategy(Set<ComputingStrategy> computingStrategySet) {
        strategies = new HashMap<String, ComputingStrategy>();
        computingStrategySet
                .forEach(strategy -> strategies.put(strategy.getStrategyName(), strategy));
    }

    public ComputingStrategy findStrategy(String figureType) {
        return strategies.get(figureType);
    }


}
