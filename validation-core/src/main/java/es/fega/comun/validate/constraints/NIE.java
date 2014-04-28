package es.fega.comun.validate.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import es.fega.comun.validate.constraintsValidator.NIEValidator;

@Documented
@Constraint(validatedBy = NIEValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface NIE {
	// claves i18n para todos los errores de validacion
				String message() default "{No es un NIE válido}";
			    Class<?>[] groups() default {};
			    Class<? extends Payload>[] payload() default {};
}
