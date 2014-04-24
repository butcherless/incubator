package es.validate.constraintValidator.IsNotSQLInyectionValidatorTest;

import javax.validation.constraints.Size;

public class MyMAXClass {
	
	@Size(max = 15)
	private String MAXString;

	public MyMAXClass() {
		// TODO Auto-generated constructor stub
	}

	public String getMAXString() {
		return MAXString;
	}

	public void setMAXString(String mAXString) {
		MAXString = mAXString;
	}
}
