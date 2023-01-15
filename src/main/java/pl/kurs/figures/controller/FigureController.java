package pl.kurs.figures.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.command.EditFigureCommand;
import pl.kurs.figures.dto.FigureDto;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.User;
import pl.kurs.figures.service.AbstractFigureService;
import pl.kurs.figures.service.UserService;
import pl.kurs.figures.specs.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/shapes")
public class FigureController {

    private AbstractFigureService abstractFigureService;

    private ModelMapper modelMapper;

    private UserService userService;

    public FigureController(AbstractFigureService abstractFigureService, ModelMapper modelMapper, UserService userService) {
        this.abstractFigureService = abstractFigureService;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<FigureDto> addFigure(@RequestBody @Valid CreateFigureCommand createFigureCommand) {
        AbstractFigure figure = abstractFigureService.createSpecificFigure(createFigureCommand);
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User byLogin = userService.findByLogin(loggedInUsername);
        abstractFigureService.prepareFigureForSave(figure, byLogin);
        AbstractFigure savedFigure = abstractFigureService.add(figure);
        Set<AbstractFigure> createdFigures = byLogin.getCreatedFigures();
        createdFigures.add(savedFigure);
        userService.edit(byLogin);
        FigureDto figureDto = modelMapper.map(savedFigure, FigureDto.class);
        figureDto.setPerimeter(savedFigure.calculatePerimeterForFigure(createFigureCommand.getParameters()).doubleValue());
        figureDto.setArea(savedFigure.calculateAreaForFigure(createFigureCommand.getParameters()).doubleValue());
        return ResponseEntity.status(HttpStatus.CREATED).body(figureDto);
    }

    @GetMapping
    public ResponseEntity<Page<FigureDto>> findFigures(@Valid FindShapesQuery findShapesQuery, @PageableDefault Pageable pageable) {
        Specification<AbstractFigure> areaSpecCircle = new AbstractFigureWithAreaCircle(findShapesQuery.getAreaFrom(), findShapesQuery.getAreaTo());
        Specification<AbstractFigure> areaSpecSquare = new AbstractFigureWithAreaSquare(findShapesQuery.getAreaFrom(), findShapesQuery.getAreaTo());
        Specification<AbstractFigure> areaSpecRectangle = new AbstractFigureWithAreaRectangle(findShapesQuery.getAreaFrom(), findShapesQuery.getAreaTo());
        Specification<AbstractFigure> perimeterSpecSquare = new AbstractFigureWithPerimeterSquare(findShapesQuery.getPerimeterFrom(), findShapesQuery.getPerimeterTo());
        Specification<AbstractFigure> perimeterSpecCircle = new AbstractFigureWithPerimeterCircle(findShapesQuery.getPerimeterFrom(), findShapesQuery.getPerimeterTo());
        Specification<AbstractFigure> perimeterSpecRectangle = new AbstractFigureWithPerimeterRectangle(findShapesQuery.getPerimeterFrom(), findShapesQuery.getPerimeterTo());

        Specification<AbstractFigure> squareSpec = Specification.where(new AbstractFigureWithTypeEqual(findShapesQuery.getType())
                .and(areaSpecSquare)
                .and(perimeterSpecSquare)
                .and(new AbstractFigureWithSideFrom(findShapesQuery.getSideFrom()))
                .and(new AbstractFigureWithSideTo(findShapesQuery.getSideTo())));

        Specification<AbstractFigure> circleSpec = Specification.where(new AbstractFigureWithTypeEqual(findShapesQuery.getType())
                .and(areaSpecCircle)
                .and(perimeterSpecCircle)
                .and(new AbstractFigureWithRadiusFrom(findShapesQuery.getRadiusFrom()))
                .and(new AbstractFigureWithRadiusTo(findShapesQuery.getRadiusTo())));

        Specification<AbstractFigure> rectangleSpec = Specification.where(new AbstractFigureWithTypeEqual(findShapesQuery.getType())
                .and(areaSpecRectangle)
                .and(perimeterSpecRectangle)
                .and(new AbstractFigureWithLengthFrom(findShapesQuery.getLengthFrom()))
                .and(new AbstractFigureWithLengthTo(findShapesQuery.getLengthTo()))
                .and(new AbstractFigureWithWidthFrom(findShapesQuery.getWidthFrom()))
                .and(new AbstractFigureWithWidthTo(findShapesQuery.getWidthTo())));

        Specification<AbstractFigure> generalSpec = Specification.where(new AbstractFigureWithCreatedAtFrom(findShapesQuery.getCreatedAtFrom())
                .and(new AbstractFigureWithCreatedAtTo(findShapesQuery.getCreatedAtTo())));

        List<AbstractFigure> squareSpecList = new ArrayList<>();
        List<AbstractFigure> circleSpecList = new ArrayList<>();
        List<AbstractFigure> rectangleSpecList = new ArrayList<>();
        squareSpecList = abstractFigureService.getSquares(findShapesQuery, squareSpec, generalSpec, squareSpecList);
        circleSpecList = abstractFigureService.getCircles(findShapesQuery, circleSpec, generalSpec, circleSpecList);
        rectangleSpecList = abstractFigureService.getRectangles(findShapesQuery, rectangleSpec, generalSpec, rectangleSpecList);
        List<FigureDto> dtoList = abstractFigureService.getFigureDtos(squareSpecList, circleSpecList, rectangleSpecList);
        PageImpl<FigureDto> figureDtoPage = new PageImpl<>(dtoList, pageable, dtoList.size());
        return ResponseEntity.status(HttpStatus.OK).body(figureDtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<FigureDto>> findChangesOfSpecificFigure(@PathVariable(name = "id") long id) {
        List<Revision<Integer, AbstractFigure>> revisionsOfSpecificFigure = abstractFigureService.findRevisionsOfSpecificFigure(id);
        List<FigureDto> figureDtoList = abstractFigureService.createFiguresRevisionDto(revisionsOfSpecificFigure, id);
        return ResponseEntity.status(HttpStatus.OK).body(figureDtoList);
    }


    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN') or @abstractFigureService.getById(#editFigureCommand.idFigure).createdBy.login.equals(principal.username)")
    public ResponseEntity<FigureDto> editFigure(@RequestBody @Valid EditFigureCommand editFigureCommand) {
        AbstractFigure figureById = abstractFigureService.getById(editFigureCommand.getIdFigure());
        AbstractFigure editedFigure = abstractFigureService.editObject(editFigureCommand);
        abstractFigureService.prepareEditedFigureForSave(editedFigure, figureById);
        AbstractFigure savedEditedFigure = abstractFigureService.edit(editedFigure);
        FigureDto savedEditedFigureDto = modelMapper.map(savedEditedFigure, FigureDto.class);
        savedEditedFigureDto.setArea(savedEditedFigure.calculateAreaForFigure(editFigureCommand.getParameters()).doubleValue());
        savedEditedFigureDto.setPerimeter(savedEditedFigure.calculatePerimeterForFigure(editFigureCommand.getParameters()).doubleValue());
        return ResponseEntity.status(HttpStatus.OK).body(savedEditedFigureDto);
    }


}

