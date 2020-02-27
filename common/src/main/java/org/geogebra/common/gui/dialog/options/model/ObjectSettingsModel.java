package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.impl.objects.SlopeSizeProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a model for the object properties
 */

abstract public class ObjectSettingsModel {

    /**
     * Application
     */
    protected App app;
    private GeoElement geoElement;
    private List<GeoElement> geoElementsList;
    private SlopeSizeProperty slopeSizeProperty;

    /**
     * Default constructor
     */
    public ObjectSettingsModel() {
    }

    /**
     * @param app
     *         Application
     */
    public ObjectSettingsModel(App app) {
        init(app);
    }

    /**
     * @param app1
     *         Application
     */
    protected void init(App app1) {
        this.app = app1;
		geoElementsList = new ArrayList<>();
    }

    /**
     * @return the color of the geoElement
     */
    public GColor getColor() {
        return geoElement != null ? geoElement.getObjectColor() : GColor.BLACK;
    }

    /**
     * @param color
     *         GColor which should be set for all selected geoElements
     */
    public void setColor(GColor color) {
        if (geoElement == null) {
            return;
        }

        if (!hasFurtherStyle()) {
            EuclidianStyleBarStatic.applyTextColor(geoElementsList, color);
        } else {
            EuclidianStyleBarStatic.applyColor(color, geoElement.getAlphaValue(), app);
        }

        app.setPropertiesOccured();
    }

    /**
     * @return if the label of the geoElement is visible or not
     */
    public boolean isLabelShown() {
        return geoElement != null && geoElement.isLabelVisible();
    }

    /**
     * @param show
     *         the label of the geoElement should be shown or not
     */
    public void showLabel(boolean show) {
        for (GeoElement geo : geoElementsList) {
            geo.setLabelVisible(show);
            geo.updateRepaint();
        }
    }

    /**
     * @return the label mode of the geoElement, default is LABEL_NAME
     */
    public int getLabelStyle() {
		return geoElement != null ? geoElement.getLabelMode() : GeoElementND.LABEL_NAME;
    }

    /**
     * @param mode
     *         set it as label mode for all selected geoElement
     */
    public void setLabelStyle(int mode) {
        if (geoElement == null) {
            return;
        }

        for (GeoElement geo : geoElementsList) {
            geo.setLabelMode(mode);
        }

        showLabel(true);
        app.setPropertiesOccured();
    }

    /**
     * @return the label string of the current label mode of the geoElement or with empty string if it is null
     */
    public String getLabelString() {
        if (geoElement == null) {
            return "";
        }

        if (!geoElement.isLabelVisible()) {
            return App.getLabelStyleName(app, -1);
        }

        return App.getLabelStyleName(app, geoElement.getLabelMode());
    }

    /**
     * @return the current line style of the geoElement or with DEFAULT_LINE_TYPE if it is null
     */
    public int getLineStyle() {
        return geoElement != null ? geoElement.getLineType() : EuclidianStyleConstants.DEFAULT_LINE_TYPE;
    }

    /**
     * @param style
     *         set the line style for all selected geoElements
     */
    public void setLineStyle(int style) {
        if (geoElement == null) {
            return;
        }

        for (GeoElement geo : geoElementsList) {
            geo.setLineType(style);
            geo.updateVisualStyleRepaint(GProperty.LINE_STYLE);
        }

        app.setPropertiesOccured();
    }

    /**
     * @return the current point style of the geoElement, or with POINT_STYLE_DOT if it is null
     */
    public int getPointStyle() {
        if (!(geoElement instanceof PointProperties)) {
            return EuclidianStyleConstants.POINT_STYLE_DOT;
        }

        return ((PointProperties) geoElement).getPointStyle();
    }

    /**
     * @param pointStyle
     *         set the point style for all selected geoElements
     */
    public void setPointStyle(int pointStyle) {
		for (GeoElement geo : geoElementsList) {
    		if (geo instanceof PointProperties) {
				((PointProperties) geo).setPointStyle(pointStyle);
				geo.updateVisualStyleRepaint(GProperty.POINT_STYLE);
			}
		}
		app.setPropertiesOccured();
	}

	/**
	 * @deprecated use getLineThickness for lines, getPointSize for points
	 * @return point size or line thickness (not well defined for a list with
	 *         one line and one point)
	 */
	public int getSize() {
		if (geoElement instanceof PointProperties) {
			return ((PointProperties) geoElement).getPointSize();
		}
		return getLineThickness();
	}

