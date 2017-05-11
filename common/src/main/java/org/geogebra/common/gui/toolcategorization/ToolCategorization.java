package org.geogebra.common.gui.toolcategorization;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.main.App;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by mathieu on 11/05/17.
 */

public class ToolCategorization {

    public enum Type {GEOMETRY, GRAPHING_CALCULATOR, GRAPHER_3D}

    public enum Category {
        BASIC("Basic"), LINES("Lines"), CIRCLES("Circles");
//
//        static private int categoriesLength;
//        private int id;

        private final String header;

        Category(String header) {
            this.header = header;
        }

        public String getHeader() {
            return header;
        }
    }

    private TreeMap<Category, ArrayList<Integer>> categoriesMap;

    private App app;

    public ToolCategorization(App app) {
        this.app = app;
        categoriesMap = new TreeMap<Category, ArrayList<Integer>>();
    }

    /**
     * @param type categorization type
     * @return categories used for this categorization type
     */
    public Category[] getCategories(Type type) {
        switch (type) {
            case GEOMETRY:
            case GRAPHING_CALCULATOR:
            case GRAPHER_3D:
            default:
                return new Category[]{Category.BASIC, Category.LINES, Category.CIRCLES};
        }
    }

    /**
     * @param category tools category
     * @return localized header for this category
     */
    public String getLocalizedHeader(Category category) {
        return app.getLocalization().getPlain(category.getHeader());
    }

    public ArrayList<Integer> getTools(Category category) {
        ArrayList<Integer> ret = categoriesMap.get(category);
        if (ret == null) {
            ret = storeTools(category);
        }
        return ret;
    }

    private ArrayList<Integer> storeTools(Category category) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        switch (category) {
            case BASIC:
                addToList(ret, EuclidianConstants.MODE_MOVE);
                addToList(ret, EuclidianConstants.MODE_POINT);
                addToList(ret, EuclidianConstants.MODE_SEGMENT);
                addToList(ret, EuclidianConstants.MODE_JOIN);
                addToList(ret, EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
                addToList(ret, EuclidianConstants.MODE_POLYGON);
                addToList(ret, EuclidianConstants.MODE_INTERSECT);
                break;
            case LINES:
                addToList(ret, EuclidianConstants.MODE_ORTHOGONAL);
                addToList(ret, EuclidianConstants.MODE_LINE_BISECTOR);
                addToList(ret, EuclidianConstants.MODE_PARALLEL);
                addToList(ret, EuclidianConstants.MODE_ANGULAR_BISECTOR);
                addToList(ret, EuclidianConstants.MODE_SEGMENT_FIXED);
                addToList(ret, EuclidianConstants.MODE_SEGMENT);
                addToList(ret, EuclidianConstants.MODE_JOIN);
                addToList(ret, EuclidianConstants.MODE_RAY);
                addToList(ret, EuclidianConstants.MODE_VECTOR);
                addToList(ret, EuclidianConstants.MODE_POLYLINE);
                addToList(ret, EuclidianConstants.MODE_TANGENTS);
                addToList(ret, EuclidianConstants.MODE_VECTOR_FROM_POINT);
                addToList(ret, EuclidianConstants.MODE_FITLINE);
                addToList(ret, EuclidianConstants.MODE_POLAR_DIAMETER);
                break;
            case CIRCLES:
                addToList(ret, EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
                addToList(ret, EuclidianConstants.MODE_COMPASSES);
                addToList(ret, EuclidianConstants.MODE_CIRCLE_POINT_RADIUS);
                addToList(ret, EuclidianConstants.MODE_CIRCLE_THREE_POINTS);
                addToList(ret, EuclidianConstants.MODE_SEMICIRCLE);
                addToList(ret, EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS);
                addToList(ret, EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS);
                addToList(ret, EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
                addToList(ret, EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);
                break;
        }
        categoriesMap.put(category, ret);
        return ret;
    }

    final private void addToList(ArrayList<Integer> toolList, int mode) {
        toolList.add(mode);
    }
}
