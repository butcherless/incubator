package es.validate.constraintValidator.test.NIFValidatorTest;

import java.util.Set;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.validate.constraintValidator.test.NIFValidatorTest.MyNIFClass;
import es.validate.constraintValidator.test.NIFValidatorTest.ValidationNIFTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:validation-core-unit-test.xml")
public class ValidationNIFTest {
	static Logger log = Logger.getLogger(ValidationNIFTest.class.getName());
	
	private static final String NIF_OK = "45102095S";

	private static final String NIF_KO = "45102095A";
	
	@Autowired
	protected javax.validation.Validator validator;
	
	@Before
	public void setup(){
		//this.isNotNIFInyectionValidator = new IsNotNIFInyectionValidator();
	}
	
	@Test
	public void validationNIFTestOk() {
		
		
		MyNIFClass myNIFClass = new MyNIFClass();
		myNIFClass.setNIF(NIF_OK);
		Set<ConstraintViolation<MyNIFClass>> violations = this.validator.validate(myNIFClass);
		Assert.assertTrue("no deben existir violaciones NIF",violations.isEmpty());
		log.info("TRUE" + violations.size()+" "+violations);

	}
	
	@Test
	public void validationNIFTestKO() {
		
		
		MyNIFClass myNIFClass = new MyNIFClass();
		myNIFClass.setNIF(NIF_KO);
		Set<ConstraintViolation<MyNIFClass>> violations = this.validator.validate(myNIFClass);
		Assert.assertFalse("deben existir violaciones NIF",violations.isEmpty());
		log.info("TRUE" + violations.size()+" "+violations);
		
	}
}
