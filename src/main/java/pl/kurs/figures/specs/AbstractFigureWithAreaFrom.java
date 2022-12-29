package pl.kurs.figures.specs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import pl.kurs.figures.model.AbstractFigure;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AbstractFigureWithAreaFrom implements Specification<AbstractFigure> {

    @Nullable
    private BigDecimal areaFrom;

    public AbstractFigureWithAreaFrom(BigDecimal areaFrom) {
        this.areaFrom = areaFrom;
    }

    @Override
    public Predicate toPredicate(Root<AbstractFigure> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (areaFrom != null) {
            Predicate area = criteriaBuilder.greaterThanOrEqualTo(root.get("area"), areaFrom);
            predicates.add(area);
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Nullable
    public BigDecimal getAreaFrom() {
        return areaFrom;
    }

    public void setAreaFrom(@Nullable BigDecimal areaFrom) {
        this.areaFrom = areaFrom;
    }
}
