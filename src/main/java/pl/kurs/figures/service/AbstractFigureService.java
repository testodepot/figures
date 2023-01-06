package pl.kurs.figures.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.command.EditFigureCommand;
import pl.kurs.figures.exception.BadEntityException;
import pl.kurs.figures.exception.EntityNotFoundException;
import pl.kurs.figures.exception.NoPermissionException;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.repository.AbstractFigureBaseRepository;
import pl.kurs.figures.security.Role;
import pl.kurs.figures.security.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public AbstractFigure edit(AbstractFigure figure) {
        return repository.save(Optional.ofNullable(figure)
                .filter(x -> Objects.nonNull(x.getId()))
                .orElseThrow(() -> new BadEntityException("AbstractFigure")));
    }

    public Page<AbstractFigure> findAll(Specification<AbstractFigure> figureSpecification, Pageable pageable) {return repository.findAll(figureSpecification, pageable);}

    public Object createSpecificFigure(CreateFigureCommand createFigureCommand) {
        return objectMaker.makeObject(createFigureCommand);
    }

    public boolean existsById(Long id) { return repository.existsById(id);}

    public AbstractFigure getById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(id, "AbstractFigure"));
    }

    public void checkIfUserHasPermissionToEdit(User user, AbstractFigure figure) {
        Role admin = new Role("ADMIN");
        if (!user.getLogin().equals(figure.getCreatedBy()) && !user.getRoles().contains(admin)) {
            throw new NoPermissionException("user is not permitted to perform this action!");
        }
    }

    public List<Revision<Integer, AbstractFigure>> findRevisionsOfSpecificFigure(long id) {
        return repository.findRevisions(id).getContent();
    }





    public Object editObject(EditFigureCommand editFigureCommand) {
        String type = getById(editFigureCommand.getIdFigure()).getType();
        return objectMaker.makeObjectWithTypeAndParams(type, editFigureCommand.getParameters());
    }






}
