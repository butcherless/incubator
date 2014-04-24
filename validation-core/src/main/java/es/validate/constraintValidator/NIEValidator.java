package es.validate.constraintValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import es.validate.comun.Cadena;
import es.validate.constraint.NIE;

@Component
public class NIEValidator implements ConstraintValidator<NIE, String> {

	@Override
	public void initialize(NIE constraintAnnotation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		boolean result = false;
		Cadena cadena = new Cadena();
		if(Cadena.isCadenaVacia(value) || Cadena.esNie(value)){
			result = true;
		}
		
		// TODO Auto-generated method stub
		return result;
	}

}