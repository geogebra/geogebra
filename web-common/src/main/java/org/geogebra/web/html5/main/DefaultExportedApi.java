package org.geogebra.web.html5.main;

import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.web.html5.util.JsRunnable;
import org.geogebra.web.html5.util.StringConsumer;
import org.gwtproject.dom.client.Element;

import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Maps GeoGebra functions to exported JS api
 */
@JsType
@SuppressWarnings("Javadoc")
public class DefaultExportedApi implements ExportedApi {

	private GgbAPIW ggbAPI;
	private ScriptManagerW scriptManager;

	@JsIgnore
	@Override
	public void setGgbAPI(GgbAPIW ggbAPI) {
		this.ggbAPI = ggbAPI;
	}

	@JsIgnore
	@Override
	public void setScriptManager(ScriptManagerW scriptManager) {
		this.scriptManager = scriptManager;
	}

	private static double doubleOrDefault(Object o, double def) {
		return Js.isFalsy(o) ? def : Js.coerceToDouble(o);
	}

	public void remove() {
		getGgbAPI().removeApplet();
		if (scriptManager != null) {
			scriptManager.export(null);
		}
		setGgbAPI(null);
		scriptManager = null;
	}

	public String getXML(@TS(TS.OPTIONAL_STRING) String objName) {
		if (Js.isTruthy(objName)) {
			return getGgbAPI().getXML(objName + "");
		} else {
			return getGgbAPI().getXML();
		}
	}

	public String getAlgorithmXML(String objName) {
		return getGgbAPI().getAlgorithmXML(objName + "");
	}

	public String getPerspectiveXML() {
		return getGgbAPI().getPerspectiveXML();
	}

	public String getBase64(Object param1, Object param2) {
		if (Js.isTripleEqual(param2, false)) {
			return getGgbAPI().getBase64(false);
		}
		if (Js.isTripleEqual(param2, true)) {
			return getGgbAPI().getBase64(true);
		}
		if (JsEval.isFunction(param2)) {
			getGgbAPI().getBase64(Js.isTruthy(param1), (StringConsumer) param2);
		} else if (JsEval.isFunction(param1)) {
			getGgbAPI().getBase64(false, (StringConsumer) param1);
		} else {
			return getGgbAPI().getBase64();
		}

		return null;
	}

	public void setBase64(String base64string, JsRunnable callback) {
		getGgbAPI().setBase64(base64string + "", callback);
	}

	public void openFile(String filename, JsRunnable callback) {
		getGgbAPI().openFile(filename + "", callback);
	}

	public void login(String token, Object ui) {
		getGgbAPI().login(token  + "", Js.isTruthy(ui));
	}

	public void logout() {
		getGgbAPI().logout();
	}

	public void setXML(String xml) {
		getGgbAPI().setXML(xml + "");
	}

	public void evalXML(String xmlString) {
		getGgbAPI().evalXML(xmlString + "");
		ggbAPI.getApplication().getActiveEuclidianView().invalidateDrawableList();
	}

	public void setDisplayStyle(String objName, String style) {
		getGgbAPI().setDisplayStyle(objName + "", style + "");
	}

	public boolean evalCommand(String cmdString) {
		return getGgbAPI().evalCommandNoException(cmdString + "");
	}

	public String evalCommandGetLabels(String cmdString) {
		return getGgbAPI().evalCommandGetLabelsNoException(cmdString + "");
	}

	public Promise<String> asyncEvalCommand(String cmdString) {
		return new Promise<>((resolve, reject) -> {
			getGgbAPI().asyncEvalCommand(cmdString + "", resolve, reject);
		});
	}

	public Promise<String> asyncEvalCommandGetLabels(String cmdString) {
		return new Promise<>((resolve, reject) -> {
			getGgbAPI().asyncEvalCommandGetLabels(cmdString + "", resolve, reject);
		});
	}

	public String evalCommandCAS(String cmdString, String rounding) {
		return getGgbAPI().evalCommandCAS(cmdString + "",
				Js.isTruthy(rounding) ? rounding : null);
	}

	public String evalGeoGebraCAS(String cmdString) {
		return getGgbAPI().evalGeoGebraCAS(cmdString + "");
	}

	public void setFixed(String objName, Object flag, Object selection) {
		if (JsEval.isUndefined(selection)) {
			getGgbAPI().setFixed(objName + "", Js.isTruthy(flag));
		} else {
			getGgbAPI().setFixed(objName + "", Js.isTruthy(flag), Js.isTruthy(selection));
		}
	}

	public boolean isFixed(String objName) {
		return getGgbAPI().isFixed(objName + "");
	}

