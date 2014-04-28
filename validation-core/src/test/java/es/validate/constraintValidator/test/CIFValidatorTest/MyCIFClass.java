package es.validate.constraintValidator.test.CIFValidatorTest;

import es.fega.comun.validate.constraints.CIF;

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
