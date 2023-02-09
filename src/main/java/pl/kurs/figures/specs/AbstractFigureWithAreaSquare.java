package pl.kurs.figures.specs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Square;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AbstractFigureWithAreaSquare implements Specification<AbstractFigure> {

    @Nullable
    private BigDecimal areaFromSquare;

    @Nullable
    private BigDecimal areaToSquare;

    public AbstractFigureWithAreaSquare(@Nullable BigDecimal areaFromSquare, @Nullable BigDecimal areaToSquare) {
        this.areaFromSquare = areaFromSquare;
        this.areaToSquare = areaToSquare;
    }

    @Override
    public Predicate toPredicate(Root<AbstractFigure> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Root<Square> squareRoot = criteriaBuilder.treat(root, Square.class);

        Expression<BigDecimal> squareArea = criteriaBuilder.function("calculateAreaSquare", BigDecimal.class, squareRoot.get("side"));

        if (areaFromSquare != null) {
            Predicate areaSquare = criteriaBuilder.greaterThanOrEqualTo(squareArea, areaFromSquare);
            predicates.add(areaSquare);
        }

        if (areaToSquare != null) {
            Predicate areaSquareTo = criteriaBuilder.lessThanOrEqualTo(squareArea, areaToSquare);
            predicates.add(areaSquareTo);
        }


        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }


    @Nullable
    public BigDecimal getAreaFromSquare() {
        return areaFromSquare;
    }

    public void setAreaFromSquare(@Nullable BigDecimal areaFromSquare) {
        this.areaFromSquare = areaFromSquare;
    }

    @Nullable
    public BigDecimal getAreaToSquare() {
        return areaToSquare;
    }

    public void setAreaToSquare(@Nullable BigDecimal areaToSquare) {
        this.areaToSquare = areaToSquare;
    }
}
