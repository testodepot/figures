package pl.kurs.figures.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.kurs.figures.model.AbstractFigure;

public interface AbstractFigureBaseRepository extends JpaRepository<AbstractFigure, Long>, PagingAndSortingRepository<AbstractFigure, Long>, JpaSpecificationExecutor<AbstractFigure> {



}