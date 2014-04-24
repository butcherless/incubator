package es.validate.constraintValidator.CIFValidatorTest;

import es.validate.constraint.CIF;

public class MyCIFClass {
	
	@CIF
	private String CIF;

	public MyCIFClass() {
		// TODO Auto-generated constructor stub
	}
	public String getCIF() {
		return CIF;
	}

	public void setCIF(String cIF) {
		CIF = cIF;
	}
	
	

}
