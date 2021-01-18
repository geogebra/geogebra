package org.geogebra.web.html5.util;

import java.util.Locale;

import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import com.google.gwt.user.client.Window;

/**
 *
 */
public class AppletParameters {
	/** default applet ID */
	public static final String DEFAULT_APPLET_ID = "ggbApplet";

	private final AttributeProvider attributeProvider;

	/**
	 * @param appName
	 *            name of app
	 */
	public AppletParameters(String appName) {
		this.attributeProvider = new MapAttributeProvider();
		setAttribute("appName", appName);
		setAttribute("width", "800");
		setAttribute("height", "600");
	}

	public AppletParameters(GeoGebraElement element) {
		this.attributeProvider = element;
	}

	private String getAttribute(String attribute) {
		return attributeProvider.getAttribute("data-param-" + attribute);
	}

	private boolean hasAttribute(String attribute) {
		return attributeProvider.hasAttribute("data-param-" + attribute);
	}

	public void removeAttribute(String attribute) {
		attributeProvider.removeAttribute("data-param-" + attribute);
	}

	/**
	 * @param attribute attribute name
	 * @param value attribute value
	 * @return this
	 */
	public AppletParameters setAttribute(String attribute, String value) {
		attributeProvider.setAttribute("data-param-" + attribute, value);
		return this;
	}

	private boolean getBoolDataParam(String attr, boolean def) {
		return (def && !"false".equals(getAttribute(attr)))
				|| "true".equals(getAttribute(attr));
	}

	private String getStringDataParam(String attr, String def) {
		return hasAttribute(attr) ? getAttribute(attr) : def;
	}

	private double getDoubleDataParam(String attr, double fallback) {
		String val = getAttribute(attr);
		if (val == null || val.isEmpty()) {
			return fallback;
		}
		try {
			return Double.parseDouble(val);
		} catch (Exception e) {
			Log.warn("Invalid value of " + attr + ":" + val);
		}
		return fallback;
	}

	private int getIntDataParam(String attr, int fallback) {
		return (int) Math.round(getDoubleDataParam(attr, fallback));
	}

	/**
	 * @return the data-param-id article attribute as String if set else
	 *         AppWeb.DEFAULT_APPLET_ID
	 */
	public String getDataParamId() {
		String ret = getAttribute("id");
		if (StringUtil.empty(ret) || !ret.matches("[A-Za-z0-9_]+")) {
			return DEFAULT_APPLET_ID;
		}
		return ret;
	}

	/**
	 * @return the data-param-filename article attribute as String if set else
	 *         empty String
	 */
	public String getDataParamFileName() {
		return getStringDataParam("filename", "");
	}

	/**
	 * @return data-param-json (string encoded ZIP file stucture)
	 */
	public String getDataParamJSON() {
		return getStringDataParam("json", "");
	}

	/**
	 * Determines if the "data-param-enableLabelDrags" article attribute is set
	 * to true
	 * 
	 * @return the data-param-enableLabelDrags (default: true)
	 */
	public boolean getDataParamEnableLabelDrags() {
		return getBoolDataParam("enableLabelDrags", true);
	}

	/**
	 * Determines if the "data-param-enableUndoRedo" article attribute is set to
	 * true
	 * 
	 * @return the data-param-enableUndoRedo (default: true)
	 */
	public boolean getDataParamEnableUndoRedo() {
		return getBoolDataParam("enableUndoRedo", true);
	}

	/**
	 * Determines if the "data-param-enableRightClick" article attribute is set
	 * to true
	 * 
	 * @return the data-param-enableRightClick (default: true)
	 */
	public boolean getDataParamEnableRightClick() {
		return getBoolDataParam("enableRightClick", true);
	}

	/**
	 * @param def
	 *            fallback if parameter not set
	 * @return data-param-enableCAS: whether CAS is enabled
	 */
	public boolean getDataParamEnableCAS(boolean def) {
		return getBoolDataParam("enableCAS", def);
	}

	/**
	 * @param def
	 *            fallback if parameter not set
	 * @return data-param-enable3D: whether 3D is enabled
	 */
	public boolean getDataParamEnable3D(boolean def) {
		return getBoolDataParam("enable3D", def);
	}

	/**
	 * @param def
	 *            fallback if parameter not set
	 * @return data-param-enableGraphing: whether graphing, commands and vectors
	 *         are enabled
	 */
	public boolean getDataParamEnableGraphing(boolean def) {
		return getBoolDataParam("enableGraphing", def);
	}

