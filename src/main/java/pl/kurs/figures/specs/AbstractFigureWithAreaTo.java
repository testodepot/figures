package pl.kurs.figures.specs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.AbstractFigureView;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AbstractFigureWithAreaTo implements Specification<AbstractFigure> {

    @Nullable
    private BigDecimal areaTo;

    public AbstractFigureWithAreaTo(@Nullable BigDecimal areaTo) {
        this.areaTo = areaTo;
    }

    @Override
    public Predicate toPredicate(Root<AbstractFigure> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        Join<AbstractFigure, AbstractFigureView> figures = root.join("abstractFigureView");
        if (areaTo != null) {
            Predicate area = criteriaBuilder.lessThanOrEqualTo(figures.get("area"), areaTo);
            predicates.add(area);
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Nullable
    public BigDecimal getAreaTo() {
        return areaTo;
    }

    public void setAreaTo(@Nullable BigDecimal areaTo) {
        this.areaTo = areaTo;
    }
}