	public boolean isSelectionAllowed(String objName) {
		return getGgbAPI().isSelectionAllowed(objName + "");
	}

	public void setOnTheFlyPointCreationActive(Object flag) {
		getGgbAPI().setOnTheFlyPointCreationActive(Js.isTruthy(flag));
	}

	public void setUndoPoint() {
		getGgbAPI().setUndoPoint();
	}

	public void setSaved(Object saved) {
		// setSaved() is a shortcut for setSaved(true) for compatibility, use safe!==false
		getGgbAPI().setSaved(!Js.isTripleEqual(saved, false));
	}

	public boolean isSaved() {
		return getGgbAPI().isSaved();
	}

	public void startSaveCallback(String title, String visibility, String callbackAction) {
		getGgbAPI().startSaveCallback(title, visibility, callbackAction);
	}

	public void initCAS() {
		getGgbAPI().initCAS();
	}

	public void setErrorDialogsActive(Object flag) {
		getGgbAPI().setErrorDialogsActive(Js.isTruthy(flag));
	}

	// TODO: implement this in Desktop and Web
	public void reset() {
		getGgbAPI().reset();
	}

	public void refreshViews() {
		getGgbAPI().refreshViews();
	}

	public void setVisible(String objName, Object visible) {
		getGgbAPI().setVisible(objName + "", Js.isTruthy(visible));
	}

	public boolean getVisible(String objName, Object view) {
		if (!JsEval.isUndefined(view)) {
			return getGgbAPI().getVisible(objName + "", Js.coerceToInt(view));
		}
		return getGgbAPI().getVisible(objName + "");
	}

	public void setLayer(String objName, int layer) {
		getGgbAPI().setLayer(objName + "", layer);
	}

	public int getLayer(String objName) {
		return getGgbAPI().getLayer(objName + "");
	}

	public void setLayerVisible(int layer, Object visible) {
		getGgbAPI().setLayerVisible(layer, Js.isTruthy(visible));
	}

	public void setTrace(String objName, Object flag) {
		getGgbAPI().setTrace(objName + "", Js.isTruthy(flag));
	}

	public boolean isTracing(String objName) {
		return getGgbAPI().isTracing(objName + "");
	}

	public void setLabelVisible(String objName, Object visible) {
		getGgbAPI().setLabelVisible(objName + "", Js.isTruthy(visible));
	}

	public void setLabelStyle(String objName, int style) {
		getGgbAPI().setLabelStyle(objName + "", style);
	}

	public int getLabelStyle(String objName) {
		return getGgbAPI().getLabelStyle(objName + "");
	}

	public boolean getLabelVisible(String objName) {
		return getGgbAPI().getLabelVisible(objName + "");
	}

	public void setColor(String objName, int red, int green, int blue) {
		getGgbAPI().setColor(objName + "", red, green, blue);
	}

	public void setCorner(String objName, double x, double y, Object index) {
		getGgbAPI().setCorner(objName + "", x, y, Js.isFalsy(index) ? 1 : Js.coerceToInt(index));
	}

	public void setLineStyle(String objName, int style) {
		getGgbAPI().setLineStyle(objName + "", style);
	}

	public void setLineThickness(String objName, int thickness) {
		getGgbAPI().setLineThickness(objName + "", thickness);
	}

	public void setPointStyle(String objName, int style) {
		getGgbAPI().setPointStyle(objName + "", style);
	}

	public void setPointSize(String objName, int style) {
		getGgbAPI().setPointSize(objName + "", style);
	}

	public void setFilling(String objName, double filling) {
		getGgbAPI().setFilling(objName + "", filling);
	}

	public String getColor(String objName) {
		return getGgbAPI().getColor(objName + "");
	}

	public String getPenColor() {
		return getGgbAPI().getPenColor();
	}

	public int getPenSize() {
		return getGgbAPI().getPenSize();
	}

	public void setPenSize(int size) {
		getGgbAPI().setPenSize(size);
	}

	public void setPenColor(int red, int green, int blue) {
		getGgbAPI().setPenColor(red, green, blue);
	}

	public double getFilling(String objName) {
		return getGgbAPI().getFilling(objName + "");
	}

	public String getImageFileName(String objName) {
		return getGgbAPI().getImageFileName(objName + "");
	}

	public int getLineStyle(String objName) {
		return getGgbAPI().getLineStyle(objName + "");
	}

	public int getLineThickness(String objName) {
		return getGgbAPI().getLineThickness(objName + "");
	}

	public int getPointStyle(String objName) {
		return getGgbAPI().getPointStyle(objName + "");
	}

