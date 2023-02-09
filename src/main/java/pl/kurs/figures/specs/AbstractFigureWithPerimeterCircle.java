package pl.kurs.figures.specs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Circle;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AbstractFigureWithPerimeterCircle implements Specification<AbstractFigure> {

    @Nullable
    private BigDecimal perimeterFrom;

    @Nullable
    private BigDecimal perimeterTo;


    public AbstractFigureWithPerimeterCircle(@Nullable BigDecimal perimeterFrom, @Nullable BigDecimal perimeterTo) {
        this.perimeterFrom = perimeterFrom;
        this.perimeterTo = perimeterTo;
    }

    @Override
    public Predicate toPredicate(Root<AbstractFigure> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Root<Circle> circleRoot = criteriaBuilder.treat(root, Circle.class);

        Expression<BigDecimal> circlePerimeter = criteriaBuilder.function("calculatePerimeterCircle", BigDecimal.class, circleRoot.get("radius"));


        if (perimeterTo != null) {
            Predicate perimeterCircleTo = criteriaBuilder.lessThanOrEqualTo(circlePerimeter, perimeterTo);
            predicates.add(perimeterCircleTo);
        }

        if (perimeterFrom != null) {
            Predicate perimeterCircleFrom = criteriaBuilder.greaterThanOrEqualTo(circlePerimeter, perimeterFrom);
            predicates.add(perimeterCircleFrom);
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }


    @Nullable
    public BigDecimal getPerimeterTo() {
        return perimeterTo;
    }

    public void setPerimeterTo(@Nullable BigDecimal perimeterTo) {
        this.perimeterTo = perimeterTo;
    }

    @Nullable
    public BigDecimal getPerimeterFrom() {
        return perimeterFrom;
    }

    public void setPerimeterFrom(@Nullable BigDecimal perimeterFrom) {
        this.perimeterFrom = perimeterFrom;
    }
}
