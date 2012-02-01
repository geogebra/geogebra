package geogebra.common.cas;

public interface Evaluate {
	
	public String evaluate(String s) throws Throwable;

	public String evaluate(String exp, long timeoutMilliseconds) throws Throwable;

	public void initialize() throws Throwable;

}
