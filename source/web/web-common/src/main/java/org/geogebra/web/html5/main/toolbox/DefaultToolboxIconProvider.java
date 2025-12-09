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

package org.geogebra.web.html5.main.toolbox;

import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.resources.SVGResource;

public class DefaultToolboxIconProvider implements ToolboxIconProvider {

	private static final DefaultToolboxIconResources res = DefaultToolboxIconResources.INSTANCE;

	@Override
	public IconSpec matchIconWithResource(ToolboxIcon icon) {
		return new ImageIconSpec(findImage(icon));
	}

	private SVGResource findImage(ToolboxIcon icon) {
		switch (icon) {
		case MOUSE_CURSOR:
			return res.mouse_cursor();
		case PEN:
			return res.pen();
		case HIGHLIGHTER:
			return res.highlighter();
		case ERASER:
			return res.eraser();
		case PLUS:
			return res.add_black();
		case SHAPES:
			return res.shapes();
		case TEXTS:
			return res.texts();
		case TEXT:
			return res.text();
		case EQUATION:
			return res.equation();
		case UPLOAD:
			return res.upload();
		case IMAGE:
			return res.image();
		case CAMERA:
			return res.camera();
		case PDF:
			return res.pdf();
		case LINK:
			return res.link();
		case WEB:
			return res.web();
		case VIDEO:
			return res.video();
		case AUDIO:
			return res.audio();
		case APPS:
			return res.apps();
		case GEOGEBRA:
			return res.geogebra();
		case MINDMAP:
			return res.mindmap();
		case TABLE:
			return res.table();
		case GRASPMATH:
			return res.grasphmath();
		case RULER:
			return res.ruler();
		case PROTRACTOR:
			return res.ruler_protractor();
		case TRIANGLE:
			return res.ruler_triangle();
		case SPOTLIGHT:
			return res.target();
		default:
			return null;
		}
	}
}
