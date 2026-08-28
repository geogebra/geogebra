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

package org.geogebra.web.full.gui.menu.icons;

import org.geogebra.common.gui.menu.Icon;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.resources.SVGResource;

/**
 * Gives default access to menu icons.
 */
public class DefaultMenuIconProvider implements MenuIconProvider {

	private static final DefaultMenuIconResources res = DefaultMenuIconResources.INSTANCE;

	@Override
	public IconSpec matchIconWithResource(Icon icon) {
		return icon != null ? new ImageIconSpec(findImage(icon)) : null;
	}

	private SVGResource findImage(Icon icon) {
		return switch (icon) {
			case CLEAR -> res.clear();
			case DOWNLOAD -> res.download();
			case SAVE -> res.save();
			case SAVE_ONLINE -> res.saveOnline();
			case HOURGLASS_EMPTY -> res.hourglassEmpty();
			case GEOGEBRA -> res.geogebra();
			case SETTINGS -> res.settings();
			case HELP -> res.help();
			case PRINT -> res.print();
			case SEARCH -> res.search();
			case EXPORT_FILE -> res.exportFile();
			case EXPORT_IMAGE -> res.exportImage();
			case ASSIGNMENT -> res.assignment();
			case SCHOOL -> res.school();
			case BUG_REPORT -> res.bugReport();
			case INFO -> res.info();
			case PRIVACY_POLICY -> res.privacyPolicy();
			case SIGN_IN -> res.signIn();
			case SIGN_OUT -> res.signOut();
			case FOLDER -> res.folder();
			default -> null;
		};
	}
}
