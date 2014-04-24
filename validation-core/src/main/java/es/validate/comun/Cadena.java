package es.validate.comun;

public class Cadena {
	
	private static final String LETRAS_NIF = "TRWAGMYFPDXBNJZSQVHLCKE";

	 public Cadena() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
     * Metodo que valida si una cadena esta vacia
     * 
     * @param cadena
     *            String
     * @return boolean
     */
    public static boolean isCadenaVacia(final String cadena) {

        boolean resultado = false;
        if (cadena == null || cadena.equals("")) {
            resultado = true;
        }
        return resultado;
    }
    
    /**
     * Metodo que valida si una cadena es un n�mero
     * 
     * @param cadena
     *            String
     * @return boolean
     */
    public static boolean isNumber(String cadena) {

        boolean resultado = false;
        try {
            if (!isCadenaVacia(cadena)) {
                Integer.parseInt(cadena);
                resultado = true;
            }
        } catch (NumberFormatException e) {
            // La cadena no se puede convertir a entero
        }
        return resultado;
    }

    /**
     * Metodo que valida si es correcto el nif
     * 
     * @param nif
     *            String
     * @return boolean
     */
	public static boolean esNif(String nif) {

		boolean resultado = false;

		try {
    		String nif1=nif.toUpperCase();
    		if (nif1.matches("[0-9]{8}[" + LETRAS_NIF + "]")) {
    			int dni = Integer.parseInt(nif1.substring(0, 8));
    			char letraCalculada = LETRAS_NIF.charAt(dni % 23);

    			if (letraCalculada == nif1.charAt(8)) {
    				resultado = true;
    			}
    		}
		} catch (Exception e) {
		    // Si ha habido alg�n error es porque hay alg�n parseo que tira bien.
		    resultado = false;
		}

		return resultado;
	}
	
	/**
	 * Realiza la validacion si la cadena representa un CIF
	 * 
	 * @param strCadena
	 *            la cadena a comprobar
	 * @return true si la cadena representa un CIF del tipo indicado
	 */
	public static boolean esCif(String cif) {

        boolean resultado = false;

        try {
            String vCif = cif.trim();

            int suma = 0;
            int contador = 0;
            int temporal = 0;
            int codigoControl = 0;
            String cadenaTemporal = null;
            String valoresCif = "ABCDEFGHJKLMNPQRSUVW";
            String letraControlCIF = "0123456789";
            String letraSociedadNumerica = "KLMNPQRSW";
            String primeraLetra = null;
            String ultimaLetra = null;

            // Comprueba la longitud correcta del CIF.
            if (!(vCif.length() == 9))
                return false;

            // Si encuentra alg�n caracter que no sea una letra o un n�mero, el cif
            // no es valido.
            if (vCif.matches("[^A-Za-z0-9]"))
                return false;

            // Convierte a may�sculas la cadena.
            vCif = vCif.toUpperCase();

            // Obtiene la primera letra (letra de la sociedad) y la �ltima letra del
            // CIF (letra de control).
            primeraLetra = vCif.substring(0, 1);

            // Obtiene la �ltima letra del CIF, para comprobar si es v�lida.
            ultimaLetra = vCif.substring(8, 9);

            // Comprueba si la primera letra es v�lida.
            if (valoresCif.indexOf(primeraLetra) < 0)
                return false;

            // Obtiene el c�digo de control.
            // Sumamos las cifras pares
            suma = suma + Integer.parseInt(vCif.substring(2, 3)) + Integer.parseInt(vCif.substring(4, 5))
                    + Integer.parseInt(vCif.substring(6, 7));

            // Ahora cada cifra impar la multiplicamos por dos y sumamos las cifras
            // del resultado.
            for (contador = 1; contador < 8; contador = contador + 2) {
                // Multiplica por 2
                temporal = (Integer.parseInt(vCif.substring(contador, contador + 1)) * 2);

                // Suma los digitos.
                // Diferencia si tiene una cifra, por ejemplo: 8 = 8
                // o si tiene varias, por ejemplo: 16 -> 6 + 1 = 7
                if (temporal < 10)
                    suma = suma + temporal;
                else {
                    cadenaTemporal = String.valueOf(temporal);
                    suma = suma + (Integer.parseInt(cadenaTemporal.substring(0, 1)))
                            + (Integer.parseInt(cadenaTemporal.substring(1, 2)));
                }
            }

            // Obtiene las unidades de la suma y se las resta a 10, para obtener el
            // d�gito de control.
            codigoControl = ((10 - (suma % 10)) % 10);

            // Si la letra es K, L, M, N, P, Q � S entonces al codigo de control le
            // suma 64 y
            // obtengo su ASCII para ver si coincide con la ultima letra del cif.
            if (letraSociedadNumerica.indexOf(primeraLetra) >= 0) {
                byte[] ascii = new byte[1];

                // Obtiene el c�digo ASCII asociado, al sumar 64 al c�digo de
                // control.
                if (codigoControl == 0)
                    codigoControl = 10;
                codigoControl = codigoControl + 64;
                ascii[0] = (Integer.valueOf(codigoControl)).byteValue();

                // El �ltimo d�gito tiene que coincidir con el d�gito de control
                // obtenido
                resultado = (ultimaLetra.equals(new String(ascii)));
            } else {
                // Para el resto de letras de comienzo de CIF el �ltimo d�gito debe ser
                // num�rico,
                // y coincidir con el c�digo de control.
                resultado = (codigoControl == letraControlCIF.indexOf(ultimaLetra));
            }
        } catch (Exception e) {
            // Si ha habido alg�n error es porque hay alg�n parseo que tira bien.
            resultado = false;
        }
        return resultado;
    }

