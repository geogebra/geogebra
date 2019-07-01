package org.geogebra.web.html5.gui.laf;

import org.geogebra.web.html5.util.ArticleElementInterface;

/**
 * Factory class, creates VendorSettings objects.
 */
public class VendorSettingsFactory {

	private ArticleElementInterface articleElement;

	/**
	 * Create a new VendorSettingsFactory instance.
	 *
	 * @param articleElement article element
	 */
	public VendorSettingsFactory(ArticleElementInterface articleElement) {
		this.articleElement = articleElement;
	}

	/**
	 * Create a new VendorSettings instance.
	 *
	 * @return VendorSettings.
	 */
	public VendorSettings createVendorSettings() {
		if ("mebis".equalsIgnoreCase(articleElement.getParamVendor())) {
			return new MebisSettings();
		} else {
			return new GgbSettings();
		}
	}
}
