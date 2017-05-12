package org.geogebra.common.gui.toolcategorization;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.main.App;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by mathieu on 11/05/17.
 */

public class ToolCategorization {

    public enum Type {GEOMETRY, GRAPHING_CALCULATOR, GRAPHER_3D}

    public enum Category {
        BASIC("Basic"), LINES("Lines"), CIRCLES("Circles");

        private final String header;

        Category(String header) {
            this.header = header;
        }

        public String getHeader() {
            return header;
        }
    }

    private Type type;
    private ArrayList<Category> customizedCategories;
    private ArrayList<ArrayList<Integer>> toolsLists;
    private TreeSet<Integer> availableTools;

    private App app;

    /**
     * Creates a tool categorization for the give type
     *
     * @param app  App (for localization)
     * @param type categorization type
     */
    public ToolCategorization(App app, Type type) {
        this.app = app;
        toolsLists = new ArrayList<ArrayList<Integer>>();
        customizedCategories = new ArrayList<Category>();
        this.type = type;
    }

    /**
     * @return categories used
     */
    public ArrayList<Category> getCategories() {
        return customizedCategories;
    }

    /**
     * @param category tools category
     * @return localized header for this category
     */
    public String getLocalizedHeader(Category category) {
        return app.getLocalization().getPlain(category.getHeader());
    }

    /**
     * categoryId is the rank of the category over the categories list
     *
     * @param categoryId category id
     * @return list of tools for that category
     */
    public ArrayList<Integer> getTools(int categoryId) {
        return toolsLists.get(categoryId);
    }

    /**
     * reset tools & categories list
     */
    public void resetTools() {
        resetTools(null);
    }

    /**
     * reset tools & categories list, keeping only tools present in toolbarDef
     *
     * @param toolbarDef toolbar definition
     */
    public void resetTools(String toolbarDef) {
        if (toolbarDef == null) {
            this.availableTools = null;
        } else {
            this.availableTools = ToolBar.toSet(toolbarDef);
        }
        toolsLists.clear();
        customizedCategories.clear();
        buildTools();
    }

    private void buildTools() {
        Category category;
        ArrayList<Integer> tools;
        switch (type) {
            case GEOMETRY:
            case GRAPHING_CALCULATOR:
            case GRAPHER_3D:
            default:
                category = Category.BASIC;
                tools = new ArrayList<Integer>();
                addToList(tools, EuclidianConstants.MODE_MOVE);
                addToList(tools, EuclidianConstants.MODE_POINT);
                addToList(tools, EuclidianConstants.MODE_SEGMENT);
                addToList(tools, EuclidianConstants.MODE_JOIN);
                addToList(tools, EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
                addToList(tools, EuclidianConstants.MODE_POLYGON);
                addToList(tools, EuclidianConstants.MODE_INTERSECT);
                storeIfNotEmpty(category, tools);

                category = Category.LINES;
                tools = new ArrayList<Integer>();
                addToList(tools, EuclidianConstants.MODE_ORTHOGONAL);
                addToList(tools, EuclidianConstants.MODE_LINE_BISECTOR);
                addToList(tools, EuclidianConstants.MODE_PARALLEL);
                addToList(tools, EuclidianConstants.MODE_ANGULAR_BISECTOR);
                addToList(tools, EuclidianConstants.MODE_SEGMENT_FIXED);
                addToList(tools, EuclidianConstants.MODE_SEGMENT);
                addToList(tools, EuclidianConstants.MODE_JOIN);
                addToList(tools, EuclidianConstants.MODE_RAY);
                addToList(tools, EuclidianConstants.MODE_VECTOR);
                addToList(tools, EuclidianConstants.MODE_POLYLINE);
                addToList(tools, EuclidianConstants.MODE_TANGENTS);
                addToList(tools, EuclidianConstants.MODE_VECTOR_FROM_POINT);
                addToList(tools, EuclidianConstants.MODE_FITLINE);
                addToList(tools, EuclidianConstants.MODE_POLAR_DIAMETER);
                storeIfNotEmpty(category, tools);

                category = Category.CIRCLES;
                tools = new ArrayList<Integer>();
                addToList(tools, EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
                addToList(tools, EuclidianConstants.MODE_COMPASSES);
                addToList(tools, EuclidianConstants.MODE_CIRCLE_POINT_RADIUS);
                addToList(tools, EuclidianConstants.MODE_CIRCLE_THREE_POINTS);
                addToList(tools, EuclidianConstants.MODE_SEMICIRCLE);
                addToList(tools, EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS);
                addToList(tools, EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS);
                addToList(tools, EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
                addToList(tools, EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);
                storeIfNotEmpty(category, tools);
                break;
        }


    }

    private void storeIfNotEmpty(Category category, ArrayList<Integer> tools) {
        if (!tools.isEmpty()) {
            customizedCategories.add(category);
            toolsLists.add(tools);
        }
    }

    final private void addToList(ArrayList<Integer> toolList, int mode) {
        if (availableTools == null || availableTools.contains(mode)) {
            toolList.add(mode);
        }
    }
}
