package geogebra.cas.maxima;

/**
 * This exception is thrown when the version of Maxima detected on the user's
 * system is unsupported.
 * Currently (2010-10-21) this is the case for Maxima < 5.21. See ticket #295
 * 
 * @author Thomas Unterthiner
 *
 */
public class MaximaVersionUnsupportedExecption extends RuntimeException {

	private static final long serialVersionUID = 9036778388214698596L;
	private int[] version;
	
	
	/**
	 * @param version  The version that was detected on the system.
	 */
	public MaximaVersionUnsupportedExecption(int[] version)
	{
		super("Unsupported maxima version detected");
		this.version = version;
	}
	
	/**
	 * @return The version that was detected on the system.
	 */
	public int[] getDetectedVersion()
	{
		return version;
	}
}
