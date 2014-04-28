package es.validate.constraintValidator.test.comun;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class cargarListTest {

	private  List<String> lista = new ArrayList<String>();
	
	 public cargarListTest( final String fichero) {
		 BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fichero))) ;
		 String linea;
			
	
		    try {	      
		     
			while((linea = br.readLine()) != null)
		      {
				
		      this.lista.add(new String(linea)); 
		      }
		 
		    }
		    catch(Exception e) {
		    	
		      System.out.println("Excepcion leyendo fichero "+ fichero + ": "+ e);
		      
		    }
		
		  }

	public List<String> getLista() {
		return lista;
	}

	public void setLista(List<String> lista) {
		this.lista = lista;
	}
	 
	 
}
