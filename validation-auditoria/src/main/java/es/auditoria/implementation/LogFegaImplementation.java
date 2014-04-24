/**
 * 
 */
package es.auditoria.implementation;

import org.springframework.stereotype.Service;

import es.auditoria.api.LogFegaRequest;
import es.auditoria.api.LogFegaService;
import es.auditoria.api.LogFegaServiceException;

/**
 * @author josemaria
 *
 */
@Service
public class LogFegaImplementation implements LogFegaService {

	/* (non-Javadoc)
	 * @see es.auditoria.api.LogFegaService#debugLog(es.auditoria.api.LogFegaRequest)
	 */
	@Override
	public void debugLog(final LogFegaRequest logFegaRequest)
			throws LogFegaServiceException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see es.auditoria.api.LogFegaService#infoLog(es.auditoria.api.LogFegaRequest)
	 */
	@Override
	public void infoLog(final LogFegaRequest logFegaRequest)
			throws LogFegaServiceException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see es.auditoria.api.LogFegaService#warmLog(es.auditoria.api.LogFegaRequest)
	 */
	@Override
	public void warmLog(final LogFegaRequest logFegaRequest)
			throws LogFegaServiceException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see es.auditoria.api.LogFegaService#errorLog(es.auditoria.api.LogFegaRequest)
	 */
	@Override
	public void errorLog(final LogFegaRequest logFegaRequest)
			throws LogFegaServiceException {
		// TODO Auto-generated method stub

	}

}
