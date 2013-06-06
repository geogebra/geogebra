package geogebra.common.move.operations;

/**
 * @author gabor
 * Base for offline and online operations
 *
 */
public abstract class NetworkOperation extends BaseOperation {
	
	/**
	 * The Application is online, or not
	 */
	protected boolean online;
	
	public Network network = null;

}
