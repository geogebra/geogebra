package org.geogebra.web.html5.main;

import java.util.HashMap;

import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.web.html5.util.ArticleElementInterface;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

/**
 * Mock of the article element
 */
public class TestArticleElement implements ArticleElementInterface {

	private HashMap<String, String> attributes = new HashMap<>();
	private Element parentElement;
	private double scale = 1;

	/**
	 * @param appName
	 *            name of app
	 */
	public TestArticleElement(String appName) {
		attr("appName", appName).attr("width", "800").attr("height", "600");
	}

	@Override
	public void clear() {
		// intentionally empty
	}

	@Override
	public String getDataParamId() {
		return attributes.getOrDefault("id",
				ArticleElementInterface.DEFAULT_APPLET_ID);
	}

	@Override
	public String getDataParamFileName() {
		return "";
	}

	@Override
	public String getDataParamJSON() {
		return attributes.getOrDefault("jsonFile", "");
	}

	@Override
	public boolean getDataParamEnableLabelDrags() {
		return false;
	}

	@Override
	public boolean getDataParamEnableUndoRedo() {
		return false;
	}

	@Override
	public boolean getDataParamEnableRightClick() {
		return !"false".equals(attributes.get("enableRightClick"));
	}

	@Override
	public boolean getDataParamEnableCAS(boolean def) {
		return def;
	}

	@Override
	public boolean getDataParamEnable3D(boolean def) {
		return def;
	}

	@Override
	public boolean getDataParamEnableGraphing(boolean def) {
		return def;
	}

	@Override
	public boolean hasDataParamEnableGraphing() {
		return false;
	}

	@Override
	public String getDataParamRounding() {
		return "";
	}

	@Override
	public String getDataParamBase64String() {
		return attributes.getOrDefault("ggbBase64", "");
	}

	@Override
	public boolean getDataParamShowMenuBar(boolean def) {
		if (attributes.containsKey("showMenuBar")) {
			return "true".equals(attributes.get("showMenuBar"));
		}

		return def;
	}

	@Override
	public boolean getDataParamAllowStyleBar(boolean def) {
		return "true".equals(attributes.get("allowStyleBar"));
	}

	@Override
	public boolean getDataParamShowToolBar(boolean def) {
		return "true".equals(attributes.get("showToolBar"));
	}

	@Override
	public boolean getDataParamShowToolBarHelp(boolean def) {
		return getDataParamShowToolBar(def);
	}

	@Override
	public String getDataParamCustomToolBar() {
		return attributes.getOrDefault("customToolbar", "");
	}

	@Override
	public boolean getDataParamShowAlgebraInput(boolean def) {
		return "true".equals(attributes.get("showAlgebraInput"));
	}

	@Override
	public InputPosition getAlgebraPosition(InputPosition def) {
		return def;
	}

	@Override
	public boolean getDataParamShowResetIcon() {
		return false;
	}

	@Override
	public boolean getDataParamShowAnimationButton() {
		return false;
	}

	@Override
	public int getDataParamCapturingThreshold() {
		return App.DEFAULT_THRESHOLD;
	}

	@Override
	public String getDataParamLanguage() {
		return "";
	}

	@Override
	public String getDataParamCountry() {
		return "";
	}

	@Override
	public boolean getDataParamUseBrowserForJS() {
		return "true".equals(attributes.get("useBrowserForJS"));
	}

	@Override
	public String[] getDataParamPreloadModules() {
		return null;
	}

	@Override
	public boolean getDataParamShiftDragZoomEnabled() {
		return true;
	}

	@Override
	public int getDataParamWidth() {
		return getInt(attributes.get("width"));
	}

	@Override
	public int getDataParamHeight() {
		return getInt(attributes.get("height"));
	}

	@Override
	public boolean getDataParamFitToScreen() {
		return false;
	}

	@Override
	public String getDataParamBorder() {
		return attributes.getOrDefault("borderColor", "");
	}

	@Override
	public boolean getDataParamShowLogging() {
		return true;
	}

	@Override
	public boolean isDebugGraphics() {
		return false;
	}

	@Override
	public boolean getDataParamAllowSymbolTable() {
		return false;
	}

	@Override
	public boolean isRTL() {
		return false;
	}

	@Override
	public double getParentScaleX() {
		return 1;
	}

