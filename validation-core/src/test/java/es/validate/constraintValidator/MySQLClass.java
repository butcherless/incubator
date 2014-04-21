package es.validate.constraintValidator;

import es.validate.constraint.IsNotSQLInyection;

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
