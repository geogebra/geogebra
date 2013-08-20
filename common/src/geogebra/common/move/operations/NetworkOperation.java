package geogebra.common.move.operations;

import geogebra.common.move.views.BooleanRenderable;

/**
 * @author gabor
 * Base for offline and online operations
 *
 */
public abstract class NetworkOperation extends BaseOperation<BooleanRenderable> {
	
	/**
	 * The Application is online, or not
	 */
	protected boolean online;
	
	/**
	 * @return if app state is online
	 */
	public boolean getOnline() {
		return online;
	}

	/**
	 * @param online online state
	 * Sets the online state of the app (used from events)
	 */
	public void setOnline(boolean online) {
		this.online = online;		
	}
	
	
}
