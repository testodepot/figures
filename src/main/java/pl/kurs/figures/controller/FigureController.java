package pl.kurs.figures.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.dto.FigureDto;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.service.AbstractFigureService;
import pl.kurs.figures.specs.*;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.sql.Date;

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
    public ResponseEntity<FigureDto> addFigure(@RequestBody @Valid CreateFigureCommand createFigureCommand) throws ClassNotFoundException {
        Object figure = abstractFigureService.createSpecificFigure(createFigureCommand);
        long millis = System.currentTimeMillis();
        AbstractFigure preSavedFigure = modelMapper.map(figure, AbstractFigure.class);
        preSavedFigure.setCreatedAt(new Date(millis));
        preSavedFigure.setLastModifiedAt(new Date(millis));
        AbstractFigure savedFigure = abstractFigureService.add(preSavedFigure);
        FigureDto figureDto = modelMapper.map(savedFigure, FigureDto.class);
        return new ResponseEntity(figureDto, HttpStatus.CREATED);
    }


    @GetMapping
    public Page<FigureDto> findFigures(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "areaFrom", required = false) BigDecimal areaFrom,
            @RequestParam(value = "areaTo", required = false) BigDecimal areaTo,
            @RequestParam(value = "perimeterFrom", required = false) BigDecimal perimeterFrom,
            @RequestParam(value = "perimeterTo", required = false) BigDecimal perimeterTo,
            @RequestParam(value = "radiusFrom", required = false) BigDecimal radiusFrom,
            @RequestParam(value = "radiusTo", required = false) BigDecimal radiusTo,
            @RequestParam(value = "createdAtFrom", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String createdAtFrom,
            @RequestParam(value = "createdAtTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String createdAtTo,
            @RequestParam(value = "lengthFrom", required = false) BigDecimal lengthFrom,
            @RequestParam(value = "lengthTo", required = false) BigDecimal lengthTo,
            @RequestParam(value = "widthFrom", required = false) BigDecimal widthFrom,
            @RequestParam(value = "widthTo", required = false) BigDecimal widthTo,
            @RequestParam(value = "sideFrom", required = false) BigDecimal sideFrom,
            @RequestParam(value = "sideTo", required = false) BigDecimal sideTo,
            @PageableDefault Pageable pageable) {
        Specification<AbstractFigure> spec = Specification.where(new AbstractFigureWithTypeEqual(type))
                .and(new AbstractFigureWithAreaFrom(areaFrom))
                .and(new AbstractFigureWithAreaTo(areaTo))
                .and(new AbstractFigureWithPerimeterFrom(perimeterFrom))
                .and(new AbstractFigureWithPerimeterTo(perimeterTo))
                .and(new AbstractFigureWithRadiusFrom(radiusFrom))
                .and(new AbstractFigureWithRadiusTo(radiusTo))
                .and(new AbstractFigureWithCreatedAtFrom(createdAtFrom))
                .and(new AbstractFigureWithCreatedAtTo(createdAtTo))
                .and(new AbstractFigureWithLengthFrom(lengthFrom))
                .and(new AbstractFigureWithLengthTo(lengthTo))
                .and(new AbstractFigureWithWidthFrom(widthFrom))
                .and(new AbstractFigureWithWidthTo(widthTo))
                .and(new AbstractFigureWithSideFrom(sideFrom))
                .and(new AbstractFigureWithSideTo(sideTo));

        Page<AbstractFigure> selectedFigures = abstractFigureService.findAll(spec, pageable);
        return toPageObjectDto(selectedFigures);
    }

    public Page<FigureDto> toPageObjectDto(Page<AbstractFigure> objects) {
        Page<FigureDto> dtos  = objects.map(this::convertToObjectDto);
        return dtos;
    }

    private FigureDto convertToObjectDto(AbstractFigure figure) {
        FigureDto dto = modelMapper.map(figure, FigureDto.class);
        return dto;
    }

}
