package geogebra.common.move.operations;

import geogebra.common.move.views.BooleanRenderable;
import geogebra.common.move.views.OfflineView;

/**
 * @author gabor
 * Base for offline and online operations
 *
 */
public class NetworkOperation extends BaseOperation<BooleanRenderable> {
	
	/**
	 * Creates a new offlineOperation class for Offline functionality
	 * @param network The implementation of the Network interface
	 */
	public NetworkOperation(Network network) {
		this.online = network.onLine();
	}
	
	@Override
	public OfflineView getView() {
		return (OfflineView) view;
	}
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
		((OfflineView) view).render(false);
	}
	
	
}
