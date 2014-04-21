package es.validate.constraintValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.validate.constraint.IsValidDNI;


public class IsValidDNIValidator  implements ConstraintValidator<IsValidDNI, String> {

	@Override
	public void initialize(IsValidDNI constraintAnnotation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// TODO Auto-generated method stub
		return false;
	}

}
