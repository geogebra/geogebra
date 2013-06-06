package geogebra.common.move.operations;

/**
 * @author gabor
 * 
 * Operational (logic) class for Online functionality
 *
 */
public class OnlineOperation extends NetworkOperation {

	/**
	 * Creates a new offlineOperation class for Online functionality
	 * @param network The implementation of Network interface
	 */
	public OnlineOperation(Network network) {
		this.online = network.onLine();
	}
}
