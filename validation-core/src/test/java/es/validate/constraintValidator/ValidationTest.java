package es.validate.constraintValidator;

import static org.junit.Assert.*;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.validate.constraint.IsNotSQLInyection;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:validation-core-unit-test.xml")



public class ValidationTest {
	
	private static final String SELECT_SENTENCE = "Texto sin violaciones";

	private static final String SELECT_QUOTE_SENTENCE = "SELECT * FROM ' TABLA";
	
	private IsNotSQLInyectionValidator isNotSQLInyectionValidator;
	
	@Autowired
	protected javax.validation.Validator validator;
	
	@Before
	public void setup(){
		this.isNotSQLInyectionValidator = new IsNotSQLInyectionValidator();
	}
	
	
	@Test
	public void isNotSQLInyectionTestOk() {
		
		
		MySQLClass mySQLClass = new MySQLClass();
		mySQLClass.setSQLString(SELECT_SENTENCE);
		Set<ConstraintViolation<MySQLClass>> violations = this.validator.validate(mySQLClass);
		Assert.assertTrue(violations.isEmpty());
		
	}
	
	@Test
	public void isNotSQLInyectionTestKO() {
		
		
		MySQLClass mySQLClass = new MySQLClass();
		mySQLClass.setSQLString(SELECT_QUOTE_SENTENCE);
		Set<ConstraintViolation<MySQLClass>> violations = this.validator.validate(mySQLClass);
		Assert.assertFalse("no deben existir violaciones",violations.isEmpty());
		
	}

}
