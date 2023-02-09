package pl.kurs.figures.strategy;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class CreatingStrategyFactory {

    private Map<String, CreatingStrategy> strategies;

    public CreatingStrategyFactory(Set<CreatingStrategy> creatingStrategySet) {
        createStrategy(creatingStrategySet);
    }

    private void createStrategy(Set<CreatingStrategy> creatingStrategySet) {
        strategies = new HashMap<String, CreatingStrategy>();
        creatingStrategySet
                .forEach(strategy -> strategies.put(strategy.getStrategyName(), strategy));
    }

    public CreatingStrategy findStrategy(String figureType) {
        return strategies.get(figureType);
    }

    public Map<String, CreatingStrategy> getStrategies() {
        return strategies;
    }
}
