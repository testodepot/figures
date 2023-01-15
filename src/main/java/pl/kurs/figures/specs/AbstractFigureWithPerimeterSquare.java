package pl.kurs.figures.specs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Square;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AbstractFigureWithPerimeterSquare implements Specification<AbstractFigure> {

    @Nullable
    private BigDecimal perimeterFrom;

    @Nullable
    private BigDecimal perimeterTo;

    public AbstractFigureWithPerimeterSquare(@Nullable BigDecimal perimeterFrom, @Nullable BigDecimal perimeterTo) {
        this.perimeterFrom = perimeterFrom;
        this.perimeterTo = perimeterTo;
    }

    @Override
    public Predicate toPredicate(Root<AbstractFigure> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Root<Square> squareRoot = criteriaBuilder.treat(root, Square.class);
        Expression<BigDecimal> squarePerimeter = criteriaBuilder.function("calculatePerimeterSquare", BigDecimal.class, squareRoot.get("side"));

        if (perimeterFrom != null) {
            Predicate area = criteriaBuilder.greaterThanOrEqualTo(squarePerimeter, perimeterFrom);
            predicates.add(area);
        }

        if (perimeterTo != null) {
            Predicate areaTo = criteriaBuilder.lessThanOrEqualTo(squarePerimeter, perimeterTo);
            predicates.add(areaTo);
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
