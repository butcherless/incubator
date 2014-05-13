package es.validate.constraintValidator.test.NIEValidatorTest;

import java.util.Iterator;
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

import es.validate.constraintValidator.test.comun.cargarListTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:validation-core-unit-test.xml")
public class ValidationNIETest {
	static Logger log = Logger.getLogger(ValidationNIETest.class.getName());
	
	private static final String NIE_OK = "Y0171430W";
	
	private static final String NIEs_OK = "NIEs_OK.txt";
			
	private static final String NIE_OK1 = "";
	
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
		
		myNIEClass.setNIE(NIE_OK1);
		violations = this.validator.validate(myNIEClass);
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
	
	
	
	@Ignore
	@Test
	public void validationNIETestAutomaticOk() {
		
		MyNIEClass myNIEClass = new MyNIEClass();
		Set<ConstraintViolation<MyNIEClass>> violations=null;
		int i = 0;
		Iterator<String> it = new cargarListTest(NIEs_OK).getLista().iterator();
		
		while(it.hasNext()){
		myNIEClass.setNIE(it.next());
		violations = this.validator.validate(myNIEClass);
		Assert.assertTrue("Este NIE: " 
							+ myNIEClass.getNIE() 
							+ ", en la posición: "
							+ (i+1) 
							+",del fichero " 
							+ NIEs_OK 
							+" es erróneo." 
							+ ". Hechas correctamente un total: "
							+i
							+" pruebas de NIE",violations.isEmpty());
		i++;
		}
		

	}
	
	
	
	 
// List<String> cargarListTest( final String fichero) {
//		 BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fichero))) ;
//		 List<String> lista = new ArrayList<String>();
//			
//	
//		    try {	      
//		      String linea;
//			while((linea = br.readLine()) != null)
//		      {
//				
//		      lista.add(new String(linea)); 
//		      }
//		 
//		    }
//		    catch(Exception e) {
//		    	
//		      System.out.println("Excepcion leyendo fichero "+ fichero + ": "+ e);
//		      
//		    }
//		
//		    return lista;
//		  }
}
