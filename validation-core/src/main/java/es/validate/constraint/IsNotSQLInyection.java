package es.validate.constraint;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import es.validate.constraintValidator.IsNotSQLInyectionValidator;


@Documented
@Constraint(validatedBy = IsNotSQLInyectionValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface IsNotSQLInyection {
	// claves i18n para todos los errores de validacion
		String message() default "{Contiene alguno de los carácteres no válidos: =&|*ø?#@%$}";
	    Class<?>[] groups() default {};
	    Class<? extends Payload>[] payload() default {};
}
