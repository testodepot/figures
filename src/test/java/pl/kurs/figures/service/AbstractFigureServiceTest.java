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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.command.EditFigureCommand;
import pl.kurs.figures.exception.BadEntityException;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Rectangle;
import pl.kurs.figures.model.Square;
import pl.kurs.figures.model.User;
import pl.kurs.figures.repository.AbstractFigureBaseRepository;
import pl.kurs.figures.specs.FindShapesQuery;

import java.math.BigDecimal;
import java.util.*;

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

    @Mock
    private UserService userService;

    @Mock
    private Authentication auth;

    @BeforeEach
    public void setup() {
        auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("adam123");
        SecurityContextHolder.getContext().setAuthentication(auth);
        objectMaker = Mockito.mock(ObjectMaker.class);
        abstractFigureBaseRepository = Mockito.mock(AbstractFigureBaseRepository.class);
        userService = Mockito.mock(UserService.class);
        abstractFigureService = new AbstractFigureService(abstractFigureBaseRepository, objectMaker, userService);
    }


    @Test
    public void addTest() {
        User newUser = new User("adam", "kocik", "adam123", "123");
        newUser.setId(1);
        Set<AbstractFigure> figuresSet = new HashSet<>();
        newUser.setCreatedFigures(figuresSet);
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
        User newUser = new User("adam", "kocik", "adam123", "123");
        newUser.setId(1);
        Set<AbstractFigure> figuresSet = new HashSet<>();
        newUser.setCreatedFigures(figuresSet);

        given(objectMaker.makeObject(ArgumentMatchers.isA(CreateFigureCommand.class))).willReturn(rectangle);
        given(userService.findByLogin(ArgumentMatchers.anyString())).willReturn(newUser);

        Object specificFigure = abstractFigureService.createSpecificFigure(createFigureCommand);

        assertThat(specificFigure).isNotNull();
    }

    @Test
    public void createEditedFigureTest() {
        List<BigDecimal> params = new ArrayList<>();
        params.add(BigDecimal.valueOf(50));
        params.add(BigDecimal.valueOf(100));
        EditFigureCommand editFigureCommand = new EditFigureCommand(1L, params);
        Rectangle editedRectangle = new Rectangle(BigDecimal.valueOf(50), BigDecimal.valueOf(100));

        User newUser = new User("adam", "kocik", "adam123", "123");
        newUser.setId(1);
        Set<AbstractFigure> figuresSet = new HashSet<>();
        newUser.setCreatedFigures(figuresSet);

        Rectangle rectangle = new Rectangle(BigDecimal.valueOf(20), BigDecimal.valueOf(30));
        rectangle.setId(1L);
        rectangle.setType("Rectangle");
        rectangle.setVersion(0L);
        rectangle.setCreatedAt(new Date());
        rectangle.setCreatedBy(newUser);
        rectangle.setLastModifiedAt(new Date());
        rectangle.setLastModifiedBy("adam123");


        given(abstractFigureBaseRepository.findById(ArgumentMatchers.anyLong())).willReturn(java.util.Optional.of(rectangle));
        given(objectMaker.makeObjectWithTypeAndParams(ArgumentMatchers.anyString(), ArgumentMatchers.anyList())).willReturn(editedRectangle);

        Object specificFigure = abstractFigureService.createEditedFigure(editFigureCommand);

        assertThat(specificFigure).isNotNull();
    }




    @Test
    public void findAllTestShouldReturnPageOfFigures() {
        Pageable paging = PageRequest.of(0, 2);
        Page<AbstractFigure> page = Mockito.mock(Page.class);
        given(abstractFigureBaseRepository.findAll(ArgumentMatchers.isA(Specification.class), ArgumentMatchers.isA(Pageable.class))).willReturn(page);
        FindShapesQuery findShapesQuery = new FindShapesQuery();
        findShapesQuery.setType("Square");
        abstractFigureService.findFiguresWithSpec(findShapesQuery, paging);
        assertThat(page).isNotNull();
    }

    @Test
    public void editTestShouldThrowBadEntityException() {
        Rectangle rectangle = new Rectangle(BigDecimal.valueOf(50), BigDecimal.valueOf(100));
        rectangle.setLength(BigDecimal.valueOf(100));
        rectangle.setWidth(BigDecimal.valueOf(50));
        given(abstractFigureBaseRepository.save(ArgumentMatchers.isA(AbstractFigure.class))).willReturn(rectangle);

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

        Object editObject = abstractFigureService.createEditedFigure(editFigureCommand);
        assertThat(editObject).isNotNull();
    }


















}