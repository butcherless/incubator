package com.mylab.learn.validation.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mylab.learn.validation.api.ValidateBankAccountRequest;
import com.mylab.learn.validation.api.ValidateBankAccountResponse;
import com.mylab.learn.validation.api.ValidateDNIRequest;
import com.mylab.learn.validation.api.ValidateDNIResponse;
import com.mylab.learn.validation.api.ValidateDateRequest;
import com.mylab.learn.validation.api.ValidateDateResponse;
import com.mylab.learn.validation.api.ValidateSQLStringRequest;
import com.mylab.learn.validation.api.ValidateSQLStringResponse;
import com.mylab.learn.validation.api.ValidateTelephoneRequest;
import com.mylab.learn.validation.api.ValidateTelephoneResponse;
import com.mylab.learn.validation.api.ValidationService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:validation-service-unit-test.xml")
public class ValidationServiceTest {

	@Autowired
	private ValidationService validationService;
	
	@Test
	public void testValidateDNI(){
		ValidateDNIRequest validateDNIRequest = null;
		ValidateDNIResponse validateDNIResponse = this.validationService.validateDNI(validateDNIRequest);
		Assert.assertNotNull("Null Response", validateDNIResponse);
		
		// TODO asserts de negocio
//		validateDNIResponse.get
	}
	
	@Test
	public void testValidateBankAccount(){
		ValidateBankAccountRequest validateBankAccountRequest = null;
		ValidateBankAccountResponse validateBankAccountResponse = this.validationService.validateBankAccount(validateBankAccountRequest);
		Assert.assertNotNull("Null Response", validateBankAccountResponse);
		
	}
	
	@Test
	public void testValidateTelephone(){
		ValidateTelephoneRequest  validateTelephoneRequest = null;
		ValidateTelephoneResponse validateTelephoneResponse = this.validationService.validateTelephone(validateTelephoneRequest);
		Assert.assertNotNull("Null Response", validateTelephoneResponse);
	}
	
	@Test
	public void testValidateDate(){
		ValidateDateRequest  ValidateDateRequest = null;
		ValidateDateResponse ValidateDateResponse = this.validationService.validateDate(ValidateDateRequest);
		Assert.assertNotNull("Null Response", ValidateDateResponse);
	}
	
	@Test
	public void testValidateSQLString(){
		ValidateSQLStringRequest  ValidateSQLStringRequest = null;
		ValidateSQLStringResponse ValidateSQLStringResponse = this.validationService.validateSQLString(ValidateSQLStringRequest);
		Assert.assertNotNull("Null Response", ValidateSQLStringResponse);
	}
	
	
}
