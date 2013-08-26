package geogebra.touch.gui.laf;

import geogebra.touch.TouchApp;

/**
 * On 2013-08-06 iOS had TouchStartEvents
 * 
 * @author Matthias Meisinger
 * 
 */
public class AppleLAF extends DefaultLAF {

	public AppleLAF(final TouchApp app) {
		super(app);
	}

	@Override
	public DefaultResources getIcons() {
		return AppleResources.INSTANCE;
	}

	@Override
	public boolean receivesDoubledEvents() {
		return false;
	}
}
