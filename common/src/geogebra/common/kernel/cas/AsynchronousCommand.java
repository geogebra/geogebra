package geogebra.common.kernel.cas;

public interface AsynchronousCommand {
	public void handleCASoutput(String output,int requestID);

	public void handleException(Throwable exception, int requestID);

}