	public int getPointSize(String objName) {
		return getGgbAPI().getPointSize(objName + "");
	}

	public void deleteObject(String objName) {
		getGgbAPI().deleteObject(objName + "");
	}

	public void setAnimating(String objName, Object animate) {
		getGgbAPI().setAnimating(objName + "", Js.isTruthy(animate));
	}

	public void setAnimationSpeed(String objName, double speed) {
		getGgbAPI().setAnimationSpeed(objName + "", speed);
	}

	public void startAnimation() {
		getGgbAPI().startAnimation();
	}

	public void stopAnimation() {
		getGgbAPI().stopAnimation();
	}

	public void setAuxiliary(String objName, Object auxiliary) {
		getGgbAPI().setAuxiliary(objName + "", Js.isTruthy(auxiliary));
	}

	public void hideCursorWhenDragging(Object hideCursorWhenDragging) {
		getGgbAPI().hideCursorWhenDragging(Js.isTruthy(hideCursorWhenDragging));
	}

	public boolean isAnimationRunning() {
		return getGgbAPI().isAnimationRunning();
	}

	public boolean isAnimating(String objName) {
		return getGgbAPI().isAnimating(objName);
	}

	public double getFrameRate() {
		return getGgbAPI().getFrameRate();
	}

	public boolean renameObject(String oldName, String newName,
			@TS(TS.OPTIONAL_BOOL) Object force) {
		return getGgbAPI().renameObject(oldName + "", newName + "", Js.isTruthy(force));
	}

	public boolean exists(String objName) {
		return getGgbAPI().exists(objName + "");
	}

	public boolean isDefined(String objName) {
		return getGgbAPI().isDefined(objName + "");
	}

	public String getValueString(String objName, @TS(TS.OPTIONAL_BOOL) Object localized) {
		boolean localizedB = JsEval.isUndefined(localized) || Js.isTruthy(localized);
		return getGgbAPI().getValueString(objName + "", localizedB);
	}

	public double getListValue(String objName, int index) {
		return getGgbAPI().getListValue(objName + "", index);
	}

	public String getDefinitionString(String objName, @TS(TS.OPTIONAL_BOOL) Object localized) {
		boolean localizedB = JsEval.isUndefined(localized) || Js.isTruthy(localized);
		return getGgbAPI().getDefinitionString(objName + "",  localizedB);
	}

	public String getLaTeXString(String objName) {
		return getGgbAPI().getLaTeXString(objName + "");
	}

	public String getLaTeXBase64(String objName, @TS(TS.OPTIONAL_BOOL) Object value) {
		return getGgbAPI().getLaTeXBase64(objName + "", Js.isTruthy(value));
	}

	public String getCommandString(String objName, @TS(TS.OPTIONAL_BOOL) Object localized) {
		boolean localizedB = JsEval.isUndefined(localized) || Js.isTruthy(localized);
		return getGgbAPI().getCommandString(objName + "", localizedB);
	}

	public String getCaption(String objName, Object subst) {
		return getGgbAPI().getCaption(objName + "", Js.isTruthy(subst));
	}

	public void setCaption(String objName, String caption) {
		getGgbAPI().setCaption(objName + "", caption + "");
	}

	public double getXcoord(String objName) {
		return getGgbAPI().getXcoord(objName + "");
	}

	public double getYcoord(String objName) {
		return getGgbAPI().getYcoord(objName + "");
	}

	public double getZcoord(String objName) {
		return getGgbAPI().getZcoord(objName + "");
	}

	public void setCoords(String objName, double... coords) {
		if (coords.length == 2) {
			getGgbAPI().setCoords(objName + "", coords[0], coords[1]);
		} else {
			getGgbAPI().setCoords(objName + "", coords);
		}
	}

	public double getValue(String objName) {
		return getGgbAPI().getValue(objName + "");
	}

	public String getVersion() {
		return getGgbAPI().getVersion();
	}

	public void getScreenshotBase64(StringConsumer callback, Object scaleObject) {
		double scale = 1;
		if (Js.isTruthy(scaleObject)) {
			scale = Js.asDouble(scaleObject);
		}
		getGgbAPI().getScreenshotBase64(callback, scale);
	}

	public String getThumbnailBase64() {
		return getGgbAPI().getThumbnailBase64();
	}

	public void setValue(String objName, Object x) {
		// #4035
		// need to support possible syntax error
		// eg setValue("a","3") rather than setValue("a",3)

		double value;
		if ("true".equals(x)) {
			value = 1;
		} else if ("false".equals(x)) {
			value = 0;
		} else {
			// force string -> number (might give NaN)
			value = Js.coerceToDouble(x);
		}

		getGgbAPI().setValue(objName + "", value);
	}

