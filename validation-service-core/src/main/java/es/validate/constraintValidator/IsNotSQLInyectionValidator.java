package es.validate.constraintValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.validate.constraint.IsNotSQLInyection;

public class IsNotSQLInyectionValidator  implements ConstraintValidator<IsNotSQLInyection, String>{

	@Override
	public void initialize(IsNotSQLInyection arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isValid(String arg0, ConstraintValidatorContext arg1) {
		// TODO Auto-generated method stub
		boolean _return= false;
		if(arg0.contentEquals("validado")) 
		_return= true;
		
		
		return _return;
	}

}
