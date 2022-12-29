package pl.kurs.figures.specs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Rectangle;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AbstractFigureWithWidthTo implements Specification<AbstractFigure> {

    @Nullable
    private BigDecimal widthTo;

    public AbstractFigureWithWidthTo(@Nullable BigDecimal widthTo) {
        this.widthTo = widthTo;
    }

    @Override
    public Predicate toPredicate(Root<AbstractFigure> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Root<Rectangle> rectangleRoot = criteriaBuilder.treat(root, Rectangle.class);

        if (widthTo != null) {
            Predicate area = criteriaBuilder.lessThanOrEqualTo(rectangleRoot.get("width"), widthTo);
            predicates.add(area);
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Nullable
    public BigDecimal getWidthTo() {
        return widthTo;
    }

    public void setWidthTo(@Nullable BigDecimal widthTo) {
        this.widthTo = widthTo;
    }
}
