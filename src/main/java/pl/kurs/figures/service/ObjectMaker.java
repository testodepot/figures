package pl.kurs.figures.service;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.strategy.CreatingStrategy;
import pl.kurs.figures.strategy.CreatingStrategyFactory;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ObjectMaker {


    private CreatingStrategyFactory creatingStrategyFactory;


    public ObjectMaker(CreatingStrategyFactory creatingStrategyFactory) {
        this.creatingStrategyFactory = creatingStrategyFactory;
    }

    public AbstractFigure makeObject(CreateFigureCommand createFigureCommand) {
        String figureType = StringUtils.capitalize(createFigureCommand.getType());
        CreatingStrategy creatingStrategy = creatingStrategyFactory.findStrategy(figureType);
        return creatingStrategy.createFigure(StringUtils.capitalize(createFigureCommand.getType()), createFigureCommand.getParameters());
    }

    public AbstractFigure makeObjectWithTypeAndParams(String type, List<BigDecimal> params) {
        CreatingStrategy creatingStrategy = creatingStrategyFactory.findStrategy(type);
        return creatingStrategy.createFigure(type, params);
    }

}
