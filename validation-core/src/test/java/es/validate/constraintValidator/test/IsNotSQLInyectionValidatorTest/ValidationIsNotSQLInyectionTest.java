package es.validate.constraintValidator.test.IsNotSQLInyectionValidatorTest;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;

import org.apache.commons.logging.impl.Log4JLogger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.fega.comun.validate.constraints.IsNotSQLInyection;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:validation-core-unit-test.xml")



public class ValidationIsNotSQLInyectionTest {
	
	static Logger log = Logger.getLogger(ValidationIsNotSQLInyectionTest.class.getName());
	
	private static final String SELECT_SENTENCE = "Texto sin inyección sql";

	private static final String SELECT_QUOTE_SENTENCE = "SELECT * FROM TABLA";
	
	private static final String SELECT_MENOR_15 = "Texto menor 15";

	private static final String SELECT_MAYOR_15= "Texto mayor de 15 caracteres";
	
	//private IsNotSQLInyectionValidator isNotSQLInyectionValidator;
	
	@Autowired
	protected javax.validation.Validator validator;
	
	@Before
	public void setup(){
		//this.isNotSQLInyectionValidator = new IsNotSQLInyectionValidator();
	}
	
	
	@Test
	public void isNotSQLInyectionTestOk() {
		
		
		MySQLClass mySQLClass = new MySQLClass();
		mySQLClass.setSQLString(SELECT_SENTENCE);
		Set<ConstraintViolation<MySQLClass>> violations = this.validator.validate(mySQLClass);
		Assert.assertTrue("no deben existir violaciones SQLInyection",violations.isEmpty());
		log.info("TRUE" + violations.size()+" "+violations);

	}
	
	@Test
	public void isNotSQLInyectionTestKO() {
		
		
		MySQLClass mySQLClass = new MySQLClass();
		mySQLClass.setSQLString(SELECT_QUOTE_SENTENCE);
		Set<ConstraintViolation<MySQLClass>> violations = this.validator.validate(mySQLClass);
		Assert.assertFalse("deben existir violaciones SQLInyection",violations.isEmpty());
		log.info("TRUE" + violations.size()+" "+violations);
		
	}

	@Test
	public void validadorSizeMAXAnotacionTestOK(){
		
		MyMAXClass myMAXClass = new MyMAXClass();
		myMAXClass.setMAXString(SELECT_MENOR_15);
		Set<ConstraintViolation<MyMAXClass>> violations = this.validator.validate(myMAXClass);
		Assert.assertTrue("NO deben existir violaciones MAX",violations.isEmpty());
		log.info("TRUE" + violations.size()+" "+violations);
	}
	
	@Test
	public void validadorSizeMAXAnotacionTestKO(){
		
		MyMAXClass myMAXClass = new MyMAXClass();
		myMAXClass.setMAXString(SELECT_MAYOR_15);
		Set<ConstraintViolation<MyMAXClass>> violations = this.validator.validate(myMAXClass);
		Assert.assertFalse("deben existir violaciones MAX",violations.isEmpty());
		log.info("TRUE" + violations.size()+" "+violations);
	}
}
