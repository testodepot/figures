package pl.kurs.figures.service;

import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.exception.BadEntityException;
import pl.kurs.figures.exception.EntityNotFoundException;
import pl.kurs.figures.model.AbstractFigureView;
import pl.kurs.figures.repository.AbstractFigureViewRepository;
import pl.kurs.figures.strategy.ComputingStrategy;
import pl.kurs.figures.strategy.ComputingStrategyFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AbstractFigureViewService {

    private ComputingStrategyFactory computingStrategyFactory;

    private AbstractFigureViewRepository abstractFigureViewRepository;

    public AbstractFigureViewService(ComputingStrategyFactory computingStrategyFactory, AbstractFigureViewRepository abstractFigureViewRepository) {
        this.computingStrategyFactory = computingStrategyFactory;
        this.abstractFigureViewRepository = abstractFigureViewRepository;
    }

    public AbstractFigureView computeAreaAndPerimeterForFigureView(CreateFigureCommand createFigureCommand) {
        String strategyName = "Compute" + StringUtils.capitalize(createFigureCommand.getType());
        ComputingStrategy computingStrategy = computingStrategyFactory.findStrategy(strategyName);
        BigDecimal area = computingStrategy.computeArea(createFigureCommand.getParameters());
        BigDecimal perimeter = computingStrategy.computePerimeter(createFigureCommand.getParameters());
        AbstractFigureView abstractFigureView = new AbstractFigureView(area, perimeter);
        return abstractFigureView;
    }

    public AbstractFigureView computeAreaAndPerimeterForEditedFigure(String type, List<BigDecimal> parameters) {
        String strategyName = "Compute" + type;
        ComputingStrategy computingStrategy = computingStrategyFactory.findStrategy(strategyName);
        BigDecimal area = computingStrategy.computeArea(parameters);
        BigDecimal perimeter = computingStrategy.computePerimeter(parameters);
        AbstractFigureView abstractFigureView = new AbstractFigureView(area, perimeter);
        return abstractFigureView;
    }


    public AbstractFigureView edit(AbstractFigureView figureView) {
        return abstractFigureViewRepository.save(Optional.ofNullable(figureView)
                .filter(x -> Objects.nonNull(x.getModelId()))
                .orElseThrow(() -> new BadEntityException("AbstractFigureView")));
    }

    public AbstractFigureView getById(Long id) {
        return abstractFigureViewRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(id, "AbstractFigureView"));
    }

    public List<Revision<Integer, AbstractFigureView>> findAreaAndPerimeterOfSpecificFigure(long id) {
        return abstractFigureViewRepository.findRevisions(id).getContent();
    }


}
