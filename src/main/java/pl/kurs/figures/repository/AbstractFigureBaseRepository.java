package pl.kurs.figures.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;
import pl.kurs.figures.model.AbstractFigure;

public interface AbstractFigureBaseRepository extends JpaRepository<AbstractFigure, Long>, JpaSpecificationExecutor<AbstractFigure>, RevisionRepository<AbstractFigure, Long, Integer> {


    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "createdBy"
            }
    )
    Page<AbstractFigure> findAll(Specification<AbstractFigure> specification, Pageable pageable);



}
