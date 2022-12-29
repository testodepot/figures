package pl.kurs.figures.specs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Circle;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AbstractFigureWithRadiusTo implements Specification<AbstractFigure> {

    @Nullable
    private BigDecimal radiusTo;

    public AbstractFigureWithRadiusTo(@Nullable BigDecimal radiusTo) {
        this.radiusTo = radiusTo;
    }

    @Override
    public Predicate toPredicate(Root<AbstractFigure> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Root<Circle> circleRoot = criteriaBuilder.treat(root, Circle.class);

        if (radiusTo != null) {
            Predicate area = criteriaBuilder.lessThanOrEqualTo(circleRoot.get("radius"), radiusTo);
            predicates.add(area);
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Nullable
    public BigDecimal getRadiusTo() {
        return radiusTo;
    }

    public void setRadiusTo(@Nullable BigDecimal radiusTo) {
        this.radiusTo = radiusTo;
    }
}
