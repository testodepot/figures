package pl.kurs.figures.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = LoginValidator.class)
@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE })
@Retention(RUNTIME)
public @interface CheckIfLoginNotTaken {

    String message() default "{checkifloginnottaken.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
