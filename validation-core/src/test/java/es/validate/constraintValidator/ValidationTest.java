package es.validate.constraintValidator;

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

import es.validate.constraint.IsNotSQLInyection;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:validation-core-unit-test.xml")



public class ValidationTest {
	
	static Logger log = Logger.getLogger(ValidationTest.class.getName());
	
	private static final String SELECT_SENTENCE = "Texto sin violaciones";

	private static final String SELECT_QUOTE_SENTENCE = "SELECT * FROM TABLA";
	
	private static final String SELECT_MENOR_15 = "Texto menor 15";

	private static final String SELECT_MAYOR_15= "Texto mayor de 15 caracteres";
	
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
		Assert.assertTrue("no deben existir violaciones",violations.isEmpty());
		log.info("TRUE" + violations.size()+" "+violations);

	}
	
	@Test
	public void isNotSQLInyectionTestKO() {
		
		
		MySQLClass mySQLClass = new MySQLClass();
		mySQLClass.setSQLString(SELECT_QUOTE_SENTENCE);
		Set<ConstraintViolation<MySQLClass>> violations = this.validator.validate(mySQLClass);
		Assert.assertFalse("deben existir violaciones",violations.isEmpty());
		log.info("TRUE" + violations.size()+" "+violations);
		
	}

	@Test
	public void validadorSizeMAXAnotacionTestOK(){
		
		MyMAXClass myMAXClass = new MyMAXClass();
		myMAXClass.setMAXString(SELECT_MENOR_15);
		Set<ConstraintViolation<MyMAXClass>> violations = this.validator.validate(myMAXClass);
		Assert.assertTrue("NO deben existir violaciones",violations.isEmpty());
		log.info("TRUE" + violations.size()+" "+violations);
	}
	
	@Test
	public void validadorSizeMAXAnotacionTestKO(){
		
		MyMAXClass myMAXClass = new MyMAXClass();
		myMAXClass.setMAXString(SELECT_MAYOR_15);
		Set<ConstraintViolation<MyMAXClass>> violations = this.validator.validate(myMAXClass);
		Assert.assertFalse("deben existir violaciones",violations.isEmpty());
		log.info("TRUE" + violations.size()+" "+violations);
	}
}
