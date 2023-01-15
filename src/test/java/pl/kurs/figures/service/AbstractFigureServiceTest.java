package pl.kurs.figures.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.command.EditFigureCommand;
import pl.kurs.figures.exception.BadEntityException;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Rectangle;
import pl.kurs.figures.model.Square;
import pl.kurs.figures.repository.AbstractFigureBaseRepository;
import pl.kurs.figures.specs.AbstractFigureWithTypeEqual;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;


public class AbstractFigureServiceTest {

    @Mock
    private AbstractFigureBaseRepository abstractFigureBaseRepository;

    @Mock
    private ObjectMaker objectMaker;

    @InjectMocks
    private AbstractFigureService abstractFigureService;

    @BeforeEach
    public void setup() {
        objectMaker = Mockito.mock(ObjectMaker.class);
        abstractFigureBaseRepository = Mockito.mock(AbstractFigureBaseRepository.class);
        abstractFigureService = new AbstractFigureService(abstractFigureBaseRepository, objectMaker);
    }


    @Test
    public void addTest() {
        Square square = new Square(BigDecimal.valueOf(50));
        given(abstractFigureBaseRepository.save(ArgumentMatchers.isA(AbstractFigure.class))).willReturn(square);
        abstractFigureService.add(square);
        assertThat(square).isNotNull();
    }

    @Test
    public void createSpecificFigureTest() {
        List<BigDecimal> params = new ArrayList<>();
        params.add(BigDecimal.valueOf(50));
        params.add(BigDecimal.valueOf(100));
        CreateFigureCommand createFigureCommand = new CreateFigureCommand("Rectangle", params);
        Rectangle rectangle = new Rectangle(BigDecimal.valueOf(50), BigDecimal.valueOf(100));

        given(objectMaker.makeObject(ArgumentMatchers.isA(CreateFigureCommand.class))).willReturn(rectangle);

        Object specificFigure = abstractFigureService.createSpecificFigure(createFigureCommand);

        assertThat(specificFigure).isNotNull();
    }



    @Test
    public void findAllTestShouldReturnPageOfFigures() {
        AbstractFigureWithTypeEqual abstractFigureWithTypeEqual = new AbstractFigureWithTypeEqual("Square");
        Pageable paging = PageRequest.of(0, 2);
        Page<AbstractFigure> page = Mockito.mock(Page.class);
        given(abstractFigureBaseRepository.findAll(abstractFigureWithTypeEqual, paging)).willReturn(page);
        abstractFigureService.findAllList(abstractFigureWithTypeEqual);
        assertThat(page).isNotNull();
    }

    @Test
    public void editTestShouldThrowBadEntityException() {
        Rectangle rectangle = new Rectangle(BigDecimal.valueOf(50), BigDecimal.valueOf(100));
        Rectangle editedRectangle = rectangle;
        editedRectangle.setLength(BigDecimal.valueOf(100));
        editedRectangle.setWidth(BigDecimal.valueOf(50));
        given(abstractFigureBaseRepository.save(ArgumentMatchers.isA(AbstractFigure.class))).willReturn(editedRectangle);

        assertThrows(BadEntityException.class, () -> abstractFigureService.edit(rectangle));
    }

    @Test
    public void editObjectTest() {
        List<BigDecimal> params = new ArrayList<>();
        params.add(BigDecimal.valueOf(100));
        params.add(BigDecimal.valueOf(50));
        EditFigureCommand editFigureCommand = new EditFigureCommand(1L, params);
        Rectangle rectangle = new Rectangle(BigDecimal.valueOf(50), BigDecimal.valueOf(100));
        rectangle.setId(1L);
        rectangle.setType("Rectangle");
        Rectangle editedRectangle = rectangle;
        editedRectangle.setLength(BigDecimal.valueOf(100));
        editedRectangle.setWidth(BigDecimal.valueOf(50));

        given(abstractFigureBaseRepository.findById(ArgumentMatchers.anyLong())).willReturn(java.util.Optional.of(rectangle));
        given(objectMaker.makeObjectWithTypeAndParams(ArgumentMatchers.anyString(), ArgumentMatchers.anyList())).willReturn(editedRectangle);

        Object editObject = abstractFigureService.editObject(editFigureCommand);
        assertThat(editObject).isNotNull();
    }


















}