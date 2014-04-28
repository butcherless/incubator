package es.fega.comun.validate.constraintsValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import es.fega.comun.auxiliar.Cadena;
import es.fega.comun.validate.constraints.CIF;

@Component
public class CIFValidator implements ConstraintValidator<CIF, String> {

	@Override
	public void initialize(CIF constraintAnnotation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		boolean result = false;
		Cadena cadena = new Cadena();
		if(Cadena.isCadenaVacia(value) || Cadena.esCif(value)){
			result = true;
		}
		
		// TODO Auto-generated method stub
		return result;
	}

}
