package pl.kurs.figures.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.strategy.CreateRectangle;
import pl.kurs.figures.strategy.CreatingStrategy;
import pl.kurs.figures.strategy.CreatingStrategyFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


public class ObjectMakerTest {

    @Mock
    private CreatingStrategyFactory creatingStrategyFactory;

    @InjectMocks
    private ObjectMaker objectMaker;

    @BeforeEach
    public void setup() {
        creatingStrategyFactory = Mockito.mock(CreatingStrategyFactory.class);
        objectMaker = new ObjectMaker(creatingStrategyFactory);
    }

    @Test
    public void makeObjectTest() {
        CreatingStrategy creatingStrategyRectangle = new CreateRectangle();
        List<BigDecimal> params = new ArrayList<>();
        params.add(BigDecimal.valueOf(50));
        params.add(BigDecimal.valueOf(100));
        CreateFigureCommand createFigureCommand = new CreateFigureCommand("Rectangle", params);

        given(creatingStrategyFactory.findStrategy(ArgumentMatchers.isA(String.class))).willReturn(creatingStrategyRectangle);

        Object object = objectMaker.makeObject(createFigureCommand);

        assertThat(object).isNotNull();
    }


    @Test
    public void makeObjectWithTypeAndParamsTest() {
        CreatingStrategy creatingStrategyRectangle = new CreateRectangle();
        List<BigDecimal> params = new ArrayList<>();
        params.add(BigDecimal.valueOf(50));
        params.add(BigDecimal.valueOf(100));

        given(creatingStrategyFactory.findStrategy(ArgumentMatchers.isA(String.class))).willReturn(creatingStrategyRectangle);

        Object object = objectMaker.makeObjectWithTypeAndParams("Rectangle", params);

        assertThat(object).isNotNull();
    }






}