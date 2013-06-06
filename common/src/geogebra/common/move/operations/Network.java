package geogebra.common.move.operations;

/**
 * @author gabor
 * interface for checking network is online or not
 */
public interface Network {
	
	/**
	 * @return state of the network
	 */
	public boolean onLine();

}