	/**
	 * @return true, if there is data-param-enableGraphing attribute, and it is
	 *         not an empty string
	 */
	public boolean hasDataParamEnableGraphing() {
		return hasAttribute("enableGraphing");
	}

	public boolean hasDataParamEnable3D() {
		return hasAttribute("enable3D");
	}

	/**
	 * @return rounding; consists of integer and suffix that determines whether
	 *         significant figures are used (s) and whether fractions are
	 *         prefered (r)
	 */
	public String getDataParamRounding() {
		return getStringDataParam("rounding", "");
	}

	/**
	 * @return the data-param-ggbbase64 article attribute as String if set else
	 *         empty String
	 */
	public String getDataParamBase64String() {
		return getStringDataParam("ggbBase64", "");
	}

	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return the data-param-showMenuBar (default: false)
	 */
	public boolean getDataParamShowMenuBar(boolean def) {
		return getBoolDataParam("showMenuBar", def) || getDataParamApp();
	}

	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return data-param-allowStylebar: whether to have stylebar; no effect
	 *         when menu is present
	 */
	public boolean getDataParamAllowStyleBar(boolean def) {
		return getBoolDataParam("allowStyleBar", def);
	}

	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return the data-param-showToolBar (default: false)
	 */
	public boolean getDataParamShowToolBar(boolean def) {
		if (getDataParamShowMenuBar(false) || getDataParamApp()) {
			return true;
		}
		return getBoolDataParam("showToolBar", def);
	}

	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return whether to show toolbar help (tooltips)
	 */
	public boolean getDataParamShowToolBarHelp(boolean def) {
		if (!getDataParamShowToolBar(false) && !getDataParamApp()) {
			return false;
		}
		return getBoolDataParam("showToolBarHelp", def);
	}

	/**
	 * 
	 * @return the data-param-customToolBar (default: null)
	 */
	public String getDataParamCustomToolBar() {
		return getStringDataParam("customToolBar", "");
	}

	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return the data-param-showAlgebraInput (default: true)
	 */
	public boolean getDataParamShowAlgebraInput(boolean def) {
		return getBoolDataParam("showAlgebraInput", def);
	}

	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return input position (top / bottom / AV)
	 */
	public InputPosition getAlgebraPosition(InputPosition def) {
		String pos = getStringDataParam("algebraInputPosition", "")
				.toLowerCase().trim();
		if ("top".equals(pos)) {
			return InputPosition.top;
		}
		if ("bottom".equals(pos)) {
			return InputPosition.bottom;
		}
		if (pos.length() > 0) {
			return InputPosition.algebraView;
		}
		return def;
	}

	/**
	 * @return the data-param-showResetIcon (default: false)
	 */
	public boolean getDataParamShowResetIcon() {
		return getBoolDataParam("showResetIcon", false);
	}

	/**
	 * @return the data-param-showAnimationButton (default: true)
	 */
	public boolean getDataParamShowAnimationButton() {
		return getBoolDataParam("showAnimationButton", true);
	}

	/**
	 * @return pixel distance from point that counts as hit, defaults to
	 *         {@link App#DEFAULT_THRESHOLD}
	 */
	public int getDataParamCapturingThreshold() {
		return getIntDataParam("capturingThreshold", App.DEFAULT_THRESHOLD);
	}

	/**
	 * eg "de"
	 * 
	 * @return the data-param-showResetIcon (default: null)
	 */
	public String getDataParamLanguage() {
		return getStringDataParam("language", "");
	}

	/**
	 * eg "AT"
	 * 
	 * @return the data-param-showResetIcon (default: null)
	 */
	public String getDataParamCountry() {
		return getStringDataParam("country", "");
	}

	/**
	 * 
	 * @return the data-param-useBrowserForJS (default: false)
	 */
	public boolean getDataParamUseBrowserForJS() {
		return getBoolDataParam("useBrowserForJS", false);
	}

	/**
	 *
	 * @return the data-param-preloadModules (comma separated) (default: null)
	 */
	public String[] getDataParamPreloadModules() {
		if (!hasAttribute("preloadModules")) {
			return null;
		}

		return getAttribute("preloadModules").split(",");
	}

	/**
	 * @return the data-param-enableShiftDragZoom (default: true)
	 */
	public boolean getDataParamShiftDragZoomEnabled() {
		return getBoolDataParam("enableShiftDragZoom", true);
	}

