package pl.kurs.figures.specs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Square;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AbstractFigureWithSideTo implements Specification<AbstractFigure> {

    @Nullable
    private BigDecimal sideTo;

    public AbstractFigureWithSideTo(@Nullable BigDecimal sideTo) {
        this.sideTo = sideTo;
    }

    @Override
    public Predicate toPredicate(Root<AbstractFigure> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Root<Square> squareRoot = criteriaBuilder.treat(root, Square.class);

        if (sideTo != null) {
            Predicate area = criteriaBuilder.lessThanOrEqualTo(squareRoot.get("side"), sideTo);
            predicates.add(area);
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Nullable
    public BigDecimal getSideTo() {
        return sideTo;
    }

    public void setSideTo(@Nullable BigDecimal sideTo) {
        this.sideTo = sideTo;
    }
}
