package pl.kurs.figures.specs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.AbstractFigureView;

import javax.persistence.criteria.*;
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
        Join<AbstractFigure, AbstractFigureView> figures = root.join("abstractFigureView");
        if (areaFrom != null) {
            Predicate area = criteriaBuilder.greaterThanOrEqualTo(figures.get("area"), areaFrom);
            predicates.add(area);
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }


//    @Override
//    public Specification<AbstractFigureView> and(Specification<AbstractFigureView> other) {
//        return null;
//    }

//    @Override
//    public Predicate toPredicate(Root<AbstractFigureView> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
//        List<Predicate> predicates = new ArrayList<>();
//        if (areaFrom != null) {
//            Predicate area = criteriaBuilder.greaterThanOrEqualTo(root.get("area"), areaFrom);
//            predicates.add(area);
//        }
//        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//    }

    @Nullable
    public BigDecimal getAreaFrom() {
        return areaFrom;
    }

    public void setAreaFrom(@Nullable BigDecimal areaFrom) {
        this.areaFrom = areaFrom;
    }
}
