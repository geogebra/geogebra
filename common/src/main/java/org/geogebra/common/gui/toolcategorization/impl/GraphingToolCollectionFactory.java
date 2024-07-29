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
public class GraphingToolCollectionFactory extends AbstractToolCollectionFactory {

    @Override
    public ToolCollection createToolCollection() {
        ToolCollectionImpl impl = new ToolCollectionImpl();
        createTools(impl);

        return impl;
    }

    private void createTools(ToolCollectionImpl impl) {
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
                EuclidianConstants.MODE_SHOW_HIDE_OBJECT,
                EuclidianConstants.MODE_COPY_VISUAL_STYLE);

        if (!isPhoneApp) {
            impl.extendCategory(ToolCategory.MEDIA,
                    EuclidianConstants.MODE_TEXT);
        }

        impl.extendCategory(ToolCategory.POINTS,
                EuclidianConstants.MODE_POINT,
                EuclidianConstants.MODE_INTERSECT,
                EuclidianConstants.MODE_POINT_ON_OBJECT,
                EuclidianConstants.MODE_ATTACH_DETACH,
                EuclidianConstants.MODE_EXTREMUM,
                EuclidianConstants.MODE_ROOTS,
                EuclidianConstants.MODE_COMPLEX_NUMBER,
                EuclidianConstants.MODE_CREATE_LIST);

        impl.extendCategory(ToolCategory.LINES,
                EuclidianConstants.MODE_JOIN,
                EuclidianConstants.MODE_RAY,
                EuclidianConstants.MODE_VECTOR);

        List<Integer> others = new ArrayList<>(Arrays.asList(
                EuclidianConstants.MODE_PEN,
                EuclidianConstants.MODE_FREEHAND_FUNCTION));

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
