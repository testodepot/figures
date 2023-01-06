package pl.kurs.figures.validation;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.util.StringUtils;
import pl.kurs.figures.command.EditFigureCommand;
import pl.kurs.figures.exception.BadEntityException;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.service.AbstractFigureService;
import pl.kurs.figures.strategy.CreatingStrategy;
import pl.kurs.figures.strategy.CreatingStrategyFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.math.BigDecimal;
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

        AbstractFigure byId = abstractFigureService.getById(obj.getIdFigure());

        Map<String, CreatingStrategy> strategies = creatingStrategyFactory.getStrategies();

        CreatingStrategy strategy = creatingStrategyFactory.findStrategy(byId.getType());

        boolean isFigureTypeAvailable = strategies.containsValue(strategy);

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

        int counter = countParamsForSpecificClass(strategies, byId);

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

    private int countParamsForSpecificClass(Map<String, CreatingStrategy> strategies, AbstractFigure byId) {
        Class<?> figureClass = null;

        try {
            figureClass = Class.forName("pl.kurs.figures.model." + StringUtils.capitalize(byId.getType()));
        } catch (ClassNotFoundException e) {
            throw new BadEntityException("not a accepted type of figure! Accepted types: " + strategies.values().toString());
        }
        Field[] fields = figureClass.getDeclaredFields();
        int counter = 0;
        for (Field f : fields) {
            if (f.getType() == BigDecimal.class) {
                counter = counter + 1;
            }
        }
        return counter;
    }
}
