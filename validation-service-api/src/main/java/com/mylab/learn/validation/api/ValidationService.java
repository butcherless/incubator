.........
package com.mylab.learn.validation.api;

public interface ValidationService {

	ValidateDNIResponse validateDNI(ValidateDNIRequest validateDNIRequest)
	        throws ValidationServiceException;

	ValidateBankAccountResponse validateBankAccount(
	        ValidateBankAccountRequest validateBankAccountRequest)
	        throws ValidationServiceException;

	ValidateTelephoneResponse validateTelephone(
	        ValidateTelephoneRequest validateTelephoneRequest)
	        throws ValidationServiceException;

	ValidateDateResponse validateDate(ValidateDateRequest validateDateRequest)
	        throws ValidationServiceException;

	ValidateSQLStringResponse validateSQLString(
	        ValidateSQLStringRequest validateSQLStringRequest)
	        throws ValidationServiceException;

}
