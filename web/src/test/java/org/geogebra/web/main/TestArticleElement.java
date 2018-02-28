package org.geogebra.web.main;

import org.geogebra.common.main.App.InputPosition;
import org.geogebra.web.html5.util.ArticleElementInterface;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class TestArticleElement implements ArticleElementInterface {

	private String appName;
	private String prerelease;

	public TestArticleElement(String prerelease, String appName) {
		this.prerelease = prerelease;
		this.appName = appName;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDataParamId() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getDataParamFileName() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getDataParamJSON() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public boolean getDataParamEnableLabelDrags() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDataParamEnableUndoRedo() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDataParamEnableRightClick() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDataParamEnableCAS(boolean def) {
		// TODO Auto-generated method stub
		return def;
	}

	@Override
	public boolean getDataParamEnable3D(boolean def) {
		// TODO Auto-generated method stub
		return def;
	}

	@Override
	public boolean getDataParamEnableGraphing(boolean def) {
		// TODO Auto-generated method stub
		return def;
	}

	@Override
	public boolean hasDataParamEnableGraphing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDataParamRounding() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getDataParamBase64String() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public boolean getDataParamShowMenuBar(boolean def) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDataParamAllowStyleBar(boolean def) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDataParamShowToolBar(boolean def) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDataParamShowToolBarHelp(boolean def) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDataParamCustomToolBar() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public boolean getDataParamShowAlgebraInput(boolean def) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InputPosition getAlgebraPosition(InputPosition def) {
		// TODO Auto-generated method stub
		return InputPosition.top;
	}

	@Override
	public boolean getDataParamShowResetIcon() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDataParamShowAnimationButton() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDataParamCapturingThreshold() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getDataParamLanguage() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getDataParamCountry() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public boolean getDataParamUseBrowserForJS() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDataParamShiftDragZoomEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDataParamWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDataParamHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getDataParamFitToScreen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDataParamBorder() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public boolean getDataParamShowLogging() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDebugGraphics() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDataParamAllowSymbolTable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRTL() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getParentScaleX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getScaleX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double readScaleX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getScaleY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getDataParamAllowStyleBar() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDataParamApp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDataParamScreenshotGenerator() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDataParamLAF() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public boolean preventFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDataClientID() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getDataParamPerspective() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getDataParamAppName() {
		return appName;
	}

	@Override
	public double getDataParamScale() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean getDataParamButtonShadows() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getDataParamButtonRounding() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void adjustScale() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetScale() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDataParamPrerelease() {
		return prerelease;
	}

	@Override
	public String getDataParamTubeID() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public boolean getDataParamShowStartTooltip(boolean def) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDataParamEnableFileFeatures() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void initID(int i) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getDataParamErrorDialogsActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMaterialsAPIurl() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getLoginAPIurl() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public boolean getDataParamShowAppsPicker() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getBorderThickness() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getDataParamShowZoomButtons() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDataParamShowFullscreenButton() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDataParamShowSuggestionButtons() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDataParamMarginTop() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int computeHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getDataParamFontsCssUrl() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void setAttribute(String string, String string2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAttribute(String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public Element getParentElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element getElement() {
		return DOM.createElement("article");
	}

	@Override
	public boolean useCompatibilityCookie() {
		// TODO Auto-generated method stub
		return false;
	}

}
