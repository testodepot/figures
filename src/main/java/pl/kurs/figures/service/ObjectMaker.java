package pl.kurs.figures.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.exception.BadEntityException;
import pl.kurs.figures.strategy.Strategy;
import pl.kurs.figures.strategy.StrategyFactory;
import pl.kurs.figures.strategy.StrategyName;

import java.util.List;

@Component
public class ObjectMaker {

    @Value("${figures}")
    private List<String> figures;

    private StrategyFactory strategyFactory;

    public ObjectMaker(List<String> figures, StrategyFactory strategyFactory) {
        this.figures = figures;
        this.strategyFactory = strategyFactory;
    }

    public Object makeObject(CreateFigureCommand createFigureCommand) {

        boolean isCorrect = checkIfFigureIsCorrect(StringUtils.capitalize(createFigureCommand.getType()));

        if (isCorrect) {
            StrategyName strategyName = StrategyName.valueOf(StringUtils.capitalize(createFigureCommand.getType()));
            Strategy strategy = strategyFactory.findStrategy(strategyName);
            return strategy.createFigure(StringUtils.capitalize(createFigureCommand.getType()), createFigureCommand.getParameters());
        }
        else
            throw new BadEntityException("No figure model of this type! Available types: " + figures);
    }

    public boolean checkIfFigureIsCorrect(String figureType) {
        return figures.contains(StringUtils.capitalize(figureType));
    }

    public List<String> getFigures() {
        return figures;
    }

    public void setFigures(List<String> figures) {
        this.figures = figures;
    }
}