    /**
     * @return the point size or line thickness, depending on the geoElement's type
     */
    public int getLineThickness() {
        if (geoElement == null) {
            return EuclidianStyleConstants.DEFAULT_LINE_THICKNESS;
        }
        return geoElement.getLineThickness();
    }

    /**
     * @return the point size or line thickness, depending on the geoElement's type
     */
    public int getPointSize() {
        if (geoElement instanceof PointProperties) {
            return ((PointProperties) geoElement).getPointSize();
        }
        return EuclidianStyleConstants.DEFAULT_POINT_SIZE;
    }

    private void setSize(GeoElement geoElement, int size) {
        if (geoElement instanceof GeoList) {
            GeoList geoList = (GeoList) geoElement;
            for (int i = 0; i < geoList.size(); i++) {
                setSize(geoList.get(i), size);
            }
        } else if (geoElement instanceof PointProperties) {
            ((PointProperties) geoElement).setPointSize(size + 1);
        } else if (LineStyleModel.match(geoElement)) {
            geoElement.setLineThickness(size + geoElement.getMinimumLineThickness());
        }
    }

    /**
     * @param size
     *         set the size of the geoElement depending on if it is Point or Line
     */
    public void setSize(int size) {
        if (geoElement == null) {
            return;
        }

        for (GeoElement geo : geoElementsList) {
            setSize(geo, size);
            geo.updateRepaint();
        }
        app.setPropertiesOccured();
    }

    private void setPointSize(GeoElement geoElement, int size) {
        if (geoElement instanceof GeoList) {
            GeoList geoList = (GeoList) geoElement;
            for (int i = 0; i < geoList.size(); i++) {
                setPointSize(geoList.get(i), size);
            }
        } else if (geoElement instanceof PointProperties) {
            ((PointProperties) geoElement).setPointSize(size + getMinSize());
        }
    }

    /**
     * @param size
     *         point size to set
     */
    public void setPointSize(int size) {
        for (GeoElement geo : geoElementsList) {
            setPointSize(geo, size);
            geo.updateRepaint();
        }
        app.setPropertiesOccured();
    }

    private void setLineThickness(GeoElement geoElement, int size) {
        if (geoElement instanceof GeoList) {
            GeoList geoList = (GeoList) geoElement;
            for (int i = 0; i < geoList.size(); i++) {
                setLineThickness(geoList.get(i), size);
            }
        } else if (LineStyleModel.match(geoElement)) {
            geoElement.setLineThickness(size + geoElement.getMinimumLineThickness());
        }
    }

    /**
     * @param size
     *         line thickness to set
     */
    public void setLineThickness(int size) {
        for (GeoElement geo : geoElementsList) {
            setLineThickness(geo, size);
            geo.updateRepaint();
        }
        app.setPropertiesOccured();
    }

    /**
     * @return the minimum size of the point size or the line thickness depending of the geoElement's type
     */
    public int getMinSize() {
        if (geoElement == null || geoElement instanceof PointProperties) {
            return 1;
        }

        return geoElement.getMinimumLineThickness();
    }

    /**
     * @return max size
     */
    public int getMaxSize() {
        return 9;
    }

    /**
     * @return whether the geoElement is fixed or not
     */
    public boolean isObjectFixed() {
        return geoElement != null && geoElement.isLocked();
    }

