package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_AUDIO;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CALCULATOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CAMERA;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_EXTENSION;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_GRASPABLE_MATH;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_IMAGE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MIND_MAP;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PDF;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TABLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRIANGLE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_VIDEO;

import org.geogebra.web.html5.main.toolbox.ToolboxIcon;

public class ToolModeIconSpecAdapter {

	/**
	 * Get IconSpec of a mode
	 * @param mode - tool mode
	 * @return icon of mode
	 */
	public static ToolboxIcon getToolboxIcon(int mode) {
		switch (mode) {
		case MODE_IMAGE:
			return ToolboxIcon.IMAGE;
		case MODE_CAMERA:
			return ToolboxIcon.CAMERA;
		case MODE_PDF:
			return ToolboxIcon.PDF;
		case MODE_EXTENSION:
			return ToolboxIcon.WEB;
		case MODE_VIDEO:
			return ToolboxIcon.VIDEO;
		case MODE_AUDIO:
			return ToolboxIcon.AUDIO;
		case MODE_CALCULATOR:
			return ToolboxIcon.GEOGEBRA;
		case MODE_MIND_MAP:
			return ToolboxIcon.MINDMAP;
		case MODE_TABLE:
			return ToolboxIcon.TABLE;
		case MODE_GRASPABLE_MATH:
			return ToolboxIcon.GRASPMATH;
		case MODE_RULER:
			return ToolboxIcon.RULER;
		case MODE_PROTRACTOR:
			return ToolboxIcon.PROTRACTOR;
		case MODE_TRIANGLE_PROTRACTOR:
			return ToolboxIcon.TRIANGLE;
		}
		return null;
	}
}
