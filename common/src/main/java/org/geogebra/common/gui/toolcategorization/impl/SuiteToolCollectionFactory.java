package org.geogebra.common.gui.toolcategorization.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.toolcategorization.ToolCategory;
import org.geogebra.common.gui.toolcategorization.ToolCollection;
import org.geogebra.common.gui.toolcategorization.ToolsetLevel;

/**
 * ToolCollectionFactory for the Graphing Calculator app.
 */
public class SuiteToolCollectionFactory extends AbstractToolCollectionFactory {
    @Override
    public ToolCollection createToolCollection() {
        ToolCollectionImpl impl = new ToolCollectionImpl();
        createStandardLevel(impl);
        createAdvancedLevel(impl);

        impl.setLevel(ToolsetLevel.STANDARD);
        return impl;
    }

    private void createStandardLevel(ToolCollectionImpl impl) {
        impl.addLevel(ToolsetLevel.STANDARD);

        impl.extendCategory(ToolCategory.BASIC,
                EuclidianConstants.MODE_MOVE,
                EuclidianConstants.MODE_POINT,
                EuclidianConstants.MODE_SLIDER,
                EuclidianConstants.MODE_INTERSECT,
                EuclidianConstants.MODE_EXTREMUM,
                EuclidianConstants.MODE_ROOTS,
                EuclidianConstants.MODE_FITLINE);

        impl.extendCategory(ToolCategory.EDIT,
                EuclidianConstants.MODE_SELECT,
                EuclidianConstants.MODE_TRANSLATEVIEW,
                EuclidianConstants.MODE_DELETE,
                EuclidianConstants.MODE_SHOW_HIDE_LABEL,
                EuclidianConstants.MODE_SHOW_HIDE_OBJECT);

        if (!isPhoneApp) {
            impl.extendCategory(ToolCategory.MEDIA,
                    EuclidianConstants.MODE_IMAGE,
                    EuclidianConstants.MODE_TEXT);
        }

        impl.extendCategory(ToolCategory.MEASURE,
                EuclidianConstants.MODE_ANGLE,
                EuclidianConstants.MODE_DISTANCE,
                EuclidianConstants.MODE_AREA);

        impl.extendCategory(ToolCategory.TRANSFORM,
                EuclidianConstants.MODE_MIRROR_AT_LINE,
                EuclidianConstants.MODE_MIRROR_AT_POINT,
                EuclidianConstants.MODE_TRANSLATE_BY_VECTOR);

        impl.extendCategory(ToolCategory.CONSTRUCT,
                EuclidianConstants.MODE_MIDPOINT,
                EuclidianConstants.MODE_ORTHOGONAL,
                EuclidianConstants.MODE_LINE_BISECTOR,
                EuclidianConstants.MODE_PARALLEL,
                EuclidianConstants.MODE_ANGULAR_BISECTOR,
                EuclidianConstants.MODE_TANGENTS);

        impl.extendCategory(ToolCategory.LINES,
                EuclidianConstants.MODE_SEGMENT,
                EuclidianConstants.MODE_JOIN,
                EuclidianConstants.MODE_RAY,
                EuclidianConstants.MODE_VECTOR);

        impl.extendCategory(ToolCategory.CIRCLES,
                EuclidianConstants.MODE_CIRCLE_TWO_POINTS,
                EuclidianConstants.MODE_COMPASSES,
                EuclidianConstants.MODE_SEMICIRCLE);
    }

    private void createAdvancedLevel(ToolCollectionImpl impl) {
        impl.addLevel(ToolsetLevel.ADVANCED);

        impl.extendCategory(ToolCategory.BASIC);

        impl.extendCategory(ToolCategory.EDIT,
                EuclidianConstants.MODE_COPY_VISUAL_STYLE);

        if (!isPhoneApp) {
            impl.extendCategory(ToolCategory.MEDIA);
        }

        impl.extendCategory(ToolCategory.MEASURE,
                EuclidianConstants.MODE_ANGLE_FIXED,
                EuclidianConstants.MODE_SLOPE);

        impl.extendCategory(ToolCategory.POINTS,
                EuclidianConstants.MODE_POINT,
                EuclidianConstants.MODE_INTERSECT,
                EuclidianConstants.MODE_POINT_ON_OBJECT,
                EuclidianConstants.MODE_ATTACH_DETACH,
                EuclidianConstants.MODE_EXTREMUM,
                EuclidianConstants.MODE_ROOTS,
                EuclidianConstants.MODE_COMPLEX_NUMBER,
                EuclidianConstants.MODE_CREATE_LIST);

        impl.extendCategory(ToolCategory.CONSTRUCT,
                EuclidianConstants.MODE_LOCUS);

        impl.extendCategory(ToolCategory.LINES,
                EuclidianConstants.MODE_SEGMENT_FIXED,
                EuclidianConstants.MODE_VECTOR_FROM_POINT,
                EuclidianConstants.MODE_POLAR_DIAMETER,
                EuclidianConstants.MODE_POLYLINE);

        impl.extendCategory(ToolCategory.POLYGONS,
                EuclidianConstants.MODE_POLYGON,
                EuclidianConstants.MODE_REGULAR_POLYGON,
                EuclidianConstants.MODE_VECTOR_POLYGON,
                EuclidianConstants.MODE_RIGID_POLYGON);

        impl.extendCategory(ToolCategory.CIRCLES,
                EuclidianConstants.MODE_CIRCLE_POINT_RADIUS,
                EuclidianConstants.MODE_CIRCLE_THREE_POINTS,
                EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS,
                EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS,
                EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS,
                EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);

        impl.extendCategory(ToolCategory.CONICS,
                EuclidianConstants.MODE_ELLIPSE_THREE_POINTS,
                EuclidianConstants.MODE_CONIC_FIVE_POINTS,
                EuclidianConstants.MODE_PARABOLA,
                EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS);

        impl.extendCategory(ToolCategory.TRANSFORM,
                EuclidianConstants.MODE_ROTATE_BY_ANGLE,
                EuclidianConstants.MODE_DILATE_FROM_POINT,
                EuclidianConstants.MODE_MIRROR_AT_CIRCLE);

        List<Integer> others = new ArrayList<>(Arrays.asList(
                EuclidianConstants.MODE_PEN,
                EuclidianConstants.MODE_FREEHAND_SHAPE,
                EuclidianConstants.MODE_RELATION));

        if (!isPhoneApp) {
            others.addAll(Arrays.asList(
                    // EuclidianConstants.MODE_FUNCTION_INSPECTOR,
                    EuclidianConstants.MODE_BUTTON_ACTION,
                    EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX,
                    EuclidianConstants.MODE_TEXTFIELD_ACTION
                    // EuclidianConstants.MODE_CREATE_LIST
            ));
        }
        impl.extendCategory(ToolCategory.OTHERS, others);
    }
}

