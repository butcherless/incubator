package es.validate.constraintValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import es.validate.constraint.IsNotSQLInyection;

@Component
public class IsNotSQLInyectionValidator implements
		ConstraintValidator<IsNotSQLInyection, String> {

	@Override
	public void initialize(IsNotSQLInyection constraintAnnotation) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// TODO Auto-generated method stub
		boolean result = true;

		//TODO mejorar la expresión regular.
		if(!value.matches("[^=&|*ø?#@%$]*$")){
			result = false;
		}
			

		

		return result;
	}

}
