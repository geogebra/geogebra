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

package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.menu.Action;
import org.geogebra.common.gui.menu.ActionableItem;
import org.geogebra.common.gui.menu.DrawerMenu;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.gui.menu.MenuItemGroup;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;

public class MebisDrawerMenuFactory extends DefaultDrawerMenuFactory {

	/**
	 * Create a new DrawerMenuFactory.
	 * @param platform platform
	 * @param version version
	 * @param logInOperation if loginOperation is not null, it creates menu options that require
	 *                       login based on the {@link LogInOperation#isLoggedIn()} method.
	 */
	public MebisDrawerMenuFactory(GeoGebraConstants.Platform platform,
								  GeoGebraConstants.Version version,
								  LogInOperation logInOperation, boolean enableFileFeatures) {
		super(platform, version, null, logInOperation, false, enableFileFeatures);
	}

	@Override
	public DrawerMenu createDrawerMenu(App app) {
		MenuItemGroup main = createMainMenuItemGroup();
		MenuItemGroup secondary = createSecondaryMenuItemGroup();
		return new DrawerMenuImpl(null, removeNulls(main, secondary));
	}

	private MenuItemGroup createMainMenuItemGroup() {
		return new MenuItemGroupImpl(removeNulls(clearConstruction(), showDownloadAs()));
	}

	@Override
	protected MenuItem showDownloadAs() {
		ActionableItem downloadPng = new ActionableItemImpl(null,
				"Download.PNGImage", Action.EXPORT_IMAGE);
		ActionableItem svg = new ActionableItemImpl(null, "Download.SVGImage",
				Action.DOWNLOAD_SVG);
		ActionableItem pdf = new ActionableItemImpl(null,
				"Download.PDFDocument", Action.DOWNLOAD_PDF);
		return new SubmenuItemImpl(Icon.DOWNLOAD, "Exportieren als", null, downloadPng, svg, pdf);
	}

	private MenuItemGroup createSecondaryMenuItemGroup() {
		return new MenuItemGroupImpl(showAboutBoard(), showHelpAndFeedback(), showTemplates(),
				showLicence());
	}

	private ActionableItem showAboutBoard() {
		return new ActionableItemImpl(Icon.ABOUT_BOARD, "\u00DCber Board", Action.ABOUT_BOARD);
	}

	@Override
	protected ActionableItem showHelpAndFeedback() {
		return new ActionableItemImpl(Icon.HELP, "Hilfe und Tutorials", Action.SHOW_TUTORIALS);
	}

	private ActionableItem showTemplates() {
		return new ActionableItemImpl(Icon.TEMPLATES, "Vorlagen", Action.TEMPLATES);
	}

	private ActionableItem showLicence() {
		return new ActionableItemImpl(Icon.INFO, "Impressum", Action.SHOW_LICENSE);
	}
}
