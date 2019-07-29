package org.geogebra.common.gui.toolcategorization.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.toolcategorization.ToolCollection;

/**
 * ToolCollectionFactory for the 3D Grapher app.
 */
public class Graphing3DToolCollectionFactory extends AbstractToolCollectionFactory {

    @Override
    public ToolCollection createToolCollection() {
        ToolCollectionImpl impl = new ToolCollectionImpl();
        createStandardLevel(impl);
        createAdvancedLevel(impl);

        impl.setLevel(0);
        return impl;
    }

    private void createStandardLevel(ToolCollectionImpl impl) {
        impl.addLevel(LEVEL_STANDARD);

        impl.extendCategory(CATEGORY_BASIC,
                EuclidianConstants.MODE_MOVE,
                EuclidianConstants.MODE_POINT,
                EuclidianConstants.MODE_PYRAMID,
                EuclidianConstants.MODE_CUBE,
                EuclidianConstants.MODE_SPHERE_TWO_POINTS,
                EuclidianConstants.MODE_PLANE_THREE_POINTS,
                EuclidianConstants.MODE_INTERSECTION_CURVE,
                EuclidianConstants.MODE_NET);
    }

    private void createAdvancedLevel(ToolCollectionImpl impl) {
        impl.addLevel(LEVEL_ADVANCED);

        impl.extendCategory(CATEGORY_BASIC);

        impl.addCategory(CATEGORY_EDIT,
                EuclidianConstants.MODE_SHOW_HIDE_LABEL,
                EuclidianConstants.MODE_SHOW_HIDE_OBJECT,
                EuclidianConstants.MODE_DELETE,
                EuclidianConstants.MODE_VIEW_IN_FRONT_OF);

        impl.addCategory(CATEGORY_POINTS,
                EuclidianConstants.MODE_POINT,
                EuclidianConstants.MODE_INTERSECT,
                EuclidianConstants.MODE_MIDPOINT,
                EuclidianConstants.MODE_POINT_ON_OBJECT,
                EuclidianConstants.MODE_ATTACH_DETACH);

		impl.addCategory(CATEGORY_LINES_AND_POLYGONS,
				EuclidianConstants.MODE_SEGMENT,
				EuclidianConstants.MODE_SEGMENT_FIXED,
				EuclidianConstants.MODE_JOIN,
                EuclidianConstants.MODE_RAY,
				EuclidianConstants.MODE_VECTOR,
                EuclidianConstants.MODE_POLYGON,
				EuclidianConstants.MODE_REGULAR_POLYGON,
				EuclidianConstants.MODE_ORTHOGONAL_THREE_D,
				EuclidianConstants.MODE_PARALLEL,
				EuclidianConstants.MODE_ANGULAR_BISECTOR,
				EuclidianConstants.MODE_TANGENTS);

        impl.addCategory(CATEGORY_SOLIDS,
                EuclidianConstants.MODE_PYRAMID,
                EuclidianConstants.MODE_PRISM,
                EuclidianConstants.MODE_TETRAHEDRON,
                EuclidianConstants.MODE_CUBE,
                EuclidianConstants.MODE_SPHERE_TWO_POINTS,
                EuclidianConstants.MODE_SPHERE_POINT_RADIUS,
                EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS,
                EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS,
                EuclidianConstants.MODE_CONIFY,
                EuclidianConstants.MODE_EXTRUSION,
                EuclidianConstants.MODE_NET);

        impl.addCategory(CATEGORY_PLANES,
                EuclidianConstants.MODE_PLANE_THREE_POINTS,
                EuclidianConstants.MODE_PLANE,
                EuclidianConstants.MODE_PARALLEL_PLANE,
                EuclidianConstants.MODE_ORTHOGONAL_PLANE);

        impl.addCategory(CATEGORY_CIRCLES,
                EuclidianConstants.MODE_CIRCLE_AXIS_POINT,
                EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION,
                EuclidianConstants.MODE_CIRCLE_THREE_POINTS,
                EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS,
                EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS,
                EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS,
                EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);

        impl.addCategory(CATEGORY_CURVES,
                EuclidianConstants.MODE_ELLIPSE_THREE_POINTS,
                EuclidianConstants.MODE_CONIC_FIVE_POINTS,
                EuclidianConstants.MODE_PARABOLA,
                EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS,
                EuclidianConstants.MODE_LOCUS,
                EuclidianConstants.MODE_INTERSECTION_CURVE);

        impl.addCategory(CATEGORY_TRANSFORM,
                EuclidianConstants.MODE_MIRROR_AT_PLANE,
                EuclidianConstants.MODE_MIRROR_AT_POINT,
                EuclidianConstants.MODE_ROTATE_AROUND_LINE,
                EuclidianConstants.MODE_TRANSLATE_BY_VECTOR,
                EuclidianConstants.MODE_DILATE_FROM_POINT,
                EuclidianConstants.MODE_MIRROR_AT_LINE);

        impl.addCategory(CATEGORY_MEASURE,
                EuclidianConstants.MODE_ANGLE,
                EuclidianConstants.MODE_DISTANCE,
                EuclidianConstants.MODE_AREA,
                EuclidianConstants.MODE_VOLUME);

        List<Integer> others = new ArrayList<>(Arrays.asList(
                EuclidianConstants.MODE_ROTATEVIEW,
                EuclidianConstants.MODE_TRANSLATEVIEW,
                EuclidianConstants.MODE_COPY_VISUAL_STYLE
        ));
        if (!isPhoneApp) {
            others.add(EuclidianConstants.MODE_TEXT);
        }
        impl.addCategory(CATEGORY_OTHERS, others);

        impl.addCategory(CATEGORY_SPECIAL_LINES,
                EuclidianConstants.MODE_VECTOR_FROM_POINT,
                EuclidianConstants.MODE_POLYLINE,
                // EuclidianConstants.MODE_FITLINE,
                EuclidianConstants.MODE_POLAR_DIAMETER);
    }
}