	@Override
	public double getScaleX() {
		return scale;
	}

	@Override
	public double readScaleX() {
		return scale;
	}

	@Override
	public double getScaleY() {
		return scale;
	}

	@Override
	public boolean getDataParamAllowStyleBar() {
		return false;
	}

	@Override
	public boolean getDataParamApp() {
		return false;
	}

	@Override
	public boolean getDataParamScreenshotGenerator() {
		return false;
	}

	@Override
	public String getDataParamLAF() {
		return "";
	}

	@Override
	public boolean preventFocus() {
		return false;
	}

	@Override
	public String getDataClientID() {
		return "";
	}

	@Override
	public String getDataParamPerspective() {
		return "";
	}

	@Override
	public String getDataParamAppName() {
		return attributes.getOrDefault("appName", "");
	}

	@Override
	public double getDataParamScale() {
		return 1;
	}

	@Override
	public boolean getDataParamButtonShadows() {
		return false;
	}

	@Override
	public double getDataParamButtonRounding() {
		return 0;
	}

	@Override
	public String getDataParamButtonBorderColor() {
		return attributes.get("buttonBorderColor");
	}

	@Override
	public void resetScale(double scale1) {
		this.scale = scale1;
	}

	@Override
	public boolean getDataParamPrerelease() {
		return "true".equals(attributes.get("prerelease"));
	}

	@Override
	public String getDataParamTubeID() {
		return "";
	}

	@Override
	public boolean getDataParamShowStartTooltip(boolean def) {
		return false;
	}

	@Override
	public boolean getDataParamEnableFileFeatures() {
		return false;
	}

	@Override
	public void initID(int i) {
		// intentionally empty
	}

	@Override
	public boolean getDataParamErrorDialogsActive() {
		return false;
	}

	@Override
	public String getMaterialsAPIurl() {
		return "";
	}

	@Override
	public String getLoginAPIurl() {
		return "";
	}

	@Override
	public boolean getDataParamShowAppsPicker() {
		return false;
	}

	@Override
	public int getBorderThickness() {
		return 2;
	}

	@Override
	public boolean getDataParamShowZoomButtons() {
		return false;
	}

	@Override
	public boolean getDataParamShowFullscreenButton() {
		return false;
	}

	@Override
	public boolean getDataParamShowSuggestionButtons() {
		return false;
	}

	@Override
	public int getDataParamMarginTop() {
		return 0;
	}

	@Override
	public String getDataParamFontsCssUrl() {
		return "";
	}

	@Override
	public String getId() {
		return "";
	}

	@Override
	public ArticleElementInterface attr(String string, String string2) {
		attributes.put(string, string2);
		return this;
	}

	@Override
	public void removeAttribute(String string) {
		// intentionally empty
	}

	@Override
	public Element getParentElement() {
		return parentElement;
	}

	@Override
	public Element getElement() {
		return DOM.createElement("article");
	}

	@Override
	public String getParamScaleContainerClass() {
		return attributes.getOrDefault("scaleContainerClass", "");
	}

	@Override
	public boolean getParamAllowUpscale() {
		return "true".equals(attributes.get("allowUpscale"));
	}

	@Override
	public boolean getParamAutoHeight() {
		return "true".equals(attributes.get("autoHeight"));
	}

	@Override
	public boolean getParamDisableAutoScale() {
		return false;
	}

	@Override
	public int getParamRandomSeed() {
		return -1;
	}

	/**
	 * Since this doesn't have representation in DOM, we need to set parent
	 * element explicitly.
	 * 
	 * @param element
	 *            parent element
	 */
	public void setParentElement(Element element) {
		this.parentElement = element;
	}

	@Override
	public String getParamLoginURL() {
		return "";
	}

	@Override
	public String getParamLogoutURL() {
		return "";
	}

	@Override
	public String getParamBackendURL() {
		return "";
	}

	@Override
	public String getParamFullscreenContainer() {
		return "";
	}

	private static int getInt(String number) {
		return (int) Double.parseDouble(number);
	}

	@Override
	public String getParamShareLinkPrefix() {
		return "";
	}

	@Override
	public String getParamVendor() {
		return attributes.get("vendor");
	}

	@Override
	public int getParamFontSize(int def) {
		return def;
	}

	@Override
	public String getParamKeyboardType(String def) {
		return def;
	}
}
