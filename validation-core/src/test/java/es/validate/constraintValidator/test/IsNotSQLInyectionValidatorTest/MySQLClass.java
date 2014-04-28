package es.validate.constraintValidator.test.IsNotSQLInyectionValidatorTest;

import es.fega.comun.validate.constraints.IsNotSQLInyection;

public class MySQLClass {

	@IsNotSQLInyection
	private String SQLString;

	public MySQLClass() {
		// TODO Auto-generated constructor stub
	}

	public void setSQLString(String sQLString) {
		SQLString = sQLString;
	}

	public String getSQLString() {
		return SQLString;
	}

}
