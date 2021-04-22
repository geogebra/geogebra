package org.geogebra.web.html5.main;

import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.web.html5.util.JsRunnable;
import org.geogebra.web.html5.util.StringConsumer;

import com.google.gwt.dom.client.Element;

import elemental2.core.Global;
import elemental2.core.JsArray;
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
		ggbAPI.removeApplet();
		scriptManager.export(null);
	}

	public String getXML(String objName) {
		if (Js.isTruthy(objName)) {
			return ggbAPI.getXML(objName + "");
		} else {
			return ggbAPI.getXML();
		}
	}

	public String getAlgorithmXML(String objName) {
		return ggbAPI.getAlgorithmXML(objName + "");
	}

	public String getPerspectiveXML() {
		return ggbAPI.getPerspectiveXML();
	}

	public String getBase64(Object param1, Object param2) {
		if (Js.isTripleEqual(param2, false)) {
			return ggbAPI.getBase64(false);
		}
		if (Js.isTripleEqual(param2, true)) {
			return ggbAPI.getBase64(true);
		}
		if (JsEval.isFunction(param2)) {
			ggbAPI.getBase64(Js.isTruthy(param1), (StringConsumer) param2);
		} else if (JsEval.isFunction(param1)) {
			ggbAPI.getBase64(false, (StringConsumer) param1);
		} else {
			return ggbAPI.getBase64();
		}

		return null;
	}

	public void setBase64(String base64string, JsRunnable callback) {
		ggbAPI.setBase64(base64string + "", callback);
	}

	public void openFile(String filename, JsRunnable callback) {
		ggbAPI.openFile(filename + "", callback);
	}

	public void login(String token, Object ui) {
		ggbAPI.login(token  + "", Js.isTruthy(ui));
	}

	public void logout() {
		ggbAPI.logout();
	}

	public void setXML(String xml) {
		ggbAPI.setXML(xml + "");
	}

	public void evalXML(String xmlString) {
		ggbAPI.evalXML(xmlString + "");
	}

	public void setDisplayStyle(String objName, String style) {
		ggbAPI.setDisplayStyle(objName + "", style + "");
	}

	public boolean evalCommand(String cmdString) {
		return ggbAPI.evalCommandNoException(cmdString + "");
	}

	public String evalCommandGetLabels(String cmdString) {
		return ggbAPI.evalCommandGetLabelsNoException(cmdString + "");
	}

	public Promise<String> asyncEvalCommand(String cmdString) {
		return new Promise<>((resolve, reject) -> {
			ggbAPI.asyncEvalCommand(cmdString + "", resolve, reject);
		});
	}

	public Promise<String> asyncEvalCommandGetLabels(String cmdString) {
		return new Promise<>((resolve, reject) -> {
			ggbAPI.asyncEvalCommandGetLabels(cmdString + "", resolve, reject);
		});
	}

	public String evalCommandCAS(String cmdString) {
		return ggbAPI.evalCommandCAS(cmdString + "");
	}

	public String evalGeoGebraCAS(String cmdString) {
		return ggbAPI.evalGeoGebraCAS(cmdString + "");
	}

	public void setFixed(String objName, Object flag, Object selection) {
		if (JsEval.isUndefined(selection)) {
			ggbAPI.setFixed(objName + "", Js.isTruthy(flag));
		} else {
			ggbAPI.setFixed(objName + "", Js.isTruthy(flag), Js.isTruthy(selection));
		}
	}

	public void setOnTheFlyPointCreationActive(Object flag) {
		ggbAPI.setOnTheFlyPointCreationActive(Js.isTruthy(flag));
	}

	public void setUndoPoint() {
		ggbAPI.setUndoPoint();
	}

	public void setSaved() {
		ggbAPI.setSaved();
	}

	public boolean isSaved() {
		return ggbAPI.isSaved();
	}

	public void initCAS() {
		ggbAPI.initCAS();
	}

	public void uploadToGeoGebraTube() {
		ggbAPI.uploadToGeoGebraTube();
	}

	public void setErrorDialogsActive(Object flag) {
		ggbAPI.setErrorDialogsActive(Js.isTruthy(flag));
	}

	// TODO: implement this in Desktop and Web
	public void reset() {
		ggbAPI.reset();
	}

	public void refreshViews() {
		ggbAPI.refreshViews();
	}

	public void setVisible(String objName, Object visible) {
		ggbAPI.setVisible(objName + "", Js.isTruthy(visible));
	}

	public boolean getVisible(String objName, Object view) {
		if (!JsEval.isUndefined(view)) {
			return ggbAPI.getVisible(objName + "", Js.coerceToInt(view));
		}
		return ggbAPI.getVisible(objName + "");
	}

	public void setLayer(String objName, int layer) {
		ggbAPI.setLayer(objName + "", layer);
	}

	public int getLayer(String objName) {
		return ggbAPI.getLayer(objName + "");
	}

	public void setLayerVisible(int layer, Object visible) {
		ggbAPI.setLayerVisible(layer, Js.isTruthy(visible));
	}

	public void setTrace(String objName, Object flag) {
		ggbAPI.setTrace(objName + "", Js.isTruthy(flag));
	}

	public boolean isTracing(String objName) {
		return ggbAPI.isTracing(objName + "");
	}

	public void setLabelVisible(String objName, Object visible) {
		ggbAPI.setLabelVisible(objName + "", Js.isTruthy(visible));
	}

	public void setLabelStyle(String objName, int style) {
		ggbAPI.setLabelStyle(objName + "", style);
	}

	public int getLabelStyle(String objName) {
		return ggbAPI.getLabelStyle(objName + "");
	}

	public boolean getLabelVisible(String objName) {
		return ggbAPI.getLabelVisible(objName + "");
	}

	public void setColor(String objName, int red, int green, int blue) {
		ggbAPI.setColor(objName + "", red, green, blue);
	}

	public void setCorner(String objName, double x, double y, Object index) {
		ggbAPI.setCorner(objName + "", x, y, Js.isFalsy(index) ? 1 : Js.coerceToInt(index));
	}

	public void setLineStyle(String objName, int style) {
		ggbAPI.setLineStyle(objName + "", style);
	}

	public void setLineThickness(String objName, int thickness) {
		ggbAPI.setLineThickness(objName + "", thickness);
	}

	public void setPointStyle(String objName, int style) {
		ggbAPI.setPointStyle(objName + "", style);
	}

	public void setPointSize(String objName, int style) {
		ggbAPI.setPointSize(objName + "", style);
	}

	public void setFilling(String objName, double filling) {
		ggbAPI.setFilling(objName + "", filling);
	}

	public String getColor(String objName) {
		return ggbAPI.getColor(objName + "");
	}

	public String getPenColor() {
		return ggbAPI.getPenColor();
	}

	public int getPenSize() {
		return ggbAPI.getPenSize();
	}

	public void setPenSize(int size) {
		ggbAPI.setPenSize(size);
	}

	public void setPenColor(int red, int green, int blue) {
		ggbAPI.setPenColor(red, green, blue);
	}

	public double getFilling(String objName) {
		return ggbAPI.getFilling(objName + "");
	}

	public String getImageFileName(String objName) {
		return ggbAPI.getImageFileName(objName + "");
	}

	public int getLineStyle(String objName) {
		return ggbAPI.getLineStyle(objName + "");
	}

	public int getLineThickness(String objName) {
		return ggbAPI.getLineThickness(objName + "");
	}

	public int getPointStyle(String objName) {
		return ggbAPI.getPointStyle(objName + "");
	}

	public int getPointSize(String objName) {
		return ggbAPI.getPointSize(objName + "");
	}

	public void deleteObject(String objName) {
		ggbAPI.deleteObject(objName + "");
	}

	public void setAnimating(String objName, Object animate) {
		ggbAPI.setAnimating(objName + "", Js.isTruthy(animate));
	}

	public void setAnimationSpeed(String objName, double speed) {
		ggbAPI.setAnimationSpeed(objName + "", speed);
	}

	public void startAnimation() {
		ggbAPI.startAnimation();
	}

	public void stopAnimation() {
		ggbAPI.stopAnimation();
	}

	public void setAuxiliary(String objName, Object auxiliary) {
		ggbAPI.setAuxiliary(objName + "", Js.isTruthy(auxiliary));
	}

	public void hideCursorWhenDragging(Object hideCursorWhenDragging) {
		ggbAPI.hideCursorWhenDragging(Js.isTruthy(hideCursorWhenDragging));
	}

	public boolean isAnimationRunning() {
		return ggbAPI.isAnimationRunning();
	}

	public double getFrameRate() {
		return ggbAPI.getFrameRate();
	}

	public boolean renameObject(String oldName, String newName, Object force) {
		return ggbAPI.renameObject(oldName + "", newName + "", Js.isTruthy(force));
	}

	public boolean exists(String objName) {
		return ggbAPI.exists(objName + "");
	}

	public boolean isDefined(String objName) {
		return ggbAPI.isDefined(objName + "");
	}

	public String getValueString(String objName, Object localized) {
		boolean localizedB = JsEval.isUndefined(localized) || Js.isTruthy(localized);
		return ggbAPI.getValueString(objName + "", localizedB);
	}

	public double getListValue(String objName, int index) {
		return ggbAPI.getListValue(objName + "", index);
	}

	public String getDefinitionString(String objName, Object localized) {
		boolean localizedB = JsEval.isUndefined(localized) || Js.isTruthy(localized);
		return ggbAPI.getDefinitionString(objName + "",  localizedB);
	}

	public String getLaTeXString(String objName) {
		return ggbAPI.getLaTeXString(objName + "");
	}

	public String getLaTeXBase64(String objName, Object value) {
		return ggbAPI.getLaTeXBase64(objName + "", Js.isTruthy(value));
	}

	public String getCommandString(String objName, Object localized) {
		boolean localizedB = JsEval.isUndefined(localized) || Js.isTruthy(localized);
		return ggbAPI.getCommandString(objName + "", localizedB);
	}

	public String getCaption(String objName, Object subst) {
		return ggbAPI.getCaption(objName + "", Js.isTruthy(subst));
	}

	public void setCaption(String objName, String caption) {
		ggbAPI.setCaption(objName + "", caption + "");
	}

	public double getXcoord(String objName) {
		return ggbAPI.getXcoord(objName + "");
	}

	public double getYcoord(String objName) {
		return ggbAPI.getYcoord(objName + "");
	}

	public double getZcoord(String objName) {
		return ggbAPI.getZcoord(objName + "");
	}

	public void setCoords(String objName, double x, double y, double z) {
		if (JsEval.isUndefined(z)) {
			ggbAPI.setCoords(objName + "", x, y);
		} else {
			ggbAPI.setCoords(objName + "", x, y, z);
		}
	}

	public double getValue(String objName) {
		return ggbAPI.getValue(objName + "");
	}

	public String getVersion() {
		return ggbAPI.getVersion();
	}

	public void getScreenshotBase64(StringConsumer callback) {
		ggbAPI.getScreenshotBase64(callback);
	}

	public String getThumbnailBase64() {
		return ggbAPI.getThumbnailBase64();
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

		ggbAPI.setValue(objName + "", value);
	}

	public void setTextValue(String objName, String x) {
		ggbAPI.setTextValue(objName + "", x + "");
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

		ggbAPI.setListValue(objName + "", xValue, yValue);
	}

	public void setRepaintingActive(Object flag) {
		ggbAPI.setRepaintingActive(Js.isTruthy(flag));
	}

	public void setCoordSystem(double xmin, double xmax, double ymin, double ymax, Object zmin,
			Object zmax, Object verticalY) {
		if (!"number".equals(Js.typeof(zmin))) {
			ggbAPI.setCoordSystem(xmin, xmax, ymin, ymax);
		} else {
			ggbAPI.setCoordSystem(xmin, xmax, ymin, ymax, Js.coerceToDouble(zmin),
					Js.coerceToDouble(zmax), Js.isTruthy(verticalY));
		}
	}

	public void setAxesVisible(Object arg1, Object arg2, Object arg3, Object arg4) {
		if (JsEval.isUndefined(arg3)) {
			ggbAPI.setAxesVisible(Js.isTruthy(arg1), Js.isTruthy(arg2));
		} else {
			ggbAPI.setAxesVisible(Js.coerceToInt(arg1), Js.isTruthy(arg2), Js.isTruthy(arg3),
					Js.isTruthy(arg4));
		}
	}

	public void setAxisUnits(int arg1, String arg2, String arg3, String arg4) {
		ggbAPI.setAxisUnits(arg1, arg2 + "", arg3 + "", arg4 + "");
	}

	public void setAxisLabels(int arg1, String arg2, String arg3, String arg4) {
		ggbAPI.setAxisLabels(arg1, arg2 + "", arg3 + "", arg4 + "");
	}

	public void setAxisSteps(int arg1, String arg2, String arg3, String arg4) {
		ggbAPI.setAxisSteps(arg1, arg2 + "", arg3 + "", arg4 + "");
	}

	public JsArray<String> getAxisUnits(Object arg1) {
		return new JsArray<>(ggbAPI.getAxisUnits(Js.coerceToInt(arg1)));
	}

	public JsArray<String> getAxisLabels(Object arg1) {
		return new JsArray<>(ggbAPI.getAxisLabels(Js.coerceToInt(arg1)));
	}

	public void setPointCapture(int view, Object capture) {
		if (JsEval.isUndefined(capture)) {
			ggbAPI.setPointCapture(1, view);
		} else {
			ggbAPI.setPointCapture(view, Js.coerceToInt(capture));
		}
	}

	public boolean getGridVisible(Object view) {
		if (JsEval.isUndefined(view)) {
			return ggbAPI.getGridVisible(1);
		} else {
			return ggbAPI.getGridVisible(Js.coerceToInt(view));
		}
	}

	public void setGridVisible(Object arg1, Object arg2) {
		if (JsEval.isUndefined(arg2)) {
			ggbAPI.setGridVisible(Js.isTruthy(arg1));
		} else {
			ggbAPI.setGridVisible(Js.coerceToInt(arg1), Js.isTruthy(arg2));
		}
	}

	public JsArray<String> getAllObjectNames(String objectType) {
		if (JsEval.isUndefined(objectType)) {
			return new JsArray<>(ggbAPI.getAllObjectNames());
		} else {
			return new JsArray<>(ggbAPI.getAllObjectNames(objectType + ""));
		}
	}

	public int getObjectNumber() {
		return ggbAPI.getObjectNumber();
	}

	public String getObjectName(int i) {
		return ggbAPI.getObjectName(i);
	}

	public String getObjectType(String objName) {
		return ggbAPI.getObjectType(objName + "");
	}

	public void setMode(int mode) {
		ggbAPI.setMode(mode);
	}

	public int getMode() {
		return ggbAPI.getMode();
	}

	public String getToolName(int i) {
		return ggbAPI.getToolName(i);
	}

	public void openMaterial(String material) {
		ggbAPI.openMaterial(material + "");
	}

	public void undo(Object repaint) {
		ggbAPI.undo(Js.isTruthy(repaint));
	}

	public void redo(Object repaint) {
		ggbAPI.redo(Js.isTruthy(repaint));
	}

	public void newConstruction() {
		ggbAPI.newConstruction();
	}

	public void resetAfterSaveLoginCallbacks() {
		ggbAPI.resetAfterSaveLoginCallbacks();
	}

	public void debug(String str) {
		ggbAPI.debug(str + "");
	}

	public void setWidth(int width) {
		ggbAPI.setWidth(width);
	}

	public void setHeight(int height) {
		ggbAPI.setHeight(height);
	}

	public void setSize(int width, int height) {
		ggbAPI.setSize(width, height);
	}

	public void enableRightClick(Object enable) {
		ggbAPI.enableRightClick(Js.isTruthy(enable));
	}

	public void enableLabelDrags(Object enable) {
		ggbAPI.enableLabelDrags(Js.isTruthy(enable));
	}

	public void enableShiftDragZoom(Object enable) {
		ggbAPI.enableShiftDragZoom(Js.isTruthy(enable));
	}

	public void showToolBar(Object show) {
		ggbAPI.showToolBar(Js.isTruthy(show));
	}

	public void setCustomToolBar(String toolbarDef) {
		ggbAPI.setCustomToolBar(toolbarDef + "");
	}

	public void showMenuBar(Object show) {
		ggbAPI.showMenuBar(Js.isTruthy(show));
	}

	public void showAlgebraInput(Object show) {
		ggbAPI.showAlgebraInput(Js.isTruthy(show));
	}

	public void showResetIcon(Object show) {
		ggbAPI.showResetIcon(Js.isTruthy(show));
	}

	public String getViewProperties(int view) {
		return ggbAPI.getViewProperties(view);
	}

	public void setFont(String label, int size, Object bold, Object italic, Object serif) {
		ggbAPI.setFont(label + "", size, Js.isTruthy(bold), Js.isTruthy(italic),
				Js.isTruthy(serif));
	}

	public String insertImage(String url, String corner1, String corner2, String corner4) {
		return ggbAPI.insertImage(url + "", corner1 + "", corner2 + "", corner4 + "");
	}

	public void addImage(String fileName, String url) {
		ggbAPI.addImage(fileName + "", url + "");
	}

	public void recalculateEnvironments() {
		ggbAPI.recalculateEnvironments();
	}

	public boolean isIndependent(String label) {
		return ggbAPI.isIndependent(label + "");
	}

	public boolean isMoveable(String label) {
		return ggbAPI.isMoveable(label + "");
	}

	public void setPerspective(String code) {
		ggbAPI.setPerspective(code + "");
	}

	public void enableCAS(Object enable) {
		ggbAPI.enableCAS(Js.isTruthy(enable));
	}

	public void enable3D(Object enable) {
		ggbAPI.enable3D(Js.isTruthy(enable));
	}

	public String getPNGBase64(double exportScale, Object transparent, double dpi,
			Object copyToClipboard, Object greyscale) {
		return ggbAPI.getPNGBase64(exportScale, Js.isTruthy(transparent), dpi,
				Js.isTruthy(copyToClipboard), Js.isTruthy(greyscale));
	}

	public void exportGIF(String sliderLabel, double scale, double timeBetweenFrames,
			Object isLoop, String filename, Object rotate) {
		ggbAPI.exportGIF(sliderLabel, scale, timeBetweenFrames, Js.isTruthy(isLoop),
				filename, Js.coerceToInt(rotate));
	}

	public Object getFileJSON(Object thumbnail) {
		return ggbAPI.getFileJSON(Js.isTruthy(thumbnail));
	}

	public void setFileJSON(Object zip) {
		ggbAPI.setFileJSON(zip);
	}

	public void setLanguage(String lang) {
		ggbAPI.setLanguage(lang + "");
	}

	public void showTooltip(Object tooltip) {
		ggbAPI.showTooltip(tooltip + "");
	}

	public void addMultiuserSelection(Object user, Object color, Object label, boolean newGeo) {
		ggbAPI.addMultiuserSelection(user + "", color + "", label + "", newGeo);
	}

	public void removeMultiuserSelections(Object user) {
		ggbAPI.removeMultiuserSelections(user + "");
	}

	// APPS-646 deprecated, needs changing to getValue("correct")
	public double getExerciseFraction() {
		return ggbAPI.getExerciseFraction();
	}

	// APPS-646 Exercises no longer supported
	public boolean isExercise() {
		return false;
	}

	public void setExternalPath(String path) {
		ggbAPI.setExternalPath(path + "");
	}

	public void checkSaved(JsRunnable path) {
		ggbAPI.checkSaved(path);
	}

	public int getCASObjectNumber() {
		return ggbAPI.getCASObjectNumber();
	}

	public boolean writePNGtoFile(String filename, double exportScale, Object transparent,
			double DPI, Object greyscale) {
		return ggbAPI.writePNGtoFile(filename + "", exportScale, Js.isTruthy(transparent),
				DPI, Js.isTruthy(greyscale));
	}

	public void exportPGF(StringConsumer callback) {
		ggbAPI.exportPGF(callback);
	}

	public String exportSVG(String filename) {
		return ggbAPI.exportSVG(filename + "");
	}

	public String exportPDF(Object scale, String filename, String sliderLabel) {
		return ggbAPI.exportPDF(Js.coerceToDouble(scale), filename, sliderLabel);
	}

	public void exportPSTricks(StringConsumer callback) {
		ggbAPI.exportPSTricks(callback);
	}

	public void exportAsymptote(StringConsumer callback) {
		ggbAPI.exportAsymptote(callback);
	}

	public void setRounding(String digits) {
		ggbAPI.setRounding(digits + "");
	}

	public String getRounding() {
		return ggbAPI.getRounding();
	}

	public void copyTextToClipboard(String text) {
		ggbAPI.copyTextToClipboard(text + "");
	}

	public void evalLaTeX(String text, int mode) {
		ggbAPI.evalLaTeX(text + "", mode);
	}

	public boolean evalMathML(String text) {
		return ggbAPI.evalMathML(text + "");
	}

	public String getScreenReaderOutput(String text) {
		return ggbAPI.getScreenReaderOutput(text + "");
	}

	public String getEditorState() {
		return ggbAPI.getEditorState();
	}

	public void setEditorState(Object state, String label) {
		String stateString = JsEval.isJSString(state) ? Js.asString(state)
				: Global.JSON.stringify(state);
		ggbAPI.setEditorState(stateString, label);
	}

	public String exportCollada(Object xmin, Object xmax, Object ymin, Object ymax, Object zmin,
			Object zmax, Object xyScale, Object xzScale, Object xTickDistance,
			Object yTickDistance, Object zTickDistance) {
		return ggbAPI.exportCollada(doubleOrDefault(xmin, -5), doubleOrDefault(xmax, 5),
				doubleOrDefault(ymin, -5), doubleOrDefault(ymax, 5),
				doubleOrDefault(zmin, -5), doubleOrDefault(zmax, 5),
				doubleOrDefault(xyScale, 1), doubleOrDefault(xzScale, 1),
				doubleOrDefault(xTickDistance, -1), doubleOrDefault(yTickDistance, -1),
				doubleOrDefault(zTickDistance, -1));
	}

	public String exportSimple3d(String name, double xmin, double xmax, double ymin, double ymax,
			double zmin, double zmax, double xyScale, double xzScale, double xTickDistance,
			double yTickDistance, double zTickDistance) {
		return ggbAPI.exportSimple3d(
			name + "", xmin, xmax, ymin, ymax, zmin, zmax, xyScale,
			xzScale, xTickDistance, yTickDistance, zTickDistance);
	}

	public String translate(String arg1, StringConsumer callback) {
		return ggbAPI.translate(arg1 + "", callback);
	}

	public String exportConstruction(String[] flags) {
		if (Js.isTruthy(flags)) {
			return ggbAPI.exportConstruction(flags);
		} else {
			return ggbAPI.exportConstruction("color", "name", "definition", "value");
		}
	}

	public void updateConstruction() {
		ggbAPI.updateConstruction();
	}

	public double getConstructionSteps(Object breakpoints) {
		return ggbAPI.getConstructionSteps(Js.isTruthy(breakpoints));
	}

	public void setConstructionStep(double n, Object breakpoints) {
		ggbAPI.setConstructionStep(n, Js.isTruthy(breakpoints));
	}

	public void previousConstructionStep() {
		ggbAPI.previousConstructionStep();
	}

	public void nextConstructionStep() {
		ggbAPI.nextConstructionStep();
	}

	public JsPropertyMap<Object> getEmbeddedCalculators(Object includeGraspableMath) {
		return ggbAPI.getEmbeddedCalculators(Js.isTruthy(includeGraspableMath));
	}

	public Element getFrame() {
		return ggbAPI.getFrame();
	}

	public void enableFpsMeasurement() {
		ggbAPI.enableFpsMeasurement();
	}

	public void disableFpsMeasurement() {
		ggbAPI.disableFpsMeasurement();
	}

	public void testDraw() {
		ggbAPI.testDraw();
	}

	public void startDrawRecording() {
		ggbAPI.startDrawRecording();
	}

	public void endDrawRecordingAndLogResults() {
		ggbAPI.endDrawRecordingAndLogResults();
	}

	public void registerAddListener(Object JSFunctionName) {
		ggbAPI.registerAddListener(JSFunctionName);
	}

	public void unregisterAddListener(Object JSFunctionName) {
		ggbAPI.unregisterAddListener(JSFunctionName);
	}

	public void registerStoreUndoListener(Object JSFunctionName) {
		ggbAPI.registerStoreUndoListener(JSFunctionName);
	}

	public void unregisterStoreUndoListener(Object JSFunctionName) {
		ggbAPI.unregisterStoreUndoListener(JSFunctionName);
	}

	public void registerRemoveListener(Object JSFunctionName) {
		ggbAPI.registerRemoveListener(JSFunctionName);
	}

	public void unregisterRemoveListener(Object JSFunctionName) {
		ggbAPI.unregisterRemoveListener(JSFunctionName);
	}

	public void registerClearListener(Object JSFunctionName) {
		ggbAPI.registerClearListener(JSFunctionName);
	}

	public void unregisterClearListener(Object JSFunctionName) {
		ggbAPI.unregisterClearListener(JSFunctionName);
	}

	public void registerRenameListener(Object JSFunctionName) {
		ggbAPI.registerRenameListener(JSFunctionName);
	}

	public void unregisterRenameListener(Object JSFunctionName) {
		ggbAPI.unregisterRenameListener(JSFunctionName);
	}

	public void registerUpdateListener(Object JSFunctionName) {
		ggbAPI.registerUpdateListener(JSFunctionName);
	}

	public void unregisterUpdateListener(Object JSFunctionName) {
		ggbAPI.unregisterUpdateListener(JSFunctionName);
	}

	public void registerClientListener(Object JSFunctionName) {
		ggbAPI.registerClientListener(JSFunctionName);
	}

	public void unregisterClientListener(Object JSFunctionName) {
		ggbAPI.unregisterClientListener(JSFunctionName);
	}

	public void registerObjectUpdateListener(String objName, Object JSFunctionName) {
		ggbAPI.registerObjectUpdateListener(objName + "", JSFunctionName);
	}

	public void unregisterObjectUpdateListener(String label) {
		ggbAPI.unregisterObjectUpdateListener(label);
	}

	public void registerObjectClickListener(String objName, Object JSFunctionName) {
		ggbAPI.registerObjectClickListener(objName + "", JSFunctionName);
	}

	public void unregisterObjectClickListener(String objName) {
		ggbAPI.unregisterObjectClickListener(objName + "");
	}

	public void registerClickListener(Object JSFunctionName) {
		ggbAPI.registerClickListener(JSFunctionName);
	}

	public void unregisterClickListener(Object JSFunctionName) {
		ggbAPI.unregisterClickListener(JSFunctionName);
	}

	public void handleSlideAction(Object eventType, Object pageIdx, Object appState) {
		ggbAPI.handleSlideAction(eventType + "", pageIdx + "", appState + "");
	}

	public void selectSlide(Object pageIdx) {
		ggbAPI.selectSlide(pageIdx + "");
	}

	public void updateOrdering(String labels) {
		ggbAPI.updateOrdering(labels);
	}

	public void previewRefresh() {
		ggbAPI.previewRefresh();
	}

	public void groupObjects(String[] objects) {
		ggbAPI.groupObjects(objects);
	}

	public void ungroupObjects(String[] objects) {
		ggbAPI.ungroupObjects(objects);
	}

	public String[] getObjectsOfItsGroup(String object) {
		return ggbAPI.getObjectsOfItsGroup(object);
	}

	public void addToGroup(String item, String[] objectsInGroup) {
		ggbAPI.addToGroup(item, objectsInGroup);
	}

	public void setEmbedContent(String label, String base64) {
		ggbAPI.setEmbedContent(label, base64);
	}

	public void addGeoToTV(String label) {
		ggbAPI.addGeoToTV(label);
	}

	public void removeGeoFromTV(String label) {
		ggbAPI.removeGeoFromTV(label);
	}

	public void setValuesOfTV(String values) throws InvalidValuesException {
		ggbAPI.setValuesOfTV(values);
	}

	public void showPointsTV(String column, String show) {
		ggbAPI.showPointsTV(column, show);
	}

	public boolean hasUnlabeledPredecessors(String label) {
		return ggbAPI.hasUnlabeledPredecessors(label);
	}
}