	public void setTextValue(String objName, String x) {
		getGgbAPI().setTextValue(objName + "", x + "");
	}

	public void setListValue(String objName, Object x, Object y) {
		// #4035
		// need to support possible syntax error
		double xValue;
		if ("true".equals(x)) {
			xValue = 1;
		} else if ("false".equals(x)) {
			xValue = 0;
		} else {
			// force string -> number (might give NaN)
			xValue = Js.coerceToDouble(x);
		}

		double yValue;
		if ("true".equals(y)) {
			yValue = 1;
		} else if ("false".equals(y)) {
			yValue = 0;
		} else {
			// force string -> number (might give NaN)
			yValue = Js.coerceToDouble(y);
		}

		getGgbAPI().setListValue(objName + "", xValue, yValue);
	}

	public void setRepaintingActive(Object flag) {
		getGgbAPI().setRepaintingActive(Js.isTruthy(flag));
	}

	public void setCoordSystem(double xmin, double xmax, double ymin, double ymax, Object zmin,
			Object zmax, Object verticalY) {
		if (!"number".equals(Js.typeof(zmin))) {
			getGgbAPI().setCoordSystem(xmin, xmax, ymin, ymax);
		} else {
			getGgbAPI().setCoordSystem(xmin, xmax, ymin, ymax, Js.coerceToDouble(zmin),
					Js.coerceToDouble(zmax), Js.isTruthy(verticalY));
		}
	}

	public void setAxesVisible(Object arg1, Object arg2, Object arg3, Object arg4) {
		if (JsEval.isUndefined(arg3)) {
			getGgbAPI().setAxesVisible(Js.isTruthy(arg1), Js.isTruthy(arg2));
		} else {
			getGgbAPI().setAxesVisible(Js.coerceToInt(arg1), Js.isTruthy(arg2), Js.isTruthy(arg3),
					Js.isTruthy(arg4));
		}
	}

	public void setAxisUnits(int arg1, String arg2, String arg3, String arg4) {
		getGgbAPI().setAxisUnits(arg1, arg2 + "", arg3 + "", arg4 + "");
	}

	public void setAxisLabels(int arg1, String arg2, String arg3, String arg4) {
		getGgbAPI().setAxisLabels(arg1, arg2 + "", arg3 + "", arg4 + "");
	}

	public void setAxisSteps(int arg1, String arg2, String arg3, String arg4) {
		getGgbAPI().setAxisSteps(arg1, arg2 + "", arg3 + "", arg4 + "");
	}

	public JsArray<String> getAxisUnits(Object arg1) {
		return new JsArray<>(getGgbAPI().getAxisUnits(Js.coerceToInt(arg1)));
	}

	public JsArray<String> getAxisLabels(Object arg1) {
		return new JsArray<>(getGgbAPI().getAxisLabels(Js.coerceToInt(arg1)));
	}

	public void setPointCapture(int view, Object capture) {
		if (JsEval.isUndefined(capture)) {
			getGgbAPI().setPointCapture(1, view);
		} else {
			getGgbAPI().setPointCapture(view, Js.coerceToInt(capture));
		}
	}

	public boolean getGridVisible(Object view) {
		if (JsEval.isUndefined(view)) {
			return getGgbAPI().getGridVisible(1);
		} else {
			return getGgbAPI().getGridVisible(Js.coerceToInt(view));
		}
	}

	public void setGridVisible(Object arg1, Object arg2) {
		if (JsEval.isUndefined(arg2)) {
			getGgbAPI().setGridVisible(Js.isTruthy(arg1));
		} else {
			getGgbAPI().setGridVisible(Js.coerceToInt(arg1), Js.isTruthy(arg2));
		}
	}

	public JsArray<String> getAllObjectNames(@TS(TS.OPTIONAL_STRING) String objectType) {
		if (JsEval.isUndefined(objectType)) {
			return new JsArray<>(getGgbAPI().getAllObjectNames());
		} else {
			return new JsArray<>(getGgbAPI().getAllObjectNames(objectType + ""));
		}
	}

	public int getObjectNumber() {
		return getGgbAPI().getObjectNumber();
	}

	public String getObjectName(int i) {
		return getGgbAPI().getObjectName(i);
	}

	public String getObjectType(String objName) {
		return getGgbAPI().getObjectType(objName + "");
	}

	public void setMode(int mode) {
		getGgbAPI().setMode(mode);
	}

