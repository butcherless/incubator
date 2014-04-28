package es.fega.comun.validate.constraintsValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import es.fega.comun.auxiliar.Cadena;
import es.fega.comun.validate.constraints.NIF;

@Component
public class NIFValidator implements ConstraintValidator<NIF, String> {

	@Override
	public void initialize(NIF constraintAnnotation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		boolean result = false;
		Cadena cadena = new Cadena();
		if(cadena.isCadenaVacia(value) || cadena.esNif(value)){
			result = true;
		}
		
		// TODO Auto-generated method stub
		return result;
	}

}
