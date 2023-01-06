package pl.kurs.figures.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.model.AbstractFigureView;
import pl.kurs.figures.repository.AbstractFigureViewRepository;
import pl.kurs.figures.strategy.ComputeCircle;
import pl.kurs.figures.strategy.ComputingStrategy;
import pl.kurs.figures.strategy.ComputingStrategyFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class AbstractFigureViewServiceTest {

    @Mock
    private ComputingStrategyFactory computingStrategyFactory;

    @Mock
    private AbstractFigureViewRepository abstractFigureViewRepository;

    @InjectMocks
    private AbstractFigureViewService abstractFigureViewService;


    @BeforeEach
    public void setup() {
        computingStrategyFactory = Mockito.mock(ComputingStrategyFactory.class);
        abstractFigureViewRepository = Mockito.mock(AbstractFigureViewRepository.class);
        abstractFigureViewService = new AbstractFigureViewService(computingStrategyFactory, abstractFigureViewRepository);
    }


    @Test
    public void computeAreaAndPerimeterForFigureViewTest() {
        ComputingStrategy computingStrategyCircle = new ComputeCircle();
        List<BigDecimal> params = new ArrayList<>();
        params.add(BigDecimal.valueOf(50));
        CreateFigureCommand createFigureCommand = new CreateFigureCommand("Circle", params);

        given(computingStrategyFactory.findStrategy(ArgumentMatchers.isA(String.class))).willReturn(computingStrategyCircle);

        AbstractFigureView figureView = abstractFigureViewService.computeAreaAndPerimeterForFigureView(createFigureCommand);

        assertThat(figureView).isNotNull();
    }

    @Test
    public void computeAreaAndPerimeterForEditedFigureTest() {
        ComputingStrategy computingStrategyCircle = new ComputeCircle();
        List<BigDecimal> params = new ArrayList<>();
        params.add(BigDecimal.valueOf(50));

        given(computingStrategyFactory.findStrategy(ArgumentMatchers.isA(String.class))).willReturn(computingStrategyCircle);

        AbstractFigureView figureView = abstractFigureViewService.computeAreaAndPerimeterForEditedFigure("Circle", params);

        assertThat(figureView).isNotNull();
    }

    @Test
    public void editTest() {
        AbstractFigureView abstractFigureView = new AbstractFigureView();
        abstractFigureView.setModelId(1L);
        abstractFigureView.setPerimeter(BigDecimal.valueOf(320));
        abstractFigureView.setArea(BigDecimal.valueOf(6400));

        AbstractFigureView editedAbstractFigureView = abstractFigureView;
        editedAbstractFigureView.setPerimeter(BigDecimal.valueOf(360));
        editedAbstractFigureView.setArea(BigDecimal.valueOf(6800));

        given(abstractFigureViewRepository.save(ArgumentMatchers.isA(AbstractFigureView.class))).willReturn(editedAbstractFigureView);

        abstractFigureViewService.edit(abstractFigureView);
    }

}