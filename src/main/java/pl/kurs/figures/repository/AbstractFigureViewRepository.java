package pl.kurs.figures.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;
import pl.kurs.figures.model.AbstractFigureView;

public interface AbstractFigureViewRepository extends JpaRepository<AbstractFigureView, Long>, PagingAndSortingRepository<AbstractFigureView, Long>, JpaSpecificationExecutor<AbstractFigureView>, RevisionRepository<AbstractFigureView, Long, Integer> {


}
