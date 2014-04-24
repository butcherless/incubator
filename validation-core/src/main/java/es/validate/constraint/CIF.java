package es.validate.constraint;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import es.validate.constraintValidator.CIFValidator;

@Documented
@Constraint(validatedBy = CIFValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface CIF {
	// claves i18n para todos los errores de validacion
			String message() default "{No es un CIF válido}";
		    Class<?>[] groups() default {};
		    Class<? extends Payload>[] payload() default {};

}
