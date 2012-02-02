package geogebra.common.kernel.cas;

public interface AsynchronousCommand {
	public boolean USE_ASYNCHRONOUS=false;
	public void handleCASoutput(String output,int requestID);

	public void handleException(Throwable exception, int requestID);

}
