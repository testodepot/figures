package pl.kurs.figures.specs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Rectangle;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AbstractFigureWithPerimeterRectangle implements Specification<AbstractFigure> {

    @Nullable
    private BigDecimal perimeterFrom;

    @Nullable
    private BigDecimal perimeterTo;

    public AbstractFigureWithPerimeterRectangle(@Nullable BigDecimal perimeterFrom, @Nullable BigDecimal perimeterTo) {
        this.perimeterFrom = perimeterFrom;
        this.perimeterTo = perimeterTo;
    }


    @Override
    public Predicate toPredicate(Root<AbstractFigure> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Root<Rectangle> rectangleRoot = criteriaBuilder.treat(root, Rectangle.class);

        Expression<BigDecimal> perimeterRectangle = criteriaBuilder.function("calculatePerimeterRectangle", BigDecimal.class, rectangleRoot.get("width"), rectangleRoot.get("length"));

        if (perimeterTo != null) {
            Predicate perimeterRectangleTo = criteriaBuilder.lessThanOrEqualTo(perimeterRectangle, perimeterTo);
            predicates.add(perimeterRectangleTo);
        }

        if (perimeterFrom != null) {
            Predicate perimeterRectangleFrom = criteriaBuilder.greaterThanOrEqualTo(perimeterRectangle, perimeterFrom);
            predicates.add(perimeterRectangleFrom);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Nullable
    public BigDecimal getPerimeterFrom() {
        return perimeterFrom;
    }

    public void setPerimeterFrom(@Nullable BigDecimal perimeterFrom) {
        this.perimeterFrom = perimeterFrom;
    }

    @Nullable
    public BigDecimal getPerimeterTo() {
        return perimeterTo;
    }

    public void setPerimeterTo(@Nullable BigDecimal perimeterTo) {
        this.perimeterTo = perimeterTo;
    }
}
