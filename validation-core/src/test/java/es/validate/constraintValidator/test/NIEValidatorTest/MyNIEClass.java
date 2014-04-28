package es.validate.constraintValidator.test.NIEValidatorTest;

import es.fega.comun.validate.constraints.NIE;

public class MyNIEClass {
	
	@NIE
	private String NIE;

	public MyNIEClass() {
		// TODO Auto-generated constructor stub
	}
	public String getNIE() {
		return NIE;
	}

	public void setNIE(String nIE) {
		NIE = nIE;
	}

}
