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
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ERASER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_EXTENSION;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_GRASPABLE_MATH;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_HIGHLIGHTER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_IMAGE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MASK;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MEDIA_TEXT;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MIND_MAP;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PDF;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_CIRCLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_CURVE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_ELLIPSE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_FREEFORM;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_LINE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_PARALLELOGRAM;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_PENTAGON;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_RECTANGLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_SQUARE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_STADIUM;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_TRIANGLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TABLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_VIDEO;

import java.util.Arrays;
import java.util.List;

public enum ToolboxCategory {
	SELECT("select"),
	PEN("pen", MODE_PEN, MODE_HIGHLIGHTER, MODE_ERASER),
	SHAPES("shapes", MODE_SHAPE_RECTANGLE,
			MODE_SHAPE_SQUARE , MODE_SHAPE_PARALLELOGRAM, MODE_SHAPE_STADIUM,
			MODE_SHAPE_TRIANGLE , MODE_SHAPE_CIRCLE , MODE_SHAPE_ELLIPSE,
			MODE_SHAPE_PENTAGON, MODE_SHAPE_LINE, MODE_SHAPE_CURVE, MODE_SHAPE_FREEFORM, MODE_MASK),
	TEXT("text", MODE_MEDIA_TEXT, MODE_EQUATION),
	UPLOAD("upload", MODE_IMAGE, MODE_CAMERA,
			MODE_PDF),
	LINK("link", MODE_EXTENSION, MODE_VIDEO, MODE_AUDIO),
	MORE("more", MODE_CALCULATOR, MODE_MIND_MAP, MODE_TABLE, MODE_GRASPABLE_MATH),
	SPOTLIGHT("spotlight"),
	RULER("ruler");

	private final String category;
	private final List<Integer> tools;

	ToolboxCategory(String category, Integer... tools) {
		this.category = category;
		this.tools = Arrays.asList(tools);
	}

	static ToolboxCategory byName(String category) {
		for (ToolboxCategory item: values()) {
			if (item.getName().equalsIgnoreCase(category)) {
				return item;
			}
		}
		return MORE;
	}

	public String getName() {
		return category;
	}

	public List<Integer> getTools() {
		return tools;
	}
}

