package org.geogebra.web.html5.util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.bridge.AttributeProvider;
import org.geogebra.web.html5.bridge.MapAttributeProvider;

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
		this.attributeProvider = new MapAttributeProvider(null);
		setAttribute("appName", appName);
		setAttribute("width", "800");
		setAttribute("height", "600");
	}

	public AppletParameters(AttributeProvider element) {
		this.attributeProvider = element;
	}

	private String getAttribute(String attribute) {
		return attributeProvider.getAttribute(attribute);
	}

	/**
	 * @param attribute attribute name
	 * @return whether attribute is set
	 */
	public boolean hasAttribute(String attribute) {
		return attributeProvider.hasAttribute(attribute);
	}

	/**
	 * Remove an attribute.
	 * @param attribute attribute name
	 */
	public void removeAttribute(String attribute) {
		attributeProvider.removeAttribute(attribute);
	}

	/**
	 * @param attribute attribute name
	 * @param value attribute value
	 * @return this
	 */
	public AppletParameters setAttribute(String attribute, String value) {
		attributeProvider.setAttribute(attribute, value);
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
	 * @return data-param-json (string encoded ZIP file structure)
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
	 * @return value of data-param-examMode or null
	 */
	public String getParamFeatureSet() {
		return getStringDataParam("featureSet", null);
	}

	public boolean getParamExamMode() {
		return getBoolDataParam("examMode", false);
	}

	/**
	 * @return whether enable3D is set
	 */
	public boolean hasDataParamEnable3D() {
		return hasAttribute("enable3D");
	}

	/**
	 * @return rounding; consists of integer and suffix that determines whether
	 *         significant figures are used (s) and whether fractions are
	 *         preferred (r)
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
	 * @param def fallback if parameter is not set
	 * @return true in full apps, or non-notes applets with show menu set.
	 *  data-param-showToolBar otherwise, with fallback def
	 */
	public boolean getDataParamShowToolBar(boolean def) {
		if (getDataParamShowMenuBar(false) && !"notes".equals(getDataParamAppName())
				|| getDataParamApp()) {
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
				.toLowerCase(Locale.ROOT).trim();
		if ("top".equals(pos)) {
			return InputPosition.top;
		}
		if ("bottom".equals(pos)) {
			return InputPosition.bottom;
		}
		if (!pos.isEmpty()) {
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
	 * @return whether the applet should fit to screen
	 */
	public boolean getDataParamFitToScreen() {
		return getBoolDataParam("fittoscreen", false) || getDataParamApp();
	}

	/**
	 * @param fallback border color if none is set
	 * @return border color (valid CSS color)
	 */
	public String getDataParamBorder(String fallback) {
		return getStringDataParam("borderColor", fallback);
	}

	/**
	 * @return the data-param-showLogging (default: false)
	 */
	public boolean getDataParamShowLogging() {
		return getBoolDataParam("showLogging", false)
				|| (NavigatorUtil.getUrlParameter("GeoGebraDebug") != null);
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
	public String getMaterialId() {
		return getStringDataParam("material_id", "");
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
	 * @return whether to allow scale &gt; 1
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
	 * @return value of data-param-fullscreenContainer
	 */
	public String getParamFullscreenContainer() {
		return getStringDataParam("fullscreenContainer", "");
	}

	/**
	 * @return getDataParamAppName by default, or prefix of the share link
	 */
	public String getParamShareLinkPrefix() {
		String dataParam = getStringDataParam("shareLinkPrefix", getDataParamAppName());
		if (GeoGebraConstants.SUITE_APPCODE.equals(dataParam)) {
			dataParam = GeoGebraConstants.SUITE_URL_NAME;
		}
		return dataParam;
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

	/**
	 * @return whether to localize digits (e.g. for Arabic)
	 */
	public boolean getParamUseLocalizedDigits() {
		return getBoolDataParam("useLocalizedDigits", false);
	}

	/**
	 * @return whether to localize point names
	 */
	public boolean getParamUseLocalizedPointNames() {
		return getBoolDataParam("useLocalizedPointNames", true);
	}

	/**
	 * @return value of detachKeyboard parameter, defaulting to "auto"
	 */
	public String getParamDetachKeyboard() {
		return getStringDataParam("detachKeyboard", "auto");
	}

	/**
	 * @param fallback default value
	 * @return value of showKeyboardOnFocus parameter
	 */
	public String getParamShowKeyboardOnFocus(String fallback) {
		return getStringDataParam("showKeyboardOnFocus", fallback);
	}

	/**
	 * @return whether to use app mode (forces fit to screen and most UIs
	 *         visible)
	 */
	public boolean getDataParamTransparentGraphics() {
		return getBoolDataParam("transparentGraphics", false);
	}

	/**
	 * @param mobile whether we're on a mobile device
	 * @return whether to use ASCII
	 */
	public boolean getParamScreenReaderMode(boolean mobile) {
		String mode = getStringDataParam("screenReaderMode", "");
		if ("ascii".equalsIgnoreCase(mode)) {
			return true;
		} else if ("unicode".equalsIgnoreCase(mode)) {
			return false;
		} else {
			return mobile;
		}
	}

	public int getParamMaxImageSize() {
		return getIntDataParam("maxImageSize", 0);
	}

	public String getParamMultiplayerUrl() {
		return getStringDataParam("multiplayerUrl", "");
	}

	public boolean getParamAllowUndoCheckpoints() {
		return getBoolDataParam("allowUndoCheckpoints", true);
	}

	public double getBorderRadius() {
		return getIntDataParam("borderRadius", 0);
	}

	/**
	 * @return if scripting in JavaScript is disabled (default: false)
	 */
	public boolean getDisableJavaScript() {
		return getBoolDataParam("disableJavaScript", false);
	}

	/**
	 * When set, keyboard should be attached to the first element in DOM
	 * that fits the selector.
	 *
	 * @return the selector where the keyboard should be attached in DOM.
	 */
	public String getDetachKeyboardParent() {
		return getStringDataParam("detachedKeyboardParent", "");
	}

	/**
	 * @return whether to run JS in QuickJS sandbox
	 */
	public boolean getParamSandbox() {
		return getBoolDataParam("sandboxJavaScript",
				getDataParamApp() || Browser.isGeoGebraOrg());
	}

	/**
	 * @return Url for the location of the web fonts
	 */
	public String getParamWebfontsUrl() {
		return getStringDataParam("webfonts", "");
	}

	/**
	 * List of disabled categories in ToolboxMow
	 * @return the data-param-customToolbox (default: empty list)
	 */
	public List<String> getDataParamCustomToolbox() {
		return Arrays.stream(getStringDataParam("customToolbox", "").split(","))
				.map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
	}

	public double getMaxHeight() {
		return getIntDataParam("maxHeight", -1);
	}

	/**
	 * @param fallback value to be used if not set
	 * @return value of the data-param-fontAwesome attribute
	 */
	public boolean getParamFontAwesome(boolean fallback) {
		return getBoolDataParam("fontAwesome", fallback);
	}

	/**
	 * @return exam launch URL
	 */
	public String getParamExamLaunchURL() {
		return getStringDataParam("examLaunchURL", "");
	}

	public String getParamExternalControls() {
		return getStringDataParam("externalControls", "");
	}

	/**
	 * @return initial sub-app code for Suite
	 */
	public String getParamSubApp() {
		return getStringDataParam("subApp", "");
	}
}