	// Funci�n encargada de validar la Tarjeta de Residencia.
	// La estructura de la T. Residencia puede ser:
	// X-7cifras-letra (esta ultima debe pasar la validacion del NIF)
	// letraletra-8cifras-letra(esta ultima debe pasar la validacion del NIF)

	/**
	 * Realiza la validacion si la cadena representa una tarjeta de residencia.
	 * 
	 * @param cadena
	 *            la cadena a comprobar
	 * @return true si la cadena representa un CIF del tipo indicado
	 */
	public static boolean esNie(String cadena) {
		
		int longitud = 0;
		boolean correcto = true;
		String nif = null;
		String primerCaracter = null;
		String segundoCaracter = null;
		String tercerCaracter = null;
		String valoresPrimerCaracter = "KLTXYZ";
		String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		// Valida la longitud de la cadena.
		longitud = cadena.length();
		if (longitud == 0) {
			return false;
		} else if (!(longitud == 11 || longitud == 9)) {
			return false;
		}

		primerCaracter = cadena.substring(0, 1).toUpperCase();

		if (longitud == 11) {
			segundoCaracter = cadena.substring(1, 2).toUpperCase();
			tercerCaracter = cadena.substring(2, 4).toUpperCase();
			nif = cadena.substring(2, 11).toUpperCase();

			if (valoresPrimerCaracter.indexOf(primerCaracter) == -1)
				correcto = false;
			else if (letras.indexOf(segundoCaracter) == -1)
				correcto = false;
			else if (tercerCaracter.equals("00"))
				correcto = false;
			else
				correcto = esNif(nif);
		}

		if (longitud == 9) {
			if (primerCaracter.equals("X")) {
				nif = "0" + cadena.substring(1, 9).toUpperCase();
				correcto = esNif(nif);
			} else if (primerCaracter.equals("Y")) {
				nif = "1" + cadena.substring(1, 9).toUpperCase();
				correcto = esNif(nif);
			} else if (primerCaracter.equals("Z")) {
				nif = "2" + cadena.substring(1, 9).toUpperCase();
				correcto = esNif(nif);
			} else {
				correcto = false;
			}
		}
		return correcto;
	}
}
