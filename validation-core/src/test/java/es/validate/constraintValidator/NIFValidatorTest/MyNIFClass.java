package es.validate.constraintValidator.NIFValidatorTest;

import es.validate.constraint.NIF;

public class MyNIFClass {
	@NIF
	private String NIF;

	public MyNIFClass() {
		// TODO Auto-generated constructor stub
	}
	public String getNIF() {
		return NIF;
	}

	public void setNIF(String nIF) {
		NIF = nIF;
	}

}
