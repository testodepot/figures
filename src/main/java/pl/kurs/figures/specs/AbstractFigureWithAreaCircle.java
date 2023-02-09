package pl.kurs.figures.specs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import pl.kurs.figures.model.*;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AbstractFigureWithAreaCircle implements Specification<AbstractFigure> {

    @Nullable
    private BigDecimal areaFromCircle;

    @Nullable
    private BigDecimal areaToCircle;


    public AbstractFigureWithAreaCircle(@Nullable BigDecimal areaFromCircle, @Nullable BigDecimal areaToCircle) {
        this.areaFromCircle = areaFromCircle;
        this.areaToCircle = areaToCircle;
    }

    @Override
    public Predicate toPredicate(Root<AbstractFigure> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Root<Circle> circleRoot = criteriaBuilder.treat(root, Circle.class);

        Expression<BigDecimal> circleArea = criteriaBuilder.function("calculateAreaCircle", BigDecimal.class, circleRoot.get("radius"));


        if (areaFromCircle != null) {
            Predicate areaCircleFrom = criteriaBuilder.greaterThanOrEqualTo(circleArea, areaFromCircle);
            predicates.add(areaCircleFrom);
        }

        if (areaToCircle != null) {
            Predicate areaCircleTo = criteriaBuilder.lessThanOrEqualTo(circleArea, areaToCircle);
            predicates.add(areaCircleTo);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }


    @Nullable
    public BigDecimal getAreaFromCircle() {
        return areaFromCircle;
    }

    public void setAreaFromCircle(@Nullable BigDecimal areaFromCircle) {
        this.areaFromCircle = areaFromCircle;
    }

    @Nullable
    public BigDecimal getAreaToCircle() {
        return areaToCircle;
    }

    public void setAreaToCircle(@Nullable BigDecimal areaToCircle) {
        this.areaToCircle = areaToCircle;
    }
}
