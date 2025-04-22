package org.geogebra.web.html5.main.topbar;

import org.geogebra.web.html5.gui.view.IconSpec;

public class TopBarIconResource {
	private final TopBarIconProvider topBarIconProvider;

	public TopBarIconResource(TopBarIconProvider topBarIconProvider) {
		this.topBarIconProvider = topBarIconProvider;
	}

	/**
	 * @param icon icon
	 * @return spec for given icon
	 */
	public IconSpec getImageResource(TopBarIcon icon) {
		return topBarIconProvider.matchIconWithResource(icon);
	}
}
