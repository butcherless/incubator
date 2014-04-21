package com.mylab.learn.validation.api;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ValidateDNIRequest implements Serializable{

	public @interface IsValidDNI {

	}

	/**
	 * 
	 */
    private static final long serialVersionUID = -8808387409187470368L;
    
   @NotNull
   @Min(1)
   @Max(9)
    private String numero;
   
   @NotNull
   @Min(1)
   @Max(1)
    private String letra;
    
   @IsValidDNI
   private String identificador;

}
