package es.validate.constraint;



import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import es.validate.constraintValidator.IsValidDNIValidator;

@Documented
@Constraint(validatedBy = IsValidDNIValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface IsValidDNI {

	// claves i18n para todos los errores de validacion
	String message() default "{com.acme.constraint.inValidDNI.invalidDNI}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


//
//
//package org.gm.beanvalidation.constraints;
//
//import static java.lang.annotation.ElementType.*;
//import static java.lang.annotation.RetentionPolicy.*;
//
//import java.lang.annotation.Documented;
//import java.lang.annotation.Retention;
//import java.lang.annotation.Target;
//
//import javax.validation.Constraint;
//
//import org.gm.beanvalidation.validators.UpperCaseValidator;
//
//@Target( { METHOD, FIELD, ANNOTATION_TYPE })
//@Retention(RUNTIME)
//@Constraint(validatedBy = UpperCaseValidator.class)
//@Documented
//public @interface UpperCase {
//
//    String message() default "{validator.uppercase}";
//
//    Class<?>[] groups() default {};
//
//}
//
//
//package org.gm.beanvalidation.validators;
//
//import javax.validation.ConstraintValidator;
//import javax.validation.ConstraintValidatorContext;
//
//import org.gm.beanvalidation.constraints.UpperCase;
//
//public class UpperCaseValidator implements ConstraintValidator<UpperCase, String> {
//
//    public void initialize(UpperCase constraintAnnotation) {
//        //nothing to do
//    }
//
//    public boolean isValid(String object,
//        ConstraintValidatorContext constraintContext) {
//
//        if (object == null)
//            return true;
//
//        
//        
//        
//        
//        
//        
//        return object.equals(object.toUpperCase());
//    }
//
//}

//@Documented
//@Constraint(validatedBy = OrderNumberValidator.class)
//@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
//@Retention(RUNTIME)
//public @interface OrderNumber {
//    String message() default "{com.acme.constraint.OrderNumber.message}";
//    Class<?>[] groups() default {};
//    Class<? extends Payload>[] payload() default {};
//}