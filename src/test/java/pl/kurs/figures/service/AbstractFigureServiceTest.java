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
import pl.kurs.figures.exception.NoPermissionException;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.repository.AbstractFigureBaseRepository;
import pl.kurs.figures.security.Role;
import pl.kurs.figures.security.User;
import pl.kurs.figures.specs.AbstractFigureWithTypeEqual;

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
        AbstractFigure figure = new AbstractFigure();
        figure.setId(1L);
        figure.setType("Square");
        given(abstractFigureBaseRepository.save(ArgumentMatchers.isA(AbstractFigure.class))).willReturn(figure);
        abstractFigureService.add(figure);
        assertThat(figure).isNotNull();
    }

    @Test
    public void checkIfUserHasPermissionToEditTestThrowsNoPermissionException() {
        User user = new User("adam", "kocik", "adam123", "123");
        Role admin = new Role("CREATOR");
        user.addRole(admin);
        AbstractFigure figure = new AbstractFigure();
        figure.setCreatedBy("tom123");
        figure.setId(1L);
        figure.setType("Square");
        assertThrows(NoPermissionException.class, () -> abstractFigureService.checkIfUserHasPermissionToEdit(user, figure));
    }

    @Test
    public void findAllTestShouldReturnPageOfFigures() {
        AbstractFigure figure = new AbstractFigure();
        figure.setCreatedBy("tom123");
        figure.setId(1L);
        figure.setType("Square");
        AbstractFigureWithTypeEqual abstractFigureWithTypeEqual = new AbstractFigureWithTypeEqual("Square");
        Pageable paging = PageRequest.of(0, 2);
        Page<AbstractFigure> page = Mockito.mock(Page.class);
        given(abstractFigureBaseRepository.findAll(abstractFigureWithTypeEqual, paging)).willReturn(page);
        abstractFigureService.findAll(abstractFigureWithTypeEqual, paging);
        assertThat(page).isNotNull();
    }
















}