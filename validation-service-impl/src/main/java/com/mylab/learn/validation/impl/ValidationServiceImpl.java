package com.mylab.learn.validation.impl;

import org.springframework.stereotype.Service;

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
import com.mylab.learn.validation.api.ValidationServiceException;

@Service
public class ValidationServiceImpl implements ValidationService {

	public ValidateDNIResponse validateDNI(ValidateDNIRequest validateDNIRequest)
            throws ValidationServiceException {
	    // TODO Auto-generated method stub
	    return new ValidateDNIResponse();
    }

	public ValidateBankAccountResponse validateBankAccount(
            ValidateBankAccountRequest validateBankAccountRequest)
            throws ValidationServiceException {
	    // TODO Auto-generated method stub
	    return null;
    }

	public ValidateTelephoneResponse validateTelephone(
            ValidateTelephoneRequest validateTelephoneRequest) throws ValidationServiceException {
	    // TODO Auto-generated method stub
	    return null;
    }

	public ValidateDateResponse validateDate(ValidateDateRequest validateDateRequest)
            throws ValidationServiceException {
	    // TODO Auto-generated method stub
	    return null;
    }

	public ValidateSQLStringResponse validateSQLString(
            ValidateSQLStringRequest validateSQLStringRequest) throws ValidationServiceException {
	    // TODO Auto-generated method stub
	    return null;
    }


	
	

}
