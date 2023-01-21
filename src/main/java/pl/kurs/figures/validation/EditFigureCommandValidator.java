package pl.kurs.figures.validation;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import pl.kurs.figures.command.EditFigureCommand;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.service.AbstractFigureService;
import pl.kurs.figures.strategy.CreatingStrategy;
import pl.kurs.figures.strategy.CreatingStrategyFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;

public class EditFigureCommandValidator implements ConstraintValidator<EditCommandCheck, EditFigureCommand> {

    private CreatingStrategyFactory creatingStrategyFactory;

    private AbstractFigureService abstractFigureService;

    public EditFigureCommandValidator(CreatingStrategyFactory creatingStrategyFactory, AbstractFigureService abstractFigureService) {
        this.creatingStrategyFactory = creatingStrategyFactory;
        this.abstractFigureService = abstractFigureService;
    }

    public boolean isValid(EditFigureCommand obj, ConstraintValidatorContext context) {

        if (obj.getIdFigure() == null) {
            String error = "figure id cant be null!";
            HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .addMessageParameter("errors", error)
                    .buildConstraintViolationWithTemplate("{editcommandcheck.message}")
                    .addConstraintViolation();
            return false;
        }


        if (obj.getIdFigure() <= 0) {
            String error = "figure id must be positive!";
            HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .addMessageParameter("errors", error)
                    .buildConstraintViolationWithTemplate("{editcommandcheck.message}")
                    .addConstraintViolation();
            return false;
        }


        boolean exists = abstractFigureService.existsById(obj.getIdFigure());

        if (!exists) {
            String error = "no figure of this id!";
            HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .addMessageParameter("errors", error)
                    .buildConstraintViolationWithTemplate("{editcommandcheck.message}")
                    .addConstraintViolation();
            return false;
        }

        AbstractFigure byId = abstractFigureService.getById(obj.getIdFigure());

        Map<String, CreatingStrategy> strategies = creatingStrategyFactory.getStrategies();

        CreatingStrategy strategy = creatingStrategyFactory.findStrategy(byId.getType());

        boolean isFigureTypeAvailable = strategies.containsValue(strategy);


        if (!isFigureTypeAvailable) {
            String error = "no figure of this type available! Accepted types: " + strategies.values().toString();
            HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .addMessageParameter("errors", error)
                    .buildConstraintViolationWithTemplate("{editcommandcheck.message}")
                    .addConstraintViolation();
            return false;
        }

        int counter = strategy.getNumberOfParamsOfSpecificFigure();

        if (obj.getParameters().size() != counter) {
            String error = "wrong number of params for this figure type: " + byId.getType();
            HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .addMessageParameter("errors", error)
                    .buildConstraintViolationWithTemplate("{parametersforspecificfigurecheck.message}")
                    .addConstraintViolation();

            return false;
        }
        return true;
    }

}
