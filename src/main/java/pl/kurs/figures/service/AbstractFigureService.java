package pl.kurs.figures.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.repository.AbstractFigureBaseRepository;

@Service
public class AbstractFigureService {

    private AbstractFigureBaseRepository repository;

    private ObjectMaker objectMaker;

    public AbstractFigureService(AbstractFigureBaseRepository repository, ObjectMaker objectMaker) {
        this.repository = repository;
        this.objectMaker = objectMaker;
    }

    public AbstractFigure add(AbstractFigure figure) {
        return repository.save(figure);
    }

    public Page<AbstractFigure> findAll(Specification<AbstractFigure> booleanExpression, Pageable pageable) {return repository.findAll(booleanExpression, pageable);}

    public Object createSpecificFigure(CreateFigureCommand createFigureCommand) throws InstantiationException, IllegalAccessException {
        return objectMaker.makeObject(createFigureCommand);
    }

}
