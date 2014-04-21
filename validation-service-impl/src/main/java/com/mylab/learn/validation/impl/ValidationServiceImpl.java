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
	
	public ValidateDNIResponse validateDNI(final ValidateDNIRequest validateDNIRequest)
            throws ValidationServiceException {
	    // TODO Auto-generated method stub
		// llamar al servicio de auditoria
		// auditRegistry = this.newAuditRegistry(validateDNIRequest);
		// this.auditRegistryRepository.save(auditRegistry);

		this.myMethod();
		
		
		
		
	    return new ValidateDNIResponse();
    }

	private void myMethod() {
		// TODO Auto-generated method stub
		
	}

	public ValidateBankAccountResponse validateBankAccount(
            ValidateBankAccountRequest validateBankAccountRequest)
            throws ValidationServiceException {
	    // TODO Auto-generated method stub
	    return new ValidateBankAccountResponse();
    }

	public ValidateTelephoneResponse validateTelephone(
            ValidateTelephoneRequest validateTelephoneRequest) throws ValidationServiceException {
	    // TODO Auto-generated method stub
	    return new ValidateTelephoneResponse();
    }

	public ValidateDateResponse validateDate(ValidateDateRequest validateDateRequest)
            throws ValidationServiceException {
	    // TODO Auto-generated method stub
	    return new ValidateDateResponse();
    }

	public ValidateSQLStringResponse validateSQLString(
            ValidateSQLStringRequest validateSQLStringRequest) throws ValidationServiceException {
	    // TODO Auto-generated method stub
	    return new ValidateSQLStringResponse();
    }


	
	//// HELPERS
	
	//TODO
	//  AuditRegistry newAuditRegistry(...) {
	// AuditRegistry ar = new ....
	// ar.setTTTT(request.getXXX();
	// ar.setTime(new Date());
	// return ar;
	//}
	

}
