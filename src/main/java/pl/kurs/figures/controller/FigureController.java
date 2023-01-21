package pl.kurs.figures.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.command.EditFigureCommand;
import pl.kurs.figures.dto.FigureDto;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.service.AbstractFigureService;
import pl.kurs.figures.specs.FindShapesQuery;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shapes")
public class FigureController {

    private AbstractFigureService abstractFigureService;

    private ModelMapper modelMapper;

    public FigureController(AbstractFigureService abstractFigureService, ModelMapper modelMapper) {
        this.abstractFigureService = abstractFigureService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<FigureDto> addFigure(@RequestBody @Valid CreateFigureCommand createFigureCommand) {
        AbstractFigure figureToSave = abstractFigureService.createSpecificFigure(createFigureCommand);
        abstractFigureService.add(figureToSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(figureToSave, FigureDto.class));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN') or @abstractFigureService.getById(#editFigureCommand.idFigure).createdBy.login.equals(principal.username)")
    public ResponseEntity<FigureDto> editFigure(@RequestBody @Valid EditFigureCommand editFigureCommand) {
        AbstractFigure editedFigure = abstractFigureService.createEditedFigure(editFigureCommand);
        abstractFigureService.edit(editedFigure);
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.map(editedFigure, FigureDto.class));
    }

    @GetMapping
    public ResponseEntity<Page<FigureDto>> findFigures(@Valid FindShapesQuery findShapesQuery, @PageableDefault Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(abstractFigureService.findFiguresWithSpec(findShapesQuery, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<FigureDto>> findChangesOfSpecificFigure(@PathVariable(name = "id") long id) {
        return ResponseEntity.status(HttpStatus.OK).body(abstractFigureService.createFigureRevisionsDto(abstractFigureService.findRevisionsOfSpecificFigure(id), id));
    }


}

