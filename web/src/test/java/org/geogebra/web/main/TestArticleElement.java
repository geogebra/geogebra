package org.geogebra.web.main;

import org.geogebra.common.main.App.InputPosition;
import org.geogebra.web.html5.util.ArticleElementInterface;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class TestArticleElement implements ArticleElementInterface {

	private String appName;
	private String prerelease;

	/**
	 * @param prerelease
	 *            flag
	 * @param appName
	 *            name of app
	 */
	public TestArticleElement(String prerelease, String appName) {
		this.prerelease = prerelease;
		this.appName = appName;
	}

	@Override
	public void clear() {
		// intentionally empty
	}

	@Override
	public String getDataParamId() {
		return "";
	}

	@Override
	public String getDataParamFileName() {
		return "";
	}

	@Override
	public String getDataParamJSON() {
		return "";
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
		return false;
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
		return "";
	}

	@Override
	public boolean getDataParamShowMenuBar(boolean def) {
		return false;
	}

	@Override
	public boolean getDataParamAllowStyleBar(boolean def) {
		return false;
	}

	@Override
	public boolean getDataParamShowToolBar(boolean def) {
		return false;
	}

	@Override
	public boolean getDataParamShowToolBarHelp(boolean def) {
		return false;
	}

	@Override
	public String getDataParamCustomToolBar() {
		return "";
	}

	@Override
	public boolean getDataParamShowAlgebraInput(boolean def) {
		return false;
	}

	@Override
	public InputPosition getAlgebraPosition(InputPosition def) {
		return InputPosition.top;
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
		return 0;
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
		return false;
	}

	@Override
	public boolean getDataParamShiftDragZoomEnabled() {
		return false;
	}

	@Override
	public int getDataParamWidth() {
		return 0;
	}

	@Override
	public int getDataParamHeight() {
		return 0;
	}

	@Override
	public boolean getDataParamFitToScreen() {
		return false;
	}

	@Override
	public String getDataParamBorder() {
		return "";
	}

	@Override
	public boolean getDataParamShowLogging() {
		return false;
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
		return 0;
	}

	@Override
	public double getScaleX() {
		return 0;
	}

	@Override
	public double readScaleX() {
		return 0;
	}

	@Override
	public double getScaleY() {
		return 0;
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
		return appName;
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
	public void adjustScale() {
		// intentionally empty
	}

	@Override
	public void resetScale() {
		// intentionally empty
	}

	@Override
	public String getDataParamPrerelease() {
		return prerelease;
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
		return 0;
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
	public int computeHeight() {
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
	public void setAttribute(String string, String string2) {
		// intentionally empty
	}

	@Override
	public void removeAttribute(String string) {
		// intentionally empty
	}

	@Override
	public Element getParentElement() {
		return null;
	}

	@Override
	public Element getElement() {
		return DOM.createElement("article");
	}

	@Override
	public boolean useCompatibilityCookie() {
		return false;
	}

}
