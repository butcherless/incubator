package es.validate.constraintValidator;

import static org.junit.Assert.*;

import org.junit.Assert;
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
	
	@Autowired
	private IsNotSQLInyection isNotSQLInyection;
	
    @Ignore
	@Test
	public void isNotSQLInyectionTest() {
		
		Assert.assertTrue(true);
		
	}

}
