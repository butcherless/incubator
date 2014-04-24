package es.validate.constraintValidator.NIEValidatorTest;

import es.validate.constraint.NIE;

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
