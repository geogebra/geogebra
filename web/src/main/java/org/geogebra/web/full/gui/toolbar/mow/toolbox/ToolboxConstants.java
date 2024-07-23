package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_AUDIO;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CALCULATOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CAMERA;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_EXTENSION;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_GRASPABLE_MATH;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_H5P;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_IMAGE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MASK;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MIND_MAP;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PDF;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_CIRCLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_ELLIPSE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_FREEFORM;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_LINE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_PENTAGON;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_RECTANGLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_SQUARE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_TRIANGLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TABLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_VIDEO;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ToolboxConstants {
	static List<Integer> uploadCategory = Arrays.asList(MODE_IMAGE, MODE_CAMERA,
			MODE_PDF);
	static List<Integer> linkCategory = Arrays.asList(
			MODE_EXTENSION, MODE_VIDEO, MODE_AUDIO);
	static List<Integer> shapeCategory = Arrays.asList(MODE_SHAPE_RECTANGLE,
			MODE_SHAPE_SQUARE , MODE_SHAPE_TRIANGLE , MODE_SHAPE_CIRCLE , MODE_SHAPE_ELLIPSE,
			MODE_SHAPE_PENTAGON, MODE_SHAPE_LINE, MODE_SHAPE_FREEFORM, MODE_MASK);
	static List<Integer> appsCategory = Arrays.asList(
			MODE_CALCULATOR, MODE_MIND_MAP, MODE_TABLE);

	static List<Integer> getLinkCategory(boolean hasH5P) {
		return hasH5P ? concat(linkCategory, MODE_H5P) : linkCategory;
	}

	static List<Integer> getAppsCategory(boolean hasGraspableMath) {
		return hasGraspableMath ? concat(appsCategory, MODE_GRASPABLE_MATH) : appsCategory;
	}

	private static List<Integer> concat(List<Integer> tools, int tool) {
		return Stream.concat(tools.stream(), Stream.of(tool)).collect(Collectors.toList());
	}
}
