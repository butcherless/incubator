package es.auditoria.api;

public interface LogFegaService {
	
	void debugLog(LogFegaRequest logFegaRequest) throws LogFegaServiceException;
	
	void infoLog(LogFegaRequest logFegaRequest) throws LogFegaServiceException;
	
	void warmLog(LogFegaRequest logFegaRequest) throws LogFegaServiceException;
	
	void errorLog(LogFegaRequest logFegaRequest) throws LogFegaServiceException;

}
