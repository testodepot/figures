package pl.kurs.figures.service;

import org.springframework.data.history.Revision;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.command.EditFigureCommand;
import pl.kurs.figures.dto.FigureDto;
import pl.kurs.figures.exception.BadEntityException;
import pl.kurs.figures.exception.EntityNotFoundException;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.User;
import pl.kurs.figures.repository.AbstractFigureBaseRepository;
import pl.kurs.figures.specs.FindShapesQuery;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service(value = "abstractFigureService")
public class AbstractFigureService {

    private AbstractFigureBaseRepository repository;

    private ObjectMaker objectMaker;

    public AbstractFigureService(AbstractFigureBaseRepository repository, ObjectMaker objectMaker) {
        this.repository = repository;
        this.objectMaker = objectMaker;
    }

    public AbstractFigure add(AbstractFigure figure) {
        return repository.save(figure);
    }

    public AbstractFigure edit(AbstractFigure figure) {
        return repository.save(Optional.ofNullable(figure)
                .filter(x -> Objects.nonNull(x.getId()))
                .orElseThrow(() -> new BadEntityException("AbstractFigure")));
    }


    public AbstractFigure createSpecificFigure(CreateFigureCommand createFigureCommand) {
        return objectMaker.makeObject(createFigureCommand);
    }

    public List<AbstractFigure> findAllList(Specification<AbstractFigure> figureSpecification) {
        return repository.findAll(figureSpecification);
    }

    public List<FigureDto> getFigureDtos(List<AbstractFigure> squareSpecList, List<AbstractFigure> circleSpecList, List<AbstractFigure> rectangleSpecList) {
        List<AbstractFigure> figuresAll = new ArrayList<>();
        Stream.of(squareSpecList, circleSpecList, rectangleSpecList).forEach(figuresAll::addAll);
        HashSet<AbstractFigure> abstractFigures = new HashSet<>(figuresAll);
        return abstractFigures
                .stream()
                .map(figure -> {
                    FigureDto dto = new FigureDto();
                    dto.setId(figure.getId());
                    dto.setType(figure.getType());
                    dto.setVersion(figure.getVersion());
                    dto.setCreatedBy(figure.getCreatedBy().getLogin());
                    dto.setCreatedAt(figure.getCreatedAt());
                    dto.setLastModifiedAt(figure.getLastModifiedAt());
                    dto.setLastModifiedBy(figure.getLastModifiedBy());
                    dto.setArea(figure.getArea().doubleValue());
                    dto.setPerimeter(figure.getPerimeter().doubleValue());
                    return dto;
                }).collect(Collectors.toList());
    }

    public boolean existsById(Long id) { return repository.existsById(id);}

    public void prepareFigureForSave(AbstractFigure figure, User createdBy) {
        long millis = System.currentTimeMillis();
        figure.setCreatedAt(new Date(millis));
        figure.setLastModifiedAt(new Date(millis));
        figure.setCreatedBy(createdBy);
    }

    public void prepareEditedFigureForSave(AbstractFigure editedFigure, AbstractFigure figureById) {
        long millis = System.currentTimeMillis();
        editedFigure.setId(figureById.getId());
        editedFigure.setVersion(figureById.getVersion());
        editedFigure.setType(figureById.getType());
        editedFigure.setCreatedAt(figureById.getCreatedAt());
        editedFigure.setLastModifiedAt(new Date(millis));
        editedFigure.setCreatedBy(figureById.getCreatedBy());
        editedFigure.setLastModifiedBy(figureById.getLastModifiedBy());
    }


    public AbstractFigure getById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(id, "AbstractFigure"));
    }


    public List<Revision<Integer, AbstractFigure>> findRevisionsOfSpecificFigure(long id) {
        return repository.findRevisions(id).getContent();
    }


    public AbstractFigure editObject(EditFigureCommand editFigureCommand) {
        String type = getById(editFigureCommand.getIdFigure()).getType();
        return objectMaker.makeObjectWithTypeAndParams(type, editFigureCommand.getParameters());
    }

    public List<AbstractFigure> getRectangles(FindShapesQuery findShapesQuery, Specification<AbstractFigure> rectangleSpec, Specification<AbstractFigure> generalSpec, List<AbstractFigure> rectangleSpecList) {
        if(findShapesQuery.getType() != null || findShapesQuery.getAreaFrom() != null
                || findShapesQuery.getAreaTo() != null || findShapesQuery.getPerimeterFrom() != null
                || findShapesQuery.getPerimeterTo() != null || findShapesQuery.getCreatedAtFrom() != null || findShapesQuery.getCreatedAtTo() != null
                || findShapesQuery.getWidthFrom() != null || findShapesQuery.getWidthTo() != null || findShapesQuery.getLengthFrom() != null
                || findShapesQuery.getLengthTo() != null) {
            rectangleSpecList = findAllList(generalSpec.and(rectangleSpec));
        }
        return rectangleSpecList;
    }

    public List<AbstractFigure> getSquares(FindShapesQuery findShapesQuery, Specification<AbstractFigure> squareSpec, Specification<AbstractFigure> generalSpec, List<AbstractFigure> squareSpecList) {
        if(findShapesQuery.getType() != null || findShapesQuery.getAreaFrom() != null
                || findShapesQuery.getAreaTo() != null || findShapesQuery.getPerimeterFrom() != null
                || findShapesQuery.getPerimeterTo() != null || findShapesQuery.getCreatedAtFrom() != null || findShapesQuery.getCreatedAtTo() != null
                || findShapesQuery.getSideFrom() != null || findShapesQuery.getSideTo() != null) {
            squareSpecList = findAllList(generalSpec.and(squareSpec));
        }
        return squareSpecList;
    }

    public List<AbstractFigure> getCircles(FindShapesQuery findShapesQuery, Specification<AbstractFigure> circleSpec, Specification<AbstractFigure> generalSpec, List<AbstractFigure> circleSpecList) {
        if(findShapesQuery.getType() != null || findShapesQuery.getAreaFrom() != null
                || findShapesQuery.getAreaTo() != null || findShapesQuery.getPerimeterFrom() != null
                || findShapesQuery.getPerimeterTo() != null || findShapesQuery.getCreatedAtFrom() != null || findShapesQuery.getCreatedAtTo() != null ||
                findShapesQuery.getRadiusFrom() != null || findShapesQuery.getRadiusTo() != null)  {
            circleSpecList = findAllList(generalSpec.and(circleSpec));
        }
        return circleSpecList;
    }

    public List<FigureDto> createFiguresRevisionDto (List <Revision<Integer, AbstractFigure>> figuresRevisions, long id) {
        List<FigureDto> figureDtoList = new ArrayList<>();
        Long version = getById(id).getVersion();
        Long counter = 0L;
        for (Revision<Integer, AbstractFigure> revisionFigure : figuresRevisions) {
            if (counter <= version) {
                AbstractFigure entity = revisionFigure.getEntity();
                entity.postLoad();
                FigureDto figureDto = new FigureDto();
                figureDto.setId(entity.getId());
                figureDto.setVersion(counter);
                figureDto.setType(entity.getType());
                figureDto.setCreatedBy(entity.getCreatedBy().getLogin());
                figureDto.setCreatedAt(entity.getCreatedAt());
                figureDto.setLastModifiedAt(entity.getLastModifiedAt());
                figureDto.setLastModifiedBy(entity.getLastModifiedBy());
                figureDto.setArea(entity.getArea().doubleValue());
                figureDto.setPerimeter(entity.getPerimeter().doubleValue());
                figureDtoList.add(figureDto);
                counter = counter + 1;
            }
        }
        return figureDtoList;
    }








}