	public int getMode() {
		return getGgbAPI().getMode();
	}

	public String getToolName(int i) {
		return getGgbAPI().getToolName(i);
	}

	public void openMaterial(String material) {
		getGgbAPI().openMaterial(material + "");
	}

	public void undo() {
		getGgbAPI().undo();
	}

	public void redo() {
		getGgbAPI().redo();
	}

	public void newConstruction() {
		getGgbAPI().newConstruction();
	}

	public void resetAfterSaveLoginCallbacks() {
		getGgbAPI().resetAfterSaveLoginCallbacks();
	}

	public void debug(String str) {
		getGgbAPI().debug(str + "");
	}

	public void setWidth(int width) {
		getGgbAPI().setWidth(width);
	}

	public void setHeight(int height) {
		getGgbAPI().setHeight(height);
	}

	public void setSize(int width, int height) {
		getGgbAPI().setSize(width, height);
	}

	public void enableRightClick(Object enable) {
		getGgbAPI().enableRightClick(Js.isTruthy(enable));
	}

	public void enableLabelDrags(Object enable) {
		getGgbAPI().enableLabelDrags(Js.isTruthy(enable));
	}

	public void enableShiftDragZoom(Object enable) {
		getGgbAPI().enableShiftDragZoom(Js.isTruthy(enable));
	}

	public void showToolBar(Object show) {
		getGgbAPI().showToolBar(Js.isTruthy(show));
	}

	public void setCustomToolBar(String toolbarDef) {
		getGgbAPI().setCustomToolBar(toolbarDef + "");
	}

	public void showMenuBar(Object show) {
		getGgbAPI().showMenuBar(Js.isTruthy(show));
	}

	public void showAlgebraInput(Object show) {
		getGgbAPI().showAlgebraInput(Js.isTruthy(show));
	}

	public void showResetIcon(Object show) {
		getGgbAPI().showResetIcon(Js.isTruthy(show));
	}

	public String getViewProperties(int view) {
		return getGgbAPI().getViewProperties(view);
	}

	public void setFont(String label, int size, Object bold, Object italic, Object serif) {
		getGgbAPI().setFont(label + "", size, Js.isTruthy(bold), Js.isTruthy(italic),
				Js.isTruthy(serif));
	}

	public String insertImage(String url, String corner1, String corner2, String corner4) {
		return getGgbAPI().insertImage(url + "", corner1 + "", corner2 + "", corner4 + "");
	}

	public void addImage(String fileName, String urlOrSvgContent) {
		getGgbAPI().addImage(fileName + "", urlOrSvgContent + "");
	}

	public void recalculateEnvironments() {
		getGgbAPI().recalculateEnvironments();
	}

	public boolean isIndependent(String label) {
		return getGgbAPI().isIndependent(label + "");
	}

	public boolean isMoveable(String label) {
		return getGgbAPI().isMoveable(label + "");
	}

	public void setPerspective(String code) {
		getGgbAPI().setPerspective(code + "");
	}

	public void enableCAS(Object enable) {
		getGgbAPI().enableCAS(Js.isTruthy(enable));
	}

	public void enable3D(Object enable) {
		getGgbAPI().enable3D(Js.isTruthy(enable));
	}

	public String getPNGBase64(double exportScale, Object transparent, double dpi,
			Object copyToClipboard, Object greyscale) {
		return getGgbAPI().getPNGBase64(exportScale, Js.isTruthy(transparent), dpi,
				Js.isTruthy(copyToClipboard), Js.isTruthy(greyscale));
	}

	public void exportGIF(String sliderLabel, double scale, double timeBetweenFrames,
			Object isLoop, String filename, Object rotate) {
		getGgbAPI().exportGIF(sliderLabel, scale, timeBetweenFrames, Js.isTruthy(isLoop),
				filename, Js.coerceToInt(rotate));
	}

	public Object getFileJSON(Object thumbnail) {
		return getGgbAPI().getFileJSON(Js.isTruthy(thumbnail));
	}

	public void setFileJSON(Object zip) {
		getGgbAPI().setFileJSON(zip);
	}

	public void setLanguage(String lang) {
		getGgbAPI().setLanguage(lang);
	}

	public void showTooltip(Object tooltip) {
		getGgbAPI().showTooltip(tooltip + "");
	}

	public void addMultiuserSelection(String clientId, String user, String color, String label, boolean implicit) {
		getGgbAPI().addMultiuserSelection(clientId, user, color, label, implicit);
	}

	public void removeMultiuserSelections(String clientId) {
		getGgbAPI().removeMultiuserSelections(clientId);
	}

