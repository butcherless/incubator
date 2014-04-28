package es.validate.constraintValidator.test.CIFValidatorTest;

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

public class ValidationCIFTest {
	
	static Logger log = Logger.getLogger(ValidationCIFTest.class.getName());
	
	private static final String CIF_OK = "A74812439";

	private static final String CIF_KO = "45102095A";
	
	@Autowired
	protected javax.validation.Validator validator;
	
	@Before
	public void setup(){
		//this.isNotCIFInyectionValidator = new IsNotCIFInyectionValidator();
	}
	
	@Test
	public void validationCIFTestOk() {
		
		
		MyCIFClass myCIFClass = new MyCIFClass();
		myCIFClass.setCIF(CIF_OK);
		Set<ConstraintViolation<MyCIFClass>> violations = this.validator.validate(myCIFClass);
		Assert.assertTrue("no deben existir violaciones CIFInyection",violations.isEmpty());
		log.info("TRUE" + violations.size()+" "+violations);

	}
	
	@Test
	public void validationCIFTestKO() {
		
		MyCIFClass myCIFClass = new MyCIFClass();
		myCIFClass.setCIF(CIF_KO);
		Set<ConstraintViolation<MyCIFClass>> violations = this.validator.validate(myCIFClass);
		Assert.assertFalse("deben existir violaciones CIFInyection",violations.isEmpty());
		log.info("False" + violations.size()+" "+violations);
		
	}
}
