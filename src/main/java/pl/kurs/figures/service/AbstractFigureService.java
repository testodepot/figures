package pl.kurs.figures.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.command.EditFigureCommand;
import pl.kurs.figures.dto.FigureDto;
import pl.kurs.figures.exception.BadEntityException;
import pl.kurs.figures.exception.EntityNotFoundException;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.User;
import pl.kurs.figures.repository.AbstractFigureBaseRepository;
import pl.kurs.figures.specs.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service(value = "abstractFigureService")
public class AbstractFigureService {

    private AbstractFigureBaseRepository repository;

    private ObjectMaker objectMaker;

    private UserService userService;

    public AbstractFigureService(AbstractFigureBaseRepository repository, ObjectMaker objectMaker, UserService userService) {
        this.repository = repository;
        this.objectMaker = objectMaker;
        this.userService = userService;
    }

    public AbstractFigure add(AbstractFigure figure) {
        return repository.save(figure);
    }

    public AbstractFigure edit(AbstractFigure figure) {
        return repository.save(Optional.ofNullable(figure)
                .filter(x -> Objects.nonNull(x.getId()))
                .orElseThrow(() -> new BadEntityException("AbstractFigure")));
    }

    public AbstractFigure getById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(id, "AbstractFigure"));
    }

    public boolean existsById(Long id) { return repository.existsById(id);}

    public AbstractFigure createSpecificFigure(CreateFigureCommand createFigureCommand) {
        AbstractFigure figureToSave = objectMaker.makeObject(createFigureCommand);
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User byLogin = userService.findByLogin(loggedInUsername);
        long millis = System.currentTimeMillis();
        figureToSave.setCreatedAt(new Date(millis));
        figureToSave.setLastModifiedAt(new Date(millis));
        figureToSave.setCreatedBy(byLogin);
        figureToSave.setArea(figureToSave.calculateAreaForFigure());
        figureToSave.setPerimeter(figureToSave.calculatePerimeterForFigure());
        byLogin.getCreatedFigures().add(figureToSave);
        return figureToSave;
    }

    public AbstractFigure createEditedFigure(EditFigureCommand editFigureCommand) {
        AbstractFigure byId = getById(editFigureCommand.getIdFigure());
        AbstractFigure editedFigure = objectMaker.makeObjectWithTypeAndParams(byId.getType(), editFigureCommand.getParameters());
        editedFigure.setId(byId.getId());
        editedFigure.setVersion(byId.getVersion());
        editedFigure.setCreatedAt(byId.getCreatedAt());
        long millis = System.currentTimeMillis();
        editedFigure.setLastModifiedAt(new Date(millis));
        editedFigure.setCreatedBy(byId.getCreatedBy());
        editedFigure.setLastModifiedBy(byId.getLastModifiedBy());
        editedFigure.setArea(editedFigure.calculateAreaForFigure());
        editedFigure.setPerimeter(editedFigure.calculatePerimeterForFigure());
        return editedFigure;
    }

    public List<Revision<Integer, AbstractFigure>> findRevisionsOfSpecificFigure(long id) {
        return repository.findRevisions(id).getContent();
    }


    public Page<FigureDto> findFiguresWithSpec(FindShapesQuery findShapesQuery, Pageable pageable) {
        Specification<AbstractFigure> perimeterSpecRectangle = new AbstractFigureWithPerimeterRectangle(findShapesQuery.getPerimeterFrom(), findShapesQuery.getPerimeterTo());
        Specification<AbstractFigure> areaSpecRectangle = new AbstractFigureWithAreaRectangle(findShapesQuery.getAreaFrom(), findShapesQuery.getAreaTo());
        AbstractFigureWithTypeEqual abstractfigureWithTypeEqual = new AbstractFigureWithTypeEqual(findShapesQuery.getType());
        AbstractFigureWithLengthFrom abstractFigureWithLengthFrom = new AbstractFigureWithLengthFrom(findShapesQuery.getLengthFrom());
        AbstractFigureWithLengthTo abstractFigureWithLengthTo = new AbstractFigureWithLengthTo(findShapesQuery.getLengthTo());
        AbstractFigureWithWidthFrom abstractFigureWithWidthFrom = new AbstractFigureWithWidthFrom(findShapesQuery.getWidthFrom());
        AbstractFigureWithWidthTo abstractFigureWithWidthTo = new AbstractFigureWithWidthTo(findShapesQuery.getWidthTo());
        AbstractFigureWithCreatedAtFrom abstractFigureWithCreatedAtFrom = new AbstractFigureWithCreatedAtFrom(findShapesQuery.getCreatedAtFrom());
        AbstractFigureWithCreatedAtTo abstractFigureWithCreatedAtTo = new AbstractFigureWithCreatedAtTo(findShapesQuery.getCreatedAtTo());
        Specification<AbstractFigure> perimeterSpecCircle = new AbstractFigureWithPerimeterCircle(findShapesQuery.getPerimeterFrom(), findShapesQuery.getPerimeterTo());
        Specification<AbstractFigure> areaSpecCircle = new AbstractFigureWithAreaCircle(findShapesQuery.getAreaFrom(), findShapesQuery.getAreaTo());
        AbstractFigureWithRadiusFrom abstractFigureWithRadiusFrom = new AbstractFigureWithRadiusFrom(findShapesQuery.getRadiusFrom());
        AbstractFigureWithRadiusTo abstractFigureWithRadiusTo = new AbstractFigureWithRadiusTo(findShapesQuery.getRadiusTo());
        Specification<AbstractFigure> areaSpecSquare = new AbstractFigureWithAreaSquare(findShapesQuery.getAreaFrom(), findShapesQuery.getAreaTo());
        Specification<AbstractFigure> perimeterSpecSquare = new AbstractFigureWithPerimeterSquare(findShapesQuery.getPerimeterFrom(), findShapesQuery.getPerimeterTo());
        AbstractFigureWithSideFrom abstractFigureWithSideFrom = new AbstractFigureWithSideFrom(findShapesQuery.getSideFrom());
        AbstractFigureWithSideTo abstractFigureWithSideTo = new AbstractFigureWithSideTo(findShapesQuery.getSideTo());

        Specification<AbstractFigure> rectangleSpec = Specification.where(abstractfigureWithTypeEqual)
                .and(areaSpecRectangle)
                .and(perimeterSpecRectangle)
                .and(abstractFigureWithLengthFrom)
                .and(abstractFigureWithLengthTo)
                .and(abstractFigureWithWidthFrom)
                .and(abstractFigureWithWidthTo);


        Specification<AbstractFigure> squareSpec = Specification.where(abstractfigureWithTypeEqual)
                .and(areaSpecSquare)
                .and(perimeterSpecSquare)
                .and(abstractFigureWithSideFrom)
                .and(abstractFigureWithSideTo);


        Specification<AbstractFigure> circleSpec = Specification.where(abstractfigureWithTypeEqual)
                .and(areaSpecCircle)
                .and(perimeterSpecCircle)
                .and(abstractFigureWithRadiusFrom)
                .and(abstractFigureWithRadiusTo);

        Specification<AbstractFigure> generalSpec = Specification.where(abstractFigureWithCreatedAtFrom)
                .and(abstractFigureWithCreatedAtTo);

        Specification<AbstractFigure> combinedSpec = generalSpec.and(rectangleSpec.or(squareSpec).or(circleSpec));

        return repository.findAll(combinedSpec, pageable).map(figure -> {
            FigureDto dto = new FigureDto();
            dto.setId(figure.getId());
            dto.setType(figure.getType());
            dto.setVersion(figure.getVersion());
            dto.setCreatedBy(figure.getCreatedBy().getLogin());
            dto.setCreatedAt(figure.getCreatedAt());
            dto.setLastModifiedAt(figure.getLastModifiedAt());
            dto.setLastModifiedBy(figure.getLastModifiedBy());
            dto.setArea(figure.calculateAreaForFigure().doubleValue());
            dto.setPerimeter(figure.calculatePerimeterForFigure().doubleValue());
            return dto;
        });
    }


    public List<FigureDto> createFigureRevisionsDto(List <Revision<Integer, AbstractFigure>> figuresRevisions, long id) {
        List<FigureDto> figureDtoList = new ArrayList<>();
        AbstractFigure byId = getById(id);
        Long version = byId.getVersion();
        String login = byId.getCreatedBy().getLogin();
        Long counter = 0L;
        for (Revision<Integer, AbstractFigure> revisionFigure : figuresRevisions) {
            if (counter <= version) {
                AbstractFigure entity = revisionFigure.getEntity();
                FigureDto figureDto = new FigureDto();
                figureDto.setId(entity.getId());
                figureDto.setVersion(counter);
                figureDto.setType(entity.getType());
                figureDto.setCreatedAt(entity.getCreatedAt());
                figureDto.setCreatedBy(login);
                figureDto.setLastModifiedAt(entity.getLastModifiedAt());
                figureDto.setLastModifiedBy(entity.getLastModifiedBy());
                figureDto.setArea(entity.calculateAreaForFigure().doubleValue());
                figureDto.setPerimeter(entity.calculatePerimeterForFigure().doubleValue());
                figureDtoList.add(figureDto);
                counter = counter + 1;
            }
        }
        return figureDtoList;
    }
}