	public void setLabelSuffixForMultiuser(int labelPrefixIndex) {
		getGgbAPI().setLabelSuffixForMultiuser(labelPrefixIndex);
	}

	// APPS-646 deprecated, needs changing to getValue("correct")
	public double getExerciseFraction() {
		return getGgbAPI().getExerciseFraction();
	}

	// APPS-646 Exercises no longer supported
	public boolean isExercise() {
		return false;
	}


	public void checkSaved(JsRunnable path) {
		getGgbAPI().checkSaved(path);
	}

	public int getCASObjectNumber() {
		return getGgbAPI().getCASObjectNumber();
	}

	public boolean writePNGtoFile(String filename, double exportScale, Object transparent,
			double DPI, Object greyscale) {
		return getGgbAPI().writePNGtoFile(filename + "", exportScale, Js.isTruthy(transparent),
				DPI, Js.isTruthy(greyscale));
	}

	public void exportPGF(StringConsumer callback) {
		getGgbAPI().exportPGF(callback);
	}

	public void exportSVG(Object filenameOrCallback) {
		if ("string".equals(Js.typeof(filenameOrCallback))) {
			getGgbAPI().exportSVG((String) filenameOrCallback, null);
		} else if ("function".equals(Js.typeof(filenameOrCallback))) {
			getGgbAPI().exportSVG(null, ((StringConsumer) filenameOrCallback)::consume);
		} else {
			DomGlobal.console.warn("exportSVG requires either a filename or a callback.");
		}
	}

	public void exportPDF(Object scale, Object filenameOrCallback, String sliderLabel) {
		if ("string".equals(Js.typeof(filenameOrCallback))) {
			getGgbAPI().exportPDF(Js.coerceToDouble(scale), (String) filenameOrCallback,
					null, sliderLabel);
		} else if ("function".equals(Js.typeof(filenameOrCallback))) {
			getGgbAPI().exportPDF(Js.coerceToDouble(scale), null,
					((StringConsumer) filenameOrCallback)::consume, sliderLabel);
		} else {
			DomGlobal.console.warn("exportPDF requires either a filename or "
					+ "a callback as the second parameter.");
		}
	}

	public void exportPSTricks(StringConsumer callback) {
		getGgbAPI().exportPSTricks(callback);
	}

	public void exportAsymptote(StringConsumer callback) {
		getGgbAPI().exportAsymptote(callback);
	}

	public void setRounding(String digits) {
		getGgbAPI().setRounding(digits + "");
	}

	public String getRounding() {
		return getGgbAPI().getRounding();
	}

	public void copyTextToClipboard(String text) {
		getGgbAPI().copyTextToClipboard(text + "");
	}

	public void evalLaTeX(String text, int mode) {
		getGgbAPI().evalLaTeX(text + "", mode);
	}

	public boolean evalMathML(String text) {
		return getGgbAPI().evalMathML(text + "");
	}

	public String getScreenReaderOutput(String text) {
		return getGgbAPI().getScreenReaderOutput(text + "");
	}

	public String getEditorState() {
		return getGgbAPI().getEditorState();
	}

	public void setEditorState(Object state, String label) {
		String stateString = JsEval.isJSString(state) ? Js.asString(state)
				: Global.JSON.stringify(state);
		getGgbAPI().setEditorState(stateString, label);
	}

	public String getInputBoxState(String label) {
		return getGgbAPI().getInputBoxState(label);
	}

	public void setInputBoxState(String state, String label) {
		getGgbAPI().setInputBoxState(state, label);
	}

	public boolean isInteractive(String label) {
		return getGgbAPI().isInteractive(label);
	}

	public String exportCollada(Object xmin, Object xmax, Object ymin, Object ymax, Object zmin,
			Object zmax, Object xyScale, Object xzScale, Object xTickDistance,
			Object yTickDistance, Object zTickDistance) {
		return getGgbAPI().exportCollada(doubleOrDefault(xmin, -5), doubleOrDefault(xmax, 5),
				doubleOrDefault(ymin, -5), doubleOrDefault(ymax, 5),
				doubleOrDefault(zmin, -5), doubleOrDefault(zmax, 5),
				doubleOrDefault(xyScale, 1), doubleOrDefault(xzScale, 1),
				doubleOrDefault(xTickDistance, -1), doubleOrDefault(yTickDistance, -1),
				doubleOrDefault(zTickDistance, -1));
	}