	/**
	 * @return integer value of the data-param-width, 0 if not present
	 */
	public int getDataParamWidth() {
		return getIntDataParam("width", 0);
	}

	/**
	 * @return integer value of the data-param-height, 0 if not present
	 */
	public int getDataParamHeight() {
		return getIntDataParam("height", 0);
	}

	/**
	 * @return wheter the applet should fit to screen
	 */
	public boolean getDataParamFitToScreen() {
		return getBoolDataParam("fittoscreen", false) || getDataParamApp();
	}

	/**
	 * @return border color (valid CSS color)
	 */
	public String getDataParamBorder() {
		return getStringDataParam("borderColor", "");
	}

	/**
	 * @return the data-param-showLogging (default: false)
	 */
	public boolean getDataParamShowLogging() {
		return getBoolDataParam("showLogging", false)
				|| (Window.Location.getParameter("GeoGebraDebug") != null);
	}

	/**
	 * @return true if debug graphics
	 */
	public boolean isDebugGraphics() {
		return "graphics".equals(getStringDataParam("showLogging", "false"));
	}

	/**
	 * @return the data-param-allowSymbolTable (default: true)
	 */
	public boolean getDataParamAllowSymbolTable() {
		return getBoolDataParam("allowSymbolTable", true);
	}

	/**
	 * @return default false
	 */
	public boolean getDataParamAllowStyleBar() {
		return getBoolDataParam("allowStyleBar", false);
	}

	/**
	 * @return whether to use app mode (forces fit to screen and most UIs
	 *         visible)
	 */
	public boolean getDataParamApp() {
		return getBoolDataParam("app", false);
	}

	/**
	 * Running in screenshot generator mode allows some optimizations
	 * 
	 * @return whether we are running the applet as screenshot generator
	 */
	public boolean getDataParamScreenshotGenerator() {
		return getBoolDataParam("screenshotGenerator", false);
	}

	/**
	 * @return look and feel
	 */
	public String getDataParamLAF() {
		return getStringDataParam("laf", "");
	}

	/**
	 * @return whether focus prevented (use in multiple applets)
	 */
	public boolean preventFocus() {
		return getBoolDataParam("preventFocus", false);
	}

	/**
	 * @return client ID for API
	 */
	public String getDataClientID() {
		return getStringDataParam("clientid", "");
	}

	/**
	 * @return perspective
	 */
	public String getDataParamPerspective() {
		return getStringDataParam("perspective", "");
	}

	/**
	 * @return graphing, geometry or classic; defaults to classic
	 */
	public String getDataParamAppName() {
		return getStringDataParam("appName", "classic")
				.replace("whiteboard", "notes").toLowerCase(Locale.US);
	}

	/**
	 * @return data-param-scale: the parameter for CSS scaling
	 */
	public double getDataParamScale() {
		return getDoubleDataParam("scale", 1);
	}

	/**
	 * @return whether to add shadows to buttons
	 */
	public boolean getDataParamButtonShadows() {
		return getBoolDataParam("buttonShadows", false);
	}

	/**
	 * @return data-param-buttonRounding: the parameter for how rounded buttons
	 *         are (0-1)
	 */
	public double getDataParamButtonRounding() {
		return getDoubleDataParam("buttonRounding", 0.2);
	}

	/**
	 * @return data-param-buttonBorderColor: the parameter for
	 */
	public String getDataParamButtonBorderColor() {
		return getStringDataParam("buttonBorderColor", null);
	}

	/**
	 * @return data-param-prerelease: whether to use some beta features
	 */
	public boolean getDataParamPrerelease() {
		return getBoolDataParam("prerelease", false);
	}

	/**
	 * @return material ID (or sharing code) to open on startup
	 */
	public String getDataParamTubeID() {
		return getStringDataParam("tubeid", "");
	}

	/**
	 * @param def
	 *            fallback if parameter not set
	 * @return whether to show "welcome" tooltip
	 */
	public boolean getDataParamShowStartTooltip(boolean def) {
		return getBoolDataParam("showTutorialLink", def);
	}

	/**
	 * @return whether to enable file menu
	 */
	public boolean getDataParamEnableFileFeatures() {
		return getBoolDataParam("enableFileFeatures", true);
	}

	/**
	 * @return whether error dialogs should be active, defaults to true
	 */
	public boolean getDataParamErrorDialogsActive() {
		return getBoolDataParam("errorDialogsActive", true);
	}

