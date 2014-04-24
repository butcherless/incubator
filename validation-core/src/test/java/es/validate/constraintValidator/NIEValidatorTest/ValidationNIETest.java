package es.validate.constraintValidator.NIEValidatorTest;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:validation-core-unit-test.xml")
public class ValidationNIETest {
	static Logger log = Logger.getLogger(ValidationNIETest.class.getName());
	
	private static final String NIE_OK = "Y0171430W";

	private static final String NIE_KO = "45102095A";
	
	@Autowired
	protected javax.validation.Validator validator;
	
	@Before
	public void setup(){
		//this.isNotNIEInyectionValidator = new IsNotNIEInyectionValidator();
	}
	
	@Test
	public void validationNIETestOk() {
		
		
		MyNIEClass myNIEClass = new MyNIEClass();
		myNIEClass.setNIE(NIE_OK);
		Set<ConstraintViolation<MyNIEClass>> violations = this.validator.validate(myNIEClass);
		Assert.assertTrue("no deben existir violaciones NIE",violations.isEmpty());
		log.info("TRUE" + violations.size()+" "+violations);

	}
	
	@Test
	public void validationNIETestKO() {
		
		
		MyNIEClass myNIEClass = new MyNIEClass();
		myNIEClass.setNIE(NIE_KO);
		Set<ConstraintViolation<MyNIEClass>> violations = this.validator.validate(myNIEClass);
		Assert.assertFalse("deben existir violaciones NIE",violations.isEmpty());
		log.info("TRUE" + violations.size()+" "+violations);
		
	}
}