	public String exportSimple3d(String name, double xmin, double xmax, double ymin, double ymax,
			double zmin, double zmax, double xyScale, double xzScale, double xTickDistance,
			double yTickDistance, double zTickDistance) {
		return getGgbAPI().exportSimple3d(
			name + "", xmin, xmax, ymin, ymax, zmin, zmax, xyScale,
			xzScale, xTickDistance, yTickDistance, zTickDistance);
	}

	public String translate(String arg1, StringConsumer callback) {
		return getGgbAPI().translate(arg1 + "", callback);
	}

	public String exportConstruction(String[] flags) {
		if (Js.isTruthy(flags)) {
			return getGgbAPI().exportConstruction(flags);
		} else {
			return getGgbAPI().exportConstruction("color", "name", "definition", "value");
		}
	}

	public void updateConstruction() {
		getGgbAPI().updateConstruction();
	}

	public double getConstructionSteps(Object breakpoints) {
		return getGgbAPI().getConstructionSteps(Js.isTruthy(breakpoints));
	}

	public void setConstructionStep(double n, Object breakpoints) {
		getGgbAPI().setConstructionStep(n, Js.isTruthy(breakpoints));
	}

	public void previousConstructionStep() {
		getGgbAPI().previousConstructionStep();
	}

	public void nextConstructionStep() {
		getGgbAPI().nextConstructionStep();
	}

	public JsPropertyMap<Object> getEmbeddedCalculators(
			@TS(TS.OPTIONAL_BOOL) Object includeGraspableMath) {
		return getGgbAPI().getEmbeddedCalculators(Js.isTruthy(includeGraspableMath));
	}

	public Element getFrame() {
		return getGgbAPI().getFrame();
	}

	public void enableFpsMeasurement() {
		getGgbAPI().enableFpsMeasurement();
	}

	public void disableFpsMeasurement() {
		getGgbAPI().disableFpsMeasurement();
	}

	public void testDraw() {
		getGgbAPI().testDraw();
	}

	public void startDrawRecording() {
		getGgbAPI().startDrawRecording();
	}

	public void endDrawRecordingAndLogResults() {
		getGgbAPI().endDrawRecordingAndLogResults();
	}

	public void registerAddListener(@TS(TS.OBJECT_LISTENER) Object JSFunctionName) {
		getGgbAPI().registerAddListener(JSFunctionName);
	}

	public void unregisterAddListener(@TS(TS.OBJECT_LISTENER) Object JSFunctionName) {
		getGgbAPI().unregisterAddListener(JSFunctionName);
	}

	public void registerStoreUndoListener(@TS(TS.OBJECT_LISTENER) Object JSFunctionName) {
		getGgbAPI().registerStoreUndoListener(JSFunctionName);
	}

	public void unregisterStoreUndoListener(@TS(TS.VOID_FUNCTION) Object JSFunctionName) {
		getGgbAPI().unregisterStoreUndoListener(JSFunctionName);
	}

	public void registerRemoveListener(@TS(TS.OBJECT_LISTENER) Object JSFunctionName) {
		getGgbAPI().registerRemoveListener(JSFunctionName);
	}

	public void unregisterRemoveListener(@TS(TS.OBJECT_LISTENER) Object JSFunctionName) {
		getGgbAPI().unregisterRemoveListener(JSFunctionName);
	}

	public void registerClearListener(@TS(TS.VOID_FUNCTION) Object JSFunctionName) {
		getGgbAPI().registerClearListener(JSFunctionName);
	}

	public void unregisterClearListener(@TS(TS.VOID_FUNCTION) Object JSFunctionName) {
		getGgbAPI().unregisterClearListener(JSFunctionName);
	}

	public void registerRenameListener(Object JSFunctionName) {
		getGgbAPI().registerRenameListener(JSFunctionName);
	}

	public void unregisterRenameListener(Object JSFunctionName) {
		getGgbAPI().unregisterRenameListener(JSFunctionName);
	}

	public void registerUpdateListener(@TS(TS.OBJECT_LISTENER) Object JSFunctionName) {
		getGgbAPI().registerUpdateListener(JSFunctionName);
	}

	public void unregisterUpdateListener(@TS(TS.OBJECT_LISTENER) Object JSFunctionName) {
		getGgbAPI().unregisterUpdateListener(JSFunctionName);
	}

	public void registerClientListener(@TS(TS.CLIENT_LISTENER) Object JSFunctionName) {
		getGgbAPI().registerClientListener(JSFunctionName);
	}

	public void unregisterClientListener(@TS(TS.CLIENT_LISTENER) Object JSFunctionName) {
		getGgbAPI().unregisterClientListener(JSFunctionName);
	}