	/**
	 * @return URL of materials plaftform API (empty string if not set)
	 */
	public String getMaterialsAPIurl() {
		return getStringDataParam("materialsApi", "");
	}

	/**
	 * @return URL of materials plaftform API (empty string if not set)
	 */
	public String getLoginAPIurl() {
		return getStringDataParam("loginApi", "");
	}

	/**
	 * @return whether to allow apps picker (for classic)
	 */
	public boolean getDataParamShowAppsPicker() {
		return getBoolDataParam("showAppsPicker", false);
	}

	/**
	 * @return total thickness of borders (left + right = top + bottom)
	 */
	public int getBorderThickness() {
		return getDataParamFitToScreen() ? 0 : 2;
	}

	/**
	 * @return whether to show zoom buttons, defaults to false
	 */
	public boolean getDataParamShowZoomButtons() {
		return getBoolDataParam("showZoomButtons", false);
	}

	/**
	 * @return whether to show fullscreen button, defaults to false
	 */
	public boolean getDataParamShowFullscreenButton() {
		return getBoolDataParam("showFullscreenButton", false);
	}

	/**
	 * @return how much space should be left above the applet in fit-to-screen
	 *         mode
	 */
	public int getDataParamMarginTop() {
		return getIntDataParam("marginTop", 0);
	}

	/**
	 * @return seed for random number generator; -1 when not set
	 */
	public int getParamRandomSeed() {
		return getIntDataParam("randomSeed", -1);
	}

	/**
	 * @return fonts dataParam
	 */
	public String getDataParamFontsCssUrl() {
		return getStringDataParam("fontscssurl", "");
	}

	/**
	 * @return scale container CSS class
	 */
	public String getParamScaleContainerClass() {
		return getStringDataParam("scaleContainerClass", "");
	}

	/**
	 * @return whether to allow scale > 1
	 */
	public boolean getParamAllowUpscale() {
		return getBoolDataParam("allowUpscale", false);
	}

	/**
	 * @return whether height should be computed automatically from width of the
	 *         container
	 */
	public boolean getParamAutoHeight() {
		return getBoolDataParam("autoHeight", false);
	}

	/**
	 * @return whether to allow changing scale
	 */
	public boolean getParamDisableAutoScale() {
		return getBoolDataParam("disableAutoScale", false);
	}

	public boolean getParamRandomize() {
		return getBoolDataParam("randomize", true);
	}

	/**
	 * @return URL of Shibboleth login
	 */
	public String getParamLoginURL() {
		return getStringDataParam("loginURL", "");
	}

	/**
	 * @return URL of Shibboleth logout
	 */
	public String getParamLogoutURL() {
		return getStringDataParam("logoutURL", "");
	}

	/**
	 * @return URL of Shibboleth logout
	 */
	public String getParamBackendURL() {
		return getStringDataParam("backendURL", "");
	}

	/**
	 * @return value of data-param-fullscreeenContainer
	 */
	public String getParamFullscreenContainer() {
		return getStringDataParam("fullscreenContainer", "");
	}

	/**
	 * @return getDataParamAppName by default, or prefix of the share link
	 */
	public String getParamShareLinkPrefix() {
		return getStringDataParam("shareLinkPrefix", getDataParamAppName());
	}

	/**
	 * @return value of data-param-vendor
	 */
	public String getParamVendor() {
		return getStringDataParam("vendor", "");
	}

	/**
	 * @return value of data-param-evaluatorFontSize, or def if not specified
	 */
	public int getParamFontSize(int def) {
		return getIntDataParam("fontSize", def);
	}

	/**
	 * @return value of data-param-evaluatorKeyboard (e.g. scientific), or def if not specified
	 */
	public String getParamKeyboardType(String def) {
		return getStringDataParam("keyboardType", def);
	}

	/**
	 * @return whether the editor should work in text mode (evaluator app only)
	 */
	public boolean getParamTextMode() {
		return getBoolDataParam("textMode", false);
	}

	/**
	 * @return the background color of the evaluator app
	 */
	public String getDataParamEditorBackgroundColor() {
		return getStringDataParam("editorBackgroundColor", "white");
	}

	/**
	 * @return the text color of the evaluator app
	 */
	public String getDataParamEditorForegroundColor() {
		return getStringDataParam("editorForegroundColor", "black");
	}

	/**
	 * @return whether to show slides panel
	 */
	public boolean getParamShowSlides() {
		return getBoolDataParam("showSlides", getDataParamApp());
	}
}