    /**
     * @return whether the geos show fix/unfix button
     */
    public boolean areObjectsShowingFixUnfix() {
        if (geoElement == null || (hasFunctionProperties() && app.getConfig().isObjectDraggingRestricted())) {
            return false;
        }

        for (GeoElement geo : geoElementsList) {
            if (!geo.showFixUnfix()) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param objectFixed
     *         geoElement should be fixed or not
     */
    public void setObjectFixed(boolean objectFixed) {
        if (geoElement == null) {
            return;
        }

        for (GeoElement geo : geoElementsList) {
            geo.setFixed(objectFixed);
        }

        app.setPropertiesOccured();
    }

    /**
     * @param name
     *         new name of the geoElement
     */
    public void rename(String name) {
        if (geoElement != null) {
            GeoElement geo;
            if (geoElementsList.size() == 1) {
                geo = geoElementsList.get(0);
            } else {
                return; // should not happen...
            }

            if (LabelManager.isValidLabel(name, geo.getKernel(), geo)) {
                geo.rename(name);
                geo.setAlgebraLabelVisible(true);
                geo.setDescriptionNeedsUpdateInAV(true);
                geo.getKernel().notifyUpdate(geo);
                geo.updateRepaint();
                app.setPropertiesOccured();
            }

        }
    }

    /**
     * @return name description of the geoElement or empty string if it is null
     */
    public String getNameDescription() {
        return geoElement != null ? geoElement.getNameDescription() : "";
    }

    /**
     * @return the name of the geoElement or empty string if it is null
     */
    public String getName() {
        return geoElement != null ? geoElement.getLabelSimple() : "";
    }

    /**
     * @return the minimum value of the slider, default is -5
     */
    public double getSliderMin() {
        return geoElement != null ? ((GeoNumeric) geoElement).getIntervalMin() : -5;
    }

    /**
     * @param min
     *         minimum value for the slider
     */
    public void setSliderMin(String min) {
        if (geoElement == null) {
            return;
        }
        GeoNumberValue num = app.getKernel().getAlgebraProcessor().evaluateToNumeric(min, false);
        if (num == null) {
            return;
        }

        for (GeoElement geo : geoElementsList) {
			if (geo instanceof GeoNumeric) {
				((GeoNumeric) geo).setIntervalMin(num);
			}
        }

        app.setPropertiesOccured();
    }

    /**
     * @return the maximum value of the slider, default is 5
     */
    public double getSliderMax() {
        return geoElement != null ? ((GeoNumeric) geoElement).getIntervalMax() : 5;
    }

    /**
     * @param max
     *         maximum value for the slider
     */
    public void setSliderMax(String max) {
        if (geoElement == null) {
            return;
        }
        GeoNumberValue num = app.getKernel().getAlgebraProcessor().evaluateToNumeric(max, false);
        if (num == null) {
            return;
        }

        for (GeoElement geo : geoElementsList) {
            ((GeoNumeric) geo).setIntervalMax(num);
        }

        app.setPropertiesOccured();
    }

    /**
     * @return the step/increment value of the slider, default is 0.1
     */
    public double getSliderIncrement() {
        return geoElement != null ? geoElement.getAnimationStep() : 0.1;
    }

    /**
     * @param increment
     *         step value for the slider
     */
    public void setSliderIncrement(String increment) {
        if (geoElement == null) {
            return;
        }
        double step = app.getKernel().getAlgebraProcessor().evaluateToDouble(increment);
        boolean notDefined = Double.isNaN(step);

        for (GeoElement geo : geoElementsList) {
            geo.setAnimationStep(step);
            setSliderAutoStep(geo, notDefined);
        }

        app.setPropertiesOccured();
    }

    /**
     * @param geoElement
     *         a single geoElement
     * @param autoStep
     *         boolean, which defines whether the slider's autostep should be turned on or not
     */
    private void setSliderAutoStep(GeoElement geoElement, boolean autoStep) {
        if (geoElement instanceof GeoNumeric) {
            ((GeoNumeric) geoElement).setAutoStep(autoStep);
        }
    }

    /**
	 * @return the current alpha value of the geoElement
	 * 
	 *         (needs to be float for Android)
	 */
	public float getAlpha() {
		return (float) (geoElement != null ? geoElement.getAlphaValue() : 1);
    }

    /**
     * @param alpha
     *         alpha value to be set for the geoElement, it should be between 0 and 100
     */
    public void setAlpha(float alpha) {
        if (geoElement == null) {
            return;
        }

        EuclidianStyleBarStatic.applyColor(geoElement.getObjectColor(), alpha, app);

        app.setPropertiesOccured();
    }

    /**
     * @return whether the selected geoElements are all fillable or not
     */
    public boolean isFillable() {
        if (geoElement == null) {
            return false;
        }

        for (GeoElement geo : geoElementsList) {
            if (!geo.isFillable()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return whether the geoElement is shown or not
     */
    public boolean isObjectShown() {
        return geoElement != null && geoElement.isEuclidianVisible();
    }

    /**
     * @param show
     *         whether the selected geoElements should be shown or not
     */
    public void showObject(boolean show) {
        if (geoElement != null) {
            for (GeoElement geo : geoElementsList) {
                geo.setEuclidianVisible(show);
                geo.updateRepaint();
            }
            app.setPropertiesOccured();
        }
    }

    /**
     * @return whether all selected geoElements has further style or not
     */
    public boolean hasFurtherStyle() {
        if (geoElement == null) {
            return false;
        }

        for (GeoElement geo : geoElementsList) {
            if (!hasFurtherStyle(geo)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param geo
     *         a single geoElement
     * @return whether the geoElement has further style or not
     */
    private boolean hasFurtherStyle(GeoElement geo) {
        return !(geo instanceof GeoText || geo instanceof GeoInputBox);
    }

    /**
     * @return whether the tracing is turned on for the geoElement or not
     */
    public boolean isTraceOn() {
        return geoElement != null && geoElement.getTrace();
    }

    /**
     * @return whether all selected geoElements are traceable or not
     */
    public boolean isTraceable() {
        if (geoElement == null) {
            return false;
        }

        for (GeoElement geo : geoElementsList) {
            if (!geo.isTraceable()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param checked
     *         whether the tracing should be turned on for the selected geoElements or not
     */
    public void setTrace(boolean checked) {
        if (geoElement != null && geoElement.isTraceable()) {
            for (GeoElement geo : geoElementsList) {
                ((Traceable) geo).setTrace(checked);
            }
        }
    }

    /**
     * @return whether the selected geoElements are all sliders or not
     */
    public boolean hasSliderProperties() {
        for (GeoElement geo : geoElementsList) {
            if (!hasSliderProperties(geo)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param geo
     *         the GeoElement to check if it is a slider or not
     * @return if the passed geo if slider or not
     */
    private boolean hasSliderProperties(GeoElement geo) {
        return geo instanceof GeoNumeric
                && ((GeoNumeric) geo).getIntervalMinObject() != null
                && geo.isIndependent();
    }

    /**
     * @return true if all the selected geoElement is an instance of the PointProperties
     */
    public boolean hasPointProperties() {
        for (GeoElement geo : geoElementsList) {
            if (!PointStyleModel.match(geo)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return true if all the selected geoElement is a GeoFunction
     */
    public boolean hasFunctionProperties() {
        for (GeoElement geo : geoElementsList) {
            if (geo instanceof GeoList) {
                GeoElement elementForProperties = geo.getGeoElementForPropertiesDialog();
                if (!(elementForProperties instanceof GeoFunction)) {
                    return false;
                }
			} else if (!geo.isFunctionOrEquationFromUser()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return true if the all the selected geoElements have line properties
     */
    public boolean hasLineProperties() {
        for (GeoElement geo : geoElementsList) {
            if (!geo.showLineProperties()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return with the selected geoElement
     */
    public GeoElement getGeoElement() {
        return geoElement;
    }

    /**
     * @param geo
     *         initialize geoElement
     */
    public void setGeoElement(GeoElement geo) {
        geoElement = geo;
    }

    /**
     * @return with the the geoElementsList which are the selected geos
     */
    public List<GeoElement> getGeoElementsList() {
        return geoElementsList;
    }

    /**
     * @param geoList
     *         initialize geoElementsList
     */
    public void setGeoElementsList(ArrayList<GeoElement> geoList) {
        geoElementsList = geoList;
    }

    /**
     * Delete the selected geoElements
     */
    public void deleteGeoElements() {
        if (geoElement == null) {
            return;
        }
        app.deleteSelectedObjects(false);
    }

    /**
     * @return the localized type string if one item is selected, anyway "Selection"
     */
    public String getTranslatedTypeString() {
        if (geoElement == null) {
            return "";
        }

        if (geoElementsList.size() > 1) {
            return app.getLocalization().getMenu("Selection");
        }
        return geoElementsList.get(0).translatedTypeString();
    }

    /**
     * @return the label of the geoElement if only one geoElement is selected
     */
    public String getLabel() {
        if (geoElement == null || geoElementsList.size() > 1) {
            return null;
        }

        return geoElement.isAlgebraLabelVisible() ? geoElementsList.get(0).getLabelSimple() : "";
    }

    static public void removeNoPropertiesGeoFromList(App app, List<GeoElement> fromList, List<GeoElement> toList) {
        Construction cons = app.getKernel().getConstruction();
        toList.clear();
        for (GeoElement geo : fromList) {
            if (cons.isConstantElement(geo) == Construction.Constants.NOT
                    && !(geo instanceof GeoBoolean || geo instanceof GeoButton)) {
                toList.add(geo);
            }
        }
    }

    public boolean hasFixUnfixFunctionProperty() {
        return app.getActiveEuclidianView().canMoveFunctions()
                && !app.getConfig().isObjectDraggingRestricted();
    }

    public boolean hasPointStyleProperty() {
        return app.getActiveEuclidianView().canShowPointStyle();
    }

    /**
     * Returns the toString mode of the elements, or -1
     * if there are no elements, or if the mode is not the same.
     *
     * @return toStringMode or -1.
     */
    public int getToStringMode() {
        if (geoElement == null || geoElementsList == null) {
            return -1;
        }
        int mode = geoElement.getToStringMode();
        boolean same = true;
        for (int i = 0; i < geoElementsList.size(); i++) {
            GeoElement element = geoElementsList.get(i);
            same = same && element.getToStringMode() == mode;
        }

        return same ? mode : -1;
    }

    /**
     * Returns the label of the toStringMode.
     *
     * @param mode mode
     * @return label
     */
    public String getModeLabel(int mode) {
        switch (mode) {
            case GeoLine.EQUATION_EXPLICIT:
                return "ExplicitLineEquation";
            case GeoLine.EQUATION_GENERAL:
                return "GeneralLineEquation";
            case GeoLine.EQUATION_IMPLICIT:
                return "ImplicitLineEquation";
            case GeoLine.EQUATION_USER:
                return "InputForm";
            case GeoLine.PARAMETRIC:
                return "ParametricForm";
            default:
                return "";
        }
    }

    /**
     * Sets the toString mode of the elements if they are
     * of instance {@link GeoVec3D}.
     *
     * @param mode toStringMode
     */
    public void setToStringMode(int mode) {
        if (geoElementsList == null) {
            return;
        }
        for (int i = 0; i < geoElementsList.size(); i++) {
            GeoElement element = geoElementsList.get(i);
            if (element instanceof GeoVec3D) {
                GeoVec3D vec3d = (GeoVec3D) element;
                vec3d.setMode(mode);
                vec3d.updateRepaint();
            }
        }
    }

    /**
     * Returns true, if the equation mode setting
     * should be shown.
     *
     * @return true if setting should be shown.
     */
    public boolean hasEquationModeSetting() {
        if (geoElementsList == null) {
            return false;
        }
        boolean show = geoElementsList.size() > 0;
        for (int i = 0; i < geoElementsList.size(); i++) {
            GeoElement element = geoElementsList.get(i);
            boolean isEnforcedLineEquationForm = element instanceof GeoLine
                    && app.getConfig().getEnforcedLineEquationForm() != -1;
            boolean isEnforcedConicEquationForm = element instanceof GeoConicND
                    && app.getConfig().getEnforcedConicEquationForm() != -1;
            boolean isEnforcedEquationForm =
                    isEnforcedLineEquationForm || isEnforcedConicEquationForm;
            show = show && !isEnforcedEquationForm;
            show = show && element instanceof GeoLine && !element.isNumberValue();
            show = show && element.getDefinition() == null;
        }
        return show;
    }

    /**
     * @return whether the geoElement is shown in algebra view or not
     */
    public boolean isShownInAlgebraView() {
        return geoElement != null && !geoElement.isAuxiliaryObject();
    }

    /**
     * set the geoElement to be shown in algebra view or not
     *
     * @param flag flag
     */
    public void setShowInAlgebraView(boolean flag) {
        if (geoElement != null) {
            for (GeoElement geo : geoElementsList) {
                geo.setAuxiliaryObject(!flag);
                geo.updateRepaint();
            }
            app.setPropertiesOccured();
            app.updateGuiForShowAuxiliaryObjects();
        }
    }

    /**
     * @return slope size property for selected geos
     */
    public SlopeSizeProperty getSlopeSizeProperty() {
        if (slopeSizeProperty == null) {
            slopeSizeProperty = new SlopeSizeProperty(app);
        }
        List<GeoElementND> list = new ArrayList<>();
        list.addAll(geoElementsList);
        slopeSizeProperty.setGeoElements(list);
        return slopeSizeProperty;
    }
}