	public void registerObjectUpdateListener(String objName,
			@TS(TS.OBJECT_LISTENER) Object JSFunctionName) {
		getGgbAPI().registerObjectUpdateListener(objName + "", JSFunctionName);
	}

	public void unregisterObjectUpdateListener(String label) {
		getGgbAPI().unregisterObjectUpdateListener(label);
	}

	public void registerObjectClickListener(String objName,
			@TS(TS.OBJECT_LISTENER) Object JSFunctionName) {
		getGgbAPI().registerObjectClickListener(objName + "", JSFunctionName);
	}

	public void unregisterObjectClickListener(String objName) {
		getGgbAPI().unregisterObjectClickListener(objName + "");
	}

	public void registerClickListener(@TS(TS.OBJECT_LISTENER) Object JSFunctionName) {
		getGgbAPI().registerClickListener(JSFunctionName);
	}

	public void unregisterClickListener(@TS(TS.OBJECT_LISTENER) Object JSFunctionName) {
		getGgbAPI().unregisterClickListener(JSFunctionName);
	}

	public void handlePageAction(Object eventType, Object pageIdx, Object appState) {
		getGgbAPI().handlePageAction(eventType + "", pageIdx + "", appState);
	}

	public void selectPage(Object pageIdx) {
		getGgbAPI().selectPage(pageIdx + "");
	}

	public String getActivePage() {
		return getGgbAPI().getActivePage();
	}

	public JsArray<String> getPages() {
		return new JsArray<>(getGgbAPI().getPages());
	}

	public PageContent getPageContent(String pageId) {
		return getGgbAPI().getPageContent(pageId);
	}

	public void setPageContent(String pageId, PageContent content) {
		getGgbAPI().setPageContent(pageId, content);
	}

	public void updateOrdering(String labels) {
		getGgbAPI().updateOrdering(labels);
	}

	public String getOrdering() {
		return getGgbAPI().getOrdering();
	}

	public void previewRefresh() {
		getGgbAPI().previewRefresh();
	}

	public void groupObjects(String[] objects) {
		getGgbAPI().groupObjects(objects);
	}

	public void ungroupObjects(String[] objects) {
		getGgbAPI().ungroupObjects(objects);
	}

	public JsArray<String> getObjectsOfItsGroup(String object) {
		return new JsArray<>(getGgbAPI().getObjectsOfItsGroup(object));
	}

	public void addGeoToTV(String label) {
		getGgbAPI().addGeoToTV(label);
	}

	public void removeGeoFromTV(String label) {
		getGgbAPI().removeGeoFromTV(label);
	}

	public void setValuesOfTV(String values) throws InvalidValuesException {
		getGgbAPI().setValuesOfTV(values);
	}

	public void showPointsTV(String column, String show) {
		getGgbAPI().showPointsTV(column, show);
	}

	public boolean hasUnlabeledPredecessors(String label) {
		return getGgbAPI().hasUnlabeledPredecessors(label);
	}

	public void lockTextElement(String label) {
		getGgbAPI().lockTextElement(label);
	}

	public void unlockTextElement(String label) {
		getGgbAPI().unlockTextElement(label);
	}

	public void setGraphicsOptions(Object arg1, Object arg2) {
		if (Js.isTruthy(arg2)) {
			getGgbAPI().setGraphicsOptions(Js.coerceToInt(arg1), arg2);
		} else {
			getGgbAPI().setGraphicsOptions(arg1);
		}
	}

	public Object getGraphicsOptions(int viewId) {
		return getGgbAPI().getGraphicsOptions(viewId);
	}

	public void setGlobalOptions(Object options) {
		getGgbAPI().setGlobalOptions(options);
	}

	public void switchCalculator(String appCode) {
		getGgbAPI().switchCalculator(appCode);
	}

	public String getStyleXML(String objName) {
		return getGgbAPI().getStyleXML(objName);
	}

	public JsArray<String> getSiblingObjectNames(String objName) {
		return new JsArray<>(getGgbAPI().getSiblingObjectNames(objName));
	}

	public void setAlgebraOptions(Object options) {
		getGgbAPI().setAlgebraOptions(options);
	}

	public void showAllObjects() {
		getGgbAPI().showAllObjects();
	}

	public Object getFileLoadingError() {
		return getGgbAPI().getFileLoadingError();
	}

	public void exitFullScreen() {
		getGgbAPI().exitFullScreen();
	}

	public boolean isFullScreenActive() {
		return getGgbAPI().isFullScreenActive();
	}

	private GgbAPIW getGgbAPI() {
		if (ggbAPI == null) {
			throw new IllegalStateException("app was already removed");
		}
		return ggbAPI;
	}
}
