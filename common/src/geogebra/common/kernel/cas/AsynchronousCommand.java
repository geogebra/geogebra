package geogebra.common.kernel.cas;

public interface AsynchronousCommand {
	public boolean USE_ASYNCHRONOUS=true;
	public void handleCASoutput(String output,int requestID);

	public void handleException(Throwable exception, int requestID);
	
	public boolean useCacheing();

	public String getCasInput();

}
