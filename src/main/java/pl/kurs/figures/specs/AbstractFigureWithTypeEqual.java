package pl.kurs.figures.specs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import pl.kurs.figures.model.AbstractFigure;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class AbstractFigureWithTypeEqual implements Specification<AbstractFigure> {

    @Nullable
    private String type;

    public AbstractFigureWithTypeEqual(@Nullable String type) {
        this.type = type;
    }

    @Override
    public Predicate toPredicate(Root<AbstractFigure> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (type != null) {
            Predicate area = criteriaBuilder.equal(root.get("type"), type);
            predicates.add(area);
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Nullable
    public String getType() {
        return type;
    }

    public void setType(@Nullable String type) {
        this.type = type;
    }
}
