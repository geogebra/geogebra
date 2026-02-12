/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.contextmenu;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.main.undo.UpdateContentActionStore;
import org.geogebra.common.properties.impl.objects.FontProperty;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.user.client.ui.Widget;

/**
 * Submenu for Font item in 3-dot menu of inline text
 *
 * @author Laszlo
 *
 */
public class FontSubMenu extends AriaMenuBar {

	private final List<FontProperty.FontFamily> fonts;
	private final List<HasTextFormat> formatters;
	private AriaMenuItem highlighted;

	/**
	 * @param app the application
	 * @param formatters to format text.
	 */
	public FontSubMenu(AppW app, List<HasTextFormat> formatters) {
		this.fonts = FontProperty.FontFamily.getAvailableFonts(app.isByCS());
		this.formatters = formatters;
		createItems();
	}

	private void createItems() {
		for (final FontProperty.FontFamily font : fonts) {
			if (!font.equals(FontProperty.FontFamily.TEST)
					|| PreviewFeature.isAvailable(PreviewFeature.TEST_FONT)) {
				ScheduledCommand command = () -> setFontName(font.cssName());
				AriaMenuItem item = new AriaMenuItem(font.displayName(), null, command);
				addItem(item);
			}
		}
	}

	private void setFontName(String cssName) {
		ArrayList<GeoInline> geosToStore = new ArrayList<>();
		for (HasTextFormat formatter : formatters) {
			geosToStore.add(formatter.getInline());
		}

		UpdateContentActionStore store = new UpdateContentActionStore(geosToStore);
		for (HasTextFormat formatter : formatters) {
			formatter.format("font", cssName);
		}
		if (store.needUndo()) {
			store.storeUndo();
		}
	}

	@Override
	public void stylePopup(Widget widget) {
		highlightCurrent();
	}

	private void highlightCurrent() {
		if (formatters.isEmpty()) {
			return;
		}

		String font = formatters.get(0).getFormat("font", "");
		for (FontProperty.FontFamily family : fonts) {
			if (font.equals(family.cssName())) {
				highlightItem(fonts.indexOf(family));
				return;
			}
		}
		unselect();
	}

	private void highlightItem(int index) {
		if (highlighted != null) {
			highlighted.removeStyleName("highlighted");
		}

		if (index < 0) {
			return;
		}

		highlighted = getItemAt(index);
		highlighted.addStyleName("highlighted");
	}
}
