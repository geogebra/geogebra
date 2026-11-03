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

package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_AUDIO;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CALCULATOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CAMERA;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_EQUATION;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_EXTENSION;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_GRASPABLE_MATH;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_IMAGE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MEDIA_TEXT;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MIND_MAP;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PDF;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TABLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRIANGLE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_VIDEO;

import org.geogebra.web.html5.main.toolbox.ToolboxIcon;

public final class ToolModeIconSpecAdapter {

	/**
	 * Get IconSpec of a mode
	 * @param mode - tool mode
	 * @return icon of mode
	 */
	public static ToolboxIcon getToolboxIcon(int mode) {
		return switch (mode) {
			case MODE_IMAGE -> ToolboxIcon.IMAGE;
			case MODE_CAMERA -> ToolboxIcon.CAMERA;
			case MODE_PDF -> ToolboxIcon.PDF;
			case MODE_EXTENSION -> ToolboxIcon.WEB;
			case MODE_VIDEO -> ToolboxIcon.VIDEO;
			case MODE_AUDIO -> ToolboxIcon.AUDIO;
			case MODE_CALCULATOR -> ToolboxIcon.GEOGEBRA;
			case MODE_MIND_MAP -> ToolboxIcon.MINDMAP;
			case MODE_TABLE -> ToolboxIcon.TABLE;
			case MODE_GRASPABLE_MATH -> ToolboxIcon.GRASPMATH;
			case MODE_RULER -> ToolboxIcon.RULER;
			case MODE_PROTRACTOR -> ToolboxIcon.PROTRACTOR;
			case MODE_TRIANGLE_PROTRACTOR -> ToolboxIcon.RULER_TRIANGLE;
			case MODE_EQUATION -> ToolboxIcon.EQUATION;
			case MODE_MEDIA_TEXT -> ToolboxIcon.TEXT;
			default -> null;
		};
	}
}
