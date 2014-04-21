package es.validate.constraintValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.validate.constraint.IsValidDNI;


public class IsValidDNIValidator  implements ConstraintValidator<IsValidDNI, String> {

	@Override
	public void initialize(IsValidDNI arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isValid(String arg0, ConstraintValidatorContext arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}
