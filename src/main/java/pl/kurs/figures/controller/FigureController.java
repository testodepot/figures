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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.command.EditFigureCommand;
import pl.kurs.figures.dto.FigureDto;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.AbstractFigureView;
import pl.kurs.figures.security.User;
import pl.kurs.figures.security.UserService;
import pl.kurs.figures.service.AbstractFigureService;
import pl.kurs.figures.service.AbstractFigureViewService;
import pl.kurs.figures.service.ObjectMaker;
import pl.kurs.figures.specs.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/shapes")
public class FigureController {

    private AbstractFigureService abstractFigureService;

    private ModelMapper modelMapper;

    private UserService userService;

    private AbstractFigureViewService abstractFigureViewService;

    private ObjectMaker objectMaker;


    public FigureController(AbstractFigureService abstractFigureService, ModelMapper modelMapper, UserService userService, AbstractFigureViewService abstractFigureViewService, ObjectMaker objectMaker) {
        this.abstractFigureService = abstractFigureService;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.abstractFigureViewService = abstractFigureViewService;
        this.objectMaker = objectMaker;
    }

    @PostMapping
    public ResponseEntity<FigureDto> addFigure(@RequestBody @Valid CreateFigureCommand createFigureCommand) {
        Object figure = abstractFigureService.createSpecificFigure(createFigureCommand);
        long millis = System.currentTimeMillis();
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User byLogin = userService.findByLogin(loggedInUsername);
        AbstractFigureView abstractFigureView = abstractFigureViewService.computeAreaAndPerimeterForFigureView(createFigureCommand);
        //saving figure
        AbstractFigure preSavedFigure = modelMapper.map(figure, AbstractFigure.class);
        preSavedFigure.setCreatedAt(new Date(millis));
        preSavedFigure.setLastModifiedAt(new Date(millis));
        preSavedFigure.setAbstractFigureView(abstractFigureView);
        AbstractFigure savedFigure = abstractFigureService.add(preSavedFigure);
        //editing user's figures
        byLogin.getCreatedFigures().add(savedFigure);
        userService.edit(byLogin);
        //mapping to dto
        FigureDto figureDto = modelMapper.map(savedFigure, FigureDto.class);
        figureDto.setArea(savedFigure.getAbstractFigureView().getArea().doubleValue());
        figureDto.setPerimeter(savedFigure.getAbstractFigureView().getPerimeter().doubleValue());
        return new ResponseEntity(figureDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageImpl<FigureDto>> findFigures(@Valid FindShapesQuery findShapesQuery,
                                                           @PageableDefault Pageable pageable) {
        Specification<AbstractFigure> spec = Specification.where(
                new AbstractFigureWithTypeEqual(findShapesQuery.getType()))
                .and(new AbstractFigureWithAreaFrom(findShapesQuery.getAreaFrom()))
                .and(new AbstractFigureWithAreaTo(findShapesQuery.getAreaTo()))
                .and(new AbstractFigureWithPerimeterFrom(findShapesQuery.getPerimeterFrom()))
                .and(new AbstractFigureWithPerimeterTo(findShapesQuery.getPerimeterTo()))
                .and(new AbstractFigureWithRadiusFrom(findShapesQuery.getRadiusFrom()))
                .and(new AbstractFigureWithRadiusTo(findShapesQuery.getRadiusTo()))
                .and(new AbstractFigureWithCreatedAtFrom(findShapesQuery.getCreatedAtFrom()))
                .and(new AbstractFigureWithCreatedAtTo(findShapesQuery.getCreatedAtTo()))
                .and(new AbstractFigureWithLengthFrom(findShapesQuery.getLengthFrom()))
                .and(new AbstractFigureWithLengthTo(findShapesQuery.getLengthTo()))
                .and(new AbstractFigureWithWidthFrom(findShapesQuery.getWidthFrom()))
                .and(new AbstractFigureWithWidthTo(findShapesQuery.getWidthTo()))
                .and(new AbstractFigureWithSideFrom(findShapesQuery.getSideFrom()))
                .and(new AbstractFigureWithSideTo(findShapesQuery.getSideTo()));

        Page<AbstractFigure> selectedFigures = abstractFigureService.findAll(spec, pageable);
        Page<FigureDto> selectedFiguresDto = toPageObjectDto(selectedFigures);
        List<FigureDto> figureDtoList = selectedFiguresDto.stream().collect(Collectors.toList());
        setAreaAndPerimeterForFigures(figureDtoList);
        PageImpl<FigureDto> page = new PageImpl<>(figureDtoList, pageable, pageable.getOffset());
        return ResponseEntity.status(HttpStatus.OK).body(page);
    }


    @GetMapping("/{id}")
    public ResponseEntity<List<FigureDto>> findChangesOfSpecificFigure(@PathVariable(name = "id") long id) {
        List<Revision<Integer, AbstractFigure>> revisionsOfSpecificFigure = abstractFigureService.findRevisionsOfSpecificFigure(id);
        List<Revision<Integer, AbstractFigureView>> areaAndPerimeterOfSpecificFigure = abstractFigureViewService.findAreaAndPerimeterOfSpecificFigure(id);
        List<FigureDto> figureDtoList = createFiguresRevisionDto(revisionsOfSpecificFigure, areaAndPerimeterOfSpecificFigure, id);
        return ResponseEntity.status(HttpStatus.OK).body(figureDtoList);
    }

    @PutMapping
    public ResponseEntity<FigureDto> editFigure(@RequestBody @Valid EditFigureCommand editFigureCommand) {
        AbstractFigure figureById = abstractFigureService.getById(editFigureCommand.getIdFigure());
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User byLogin = userService.findByLogin(loggedInUsername);
        abstractFigureService.checkIfUserHasPermissionToEdit(byLogin, figureById);
        long millis = System.currentTimeMillis();
        Object editObject = abstractFigureService.editObject(editFigureCommand);
        AbstractFigure editedFigure = modelMapper.map(editObject, AbstractFigure.class);
        editedFigure.setId(figureById.getId());
        editedFigure.setVersion(figureById.getVersion());
        editedFigure.setType(figureById.getType());
        editedFigure.setCreatedAt(figureById.getCreatedAt());
        editedFigure.setLastModifiedAt(new Date(millis));
        editedFigure.setCreatedBy(figureById.getCreatedBy());
        editedFigure.setLastModifiedBy(byLogin.getLogin());
        //////
        AbstractFigureView editedFigureView = abstractFigureViewService.computeAreaAndPerimeterForEditedFigure(figureById.getType(), editFigureCommand.getParameters());
        editedFigureView.setModelId(figureById.getId());
        //////
        editedFigure.setAbstractFigureView(editedFigureView);
        AbstractFigureView savedEditedFigureView = abstractFigureViewService.edit(editedFigureView);
        AbstractFigure savedEditedFigure = abstractFigureService.edit(editedFigure);
        FigureDto savedEditedFigureDto = modelMapper.map(savedEditedFigure, FigureDto.class);
        savedEditedFigureDto.setArea(savedEditedFigureView.getArea().doubleValue());
        savedEditedFigureDto.setPerimeter(savedEditedFigureView.getPerimeter().doubleValue());
        return ResponseEntity.status(HttpStatus.OK).body(savedEditedFigureDto);
    }

    public Page<FigureDto> toPageObjectDto(Page<AbstractFigure> objects) {
        Page<FigureDto> dtos = objects.map(this::convertToObjectDto);
        return dtos;
    }

    private FigureDto convertToObjectDto(AbstractFigure figure) {
        FigureDto dto = modelMapper.map(figure, FigureDto.class);
        return dto;
    }

    private void setAreaAndPerimeterForFigures(List<FigureDto> figureDtoList) {
        for (FigureDto u : figureDtoList) {
            u.setArea(abstractFigureViewService.getById(u.getId()).getArea().doubleValue());
            u.setPerimeter(abstractFigureViewService.getById(u.getId()).getPerimeter().doubleValue());
        }
    }


    private List<FigureDto> createFiguresRevisionDto (List <Revision<Integer, AbstractFigure>> figuresRevisions,
                                                      List <Revision<Integer, AbstractFigureView>> correspondingRevisionsOfAreaAndPerimeter, long id) {
            List<FigureDto> figureDtoList = new ArrayList<>();
            Long version = abstractFigureService.getById(id).getVersion();
            Long counter = 0L;
            for (Revision<Integer, AbstractFigureView> revisionAreaPerimeter : correspondingRevisionsOfAreaAndPerimeter) {
                for (Revision<Integer, AbstractFigure> revisionFigure : figuresRevisions) {
                    if (revisionFigure.getRequiredRevisionNumber() == revisionAreaPerimeter.getRequiredRevisionNumber() + 1 && counter <= version) {
                        AbstractFigure entity = revisionFigure.getEntity();
                        FigureDto figureDto = modelMapper.map(entity, FigureDto.class);
                        figureDto.setVersion(counter);
                        figureDto.setArea(revisionAreaPerimeter.getEntity().getArea().doubleValue());
                        figureDto.setPerimeter(revisionAreaPerimeter.getEntity().getPerimeter().doubleValue());
                        figureDtoList.add(figureDto);
                        counter = counter + 1;
                    }
                }
            }
            return figureDtoList;
        }
}

