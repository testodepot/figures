package pl.kurs.figures.validation;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.stereotype.Component;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.List;

@Component
public class BigDecimalValidator implements ConstraintValidator<BigDecimalCheck, List<BigDecimal>> {

    @Override
    public boolean isValid(List<BigDecimal> bigDecimals, ConstraintValidatorContext constraintValidatorContext) {

        for (BigDecimal param : bigDecimals) {

            if (param == null) {
                String error = "params cant be null!";

                HibernateConstraintValidatorContext hibernateContext = constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class);
                hibernateContext.disableDefaultConstraintViolation();
                hibernateContext
                        .addMessageParameter( "errors", error )
                        .buildConstraintViolationWithTemplate("{bigdecimalcheck.message}")
                        .addConstraintViolation();

                return false;
            }

            String paramString = param.toString();

            if (!paramString.matches("^-?[0-9]\\d*(\\.\\d+)?$")) {
                String error = "params must contain only numbers!";
                HibernateConstraintValidatorContext hibernateContext = constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class);
                hibernateContext.disableDefaultConstraintViolation();
                hibernateContext
                        .addMessageParameter( "errors", error )
                        .buildConstraintViolationWithTemplate("{bigdecimalcheck.message}")
                        .addConstraintViolation();

                return false;
            }

            if (param.compareTo(BigDecimal.ZERO) < 0) {
                String error = "params must be greater than 0!";
                HibernateConstraintValidatorContext hibernateContext = constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class);
                hibernateContext.disableDefaultConstraintViolation();
                hibernateContext
                        .addMessageParameter( "errors", error )
                        .buildConstraintViolationWithTemplate("{bigdecimalcheck.message}")
                        .addConstraintViolation();

                return false;
            }
        }
        return true;

    }
}
