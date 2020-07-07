package org.geogebra.web.html5.util;

import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;

import com.google.gwt.dom.client.Element;

/**
 *
 */
public interface ArticleElementInterface {
	/** default applet ID */
	String DEFAULT_APPLET_ID = "ggbApplet";
	/**
	 * Clear the content of this element
	 */
	void clear();

	/**
	 * @return the data-param-id article attribute as String if set else
	 *         AppWeb.DEFAULT_APPLET_ID
	 */
	String getDataParamId();

	/**
	 * @return the data-param-filename article attribute as String if set else
	 *         empty String
	 */
	String getDataParamFileName();

	/**
	 * @return data-param-json (string encoded ZIP file stucture)
	 */
	String getDataParamJSON();

	/**
	 * Determines if the "data-param-enableLabelDrags" article attribute is set
	 * to true
	 * 
	 * @return the data-param-enableLabelDrags (default: true)
	 */
	boolean getDataParamEnableLabelDrags();

	/**
	 * Determines if the "data-param-enableUndoRedo" article attribute is set to
	 * true
	 * 
	 * @return the data-param-enableUndoRedo (default: true)
	 */
	boolean getDataParamEnableUndoRedo();

	/**
	 * Determines if the "data-param-enableRightClick" article attribute is set
	 * to true
	 * 
	 * @return the data-param-enableRightClick (default: true)
	 */
	boolean getDataParamEnableRightClick();

	/**
	 * @param def
	 *            fallback if parameter not set
	 * @return data-param-enableCAS: whether CAS is enabled
	 */
	boolean getDataParamEnableCAS(boolean def);

	/**
	 * @param def
	 *            fallback if parameter not set
	 * @return data-param-enable3D: whether 3D is enabled
	 */
	boolean getDataParamEnable3D(boolean def);

	/**
	 * @param def
	 *            fallback if parameter not set
	 * @return data-param-enableGraphing: whether graphing, commands and vectors
	 *         are enabled
	 */
	boolean getDataParamEnableGraphing(boolean def);

	/**
	 * @return true, if there is data-param-enableGraphing attribute, and it is
	 *         not an empty string
	 */
	boolean hasDataParamEnableGraphing();

	/**
	 * @return rounding; consists of integer and suffix that determines whether
	 *         significant figures are used (s) and whether fractions are
	 *         prefered (r)
	 */
	String getDataParamRounding();

	/**
	 * @return the data-param-ggbbase64 article attribute as String if set else
	 *         empty String
	 */
	String getDataParamBase64String();

	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return the data-param-showMenuBar (default: false)
	 */
	boolean getDataParamShowMenuBar(boolean def);

	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return data-param-allowStylebar: whether to have stylebar; no effect
	 *         when menu is present
	 */
	boolean getDataParamAllowStyleBar(boolean def);

	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return the data-param-showToolBar (default: false)
	 */
	boolean getDataParamShowToolBar(boolean def);

	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return whether to show toolbar help (tooltips)
	 */
	boolean getDataParamShowToolBarHelp(boolean def);

	/**
	 * 
	 * @return the data-param-customToolBar (default: null)
	 */
	String getDataParamCustomToolBar();

	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return the data-param-showAlgebraInput (default: true)
	 */
	boolean getDataParamShowAlgebraInput(boolean def);

	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return input position (top / bottom / AV)
	 */
	InputPosition getAlgebraPosition(InputPosition def);

	/**
	 * @return the data-param-showResetIcon (default: false)
	 */
	boolean getDataParamShowResetIcon();

	/**
	 * @return the data-param-showAnimationButton (default: true)
	 */
	boolean getDataParamShowAnimationButton();

	/**
	 * @return pixel distance from point that counts as hit, defaults to
	 *         {@link App#DEFAULT_THRESHOLD}
	 */
	int getDataParamCapturingThreshold();

	/**
	 * eg "de"
	 * 
	 * @return the data-param-showResetIcon (default: null)
	 */
	String getDataParamLanguage();

	/**
	 * 
	 * eg "AT"
	 * 
	 * @return the data-param-showResetIcon (default: null)
	 */
	String getDataParamCountry();

	/**
	 * 
	 * @return the data-param-useBrowserForJS (default: false)
	 */
	boolean getDataParamUseBrowserForJS();

	/**
	 *
	 * @return the data-param-preloadModules (comma separated) (default: null)
	 */
	String[] getDataParamPreloadModules();

	/**
	 * @return the data-param-enableShiftDragZoom (default: true)
	 */
	boolean getDataParamShiftDragZoomEnabled();

	/**
	 * @return integer value of the data-param-width, 0 if not present
	 */
	int getDataParamWidth();

	/**
	 * @return integer value of the data-param-height, 0 if not present
	 */
	int getDataParamHeight();

	/**
	 * @return wheter the applet should fit to screen
	 */
	boolean getDataParamFitToScreen();

	/**
	 * @return border color (valid CSS color)
	 */
	String getDataParamBorder();

	/**
	 * @return the data-param-showLogging (default: false)
	 */
	boolean getDataParamShowLogging();

	/**
	 * @return true if debug graphics
	 */
	boolean isDebugGraphics();

	/**
	 * @return the data-param-allowSymbolTable (default: true)
	 */
	boolean getDataParamAllowSymbolTable();

	/**
	 * 
	 * @return that the article element has (inherited) direction attribute
	 */
	boolean isRTL();

	/**
	 * @return get CSS scale of parent element
	 */
	double getParentScaleX();

	/**
	 * Read scale value and cache it
	 * 
	 * @return the CSS scale attached to the article element
	 */
	double getScaleX();

	/**
	 * Read cached scale value or compute it, do not cache it
	 * 
	 * @return the CSS scale attached to the article element
	 */
	double readScaleX();

	/**
	 * @return the CSS scale attached to the article element
	 * 
	 */
	double getScaleY();

	/**
	 * @return default false
	 */
	boolean getDataParamAllowStyleBar();

	/**
	 * @return whether to use app mode (forces fit to screen and most UIs
	 *         visible)
	 */
	boolean getDataParamApp();

	/**
	 * Running in screenshot generator mode allows some optimizations
	 * 
	 * @return whether we are running the applet as screenshot generator
	 */
	boolean getDataParamScreenshotGenerator();

	/**
	 * @return look and feel
	 */
	String getDataParamLAF();

	/**
	 * @return whether focus prevented (use in multiple applets)
	 */
	boolean preventFocus();

	/**
	 * @return client ID for API
	 */
	String getDataClientID();

	/**
	 * @return perspective
	 */
	String getDataParamPerspective();

	/**
	 * @return graphing, geometry or classic; defaults to classic
	 */
	String getDataParamAppName();

	/**
	 * @return data-param-scale: the parameter for CSS scaling
	 */
	double getDataParamScale();

	/**
	 * @return whether to add shadows to buttons
	 */
	boolean getDataParamButtonShadows();

	/**
	 * @return data-param-buttonRounding: the parameter for how rounded buttons
	 *         are (0-1)
	 */
	double getDataParamButtonRounding();

	/**
	 * @return data-param-buttonBorderColor: the parameter for
	 */
	String getDataParamButtonBorderColor();

	/**
	 * Remove cached scale values
	 * 
	 * @param parentScale
	 *            new scale of scaler element
	 */
	void resetScale(double parentScale);

	/**
	 * @return data-param-prerelease: whether to use some beta features
	 */
	boolean getDataParamPrerelease();

	/**
	 * @return material ID (or sharing code) to open on startup
	 */
	String getDataParamTubeID();

	/**
	 * @param def
	 *            fallback if parameter not set
	 * @return whether to show "welcome" tooltip
	 */
	boolean getDataParamShowStartTooltip(boolean def);

	/**
	 * @return whether to enable file menu
	 */
	boolean getDataParamEnableFileFeatures();

	/**
	 * Set the ID of this article to something unique; prefer getDataParamId,
	 * append number in case of conflicts. If not set, use a string that
	 * contains i
	 * 
	 * @param i
	 *            number for id if fdataParamId not set
	 */
	void initID(int i);

	/**
	 * @return whether error dialogs should be active, defaults to true
	 */
	boolean getDataParamErrorDialogsActive();

	/**
	 * @return URL of materials plaftform API (empty string if not set)
	 */
	String getMaterialsAPIurl();

	/**
	 * @return URL of materials plaftform API (empty string if not set)
	 */
	String getLoginAPIurl();

	/**
	 * @return whether to allow apps picker (for classic)
	 */
	boolean getDataParamShowAppsPicker();

	/**
	 * @return total thickness of borders (left + right = top + bottom)
	 */
	int getBorderThickness();

	/**
	 * @return whether to show zoom buttons, defaults to false
	 */
	boolean getDataParamShowZoomButtons();

	/**
	 * @return whether to show fullscreen button, defaults to false
	 */
	boolean getDataParamShowFullscreenButton();

	/**
	 * @return whether suggestions buttons should be shown; default true if not
	 *         set
	 */
	boolean getDataParamShowSuggestionButtons();

	/**
	 * @return how much space should be left above the applet in fit-to-screen
	 *         mode
	 */
	int getDataParamMarginTop();

	/**
	 * @return fonts dataParam
	 */
	String getDataParamFontsCssUrl();

	/**
	 * @return id
	 */
	String getId();

	/**
	 * @param attributeName
	 *            attribute name
	 * @param value
	 *            value of attribute
	 * @return this
	 */
	ArticleElementInterface attr(String attributeName, String value);

	/**
	 * @param string
	 *            attribute which should be removed
	 */
	void removeAttribute(String string);

	/**
	 * @return parent element
	 */
	Element getParentElement();

	/**
	 * @return element
	 */
	Element getElement();

	/**
	 * @return scale container CSS class
	 */
	String getParamScaleContainerClass();

	/**
	 * @return whether to allow scale > 1
	 */
	boolean getParamAllowUpscale();

	/**
	 * @return whether to allow changing scale
	 */
	boolean getParamDisableAutoScale();

	/**
	 * @return seed for random number generator; -1 when not set
	 */
	int getParamRandomSeed();

	/**
	 * @return URL of Shibboleth login
	 */
	String getParamLoginURL();

	/**
	 * @return URL of Shibboleth logout
	 */
	String getParamLogoutURL();

	/**
	 * @return URL of Shibboleth logout
	 */
	String getParamBackendURL();

	/**
	 * @return whether height should be computed automatically from width of the
	 *         container
	 */
	boolean getParamAutoHeight();

	/**
	 * @return value of data-param-fullscreeenContainer
	 */
	String getParamFullscreenContainer();

	/**
	 * @return getDataParamAppName by default, or prefix of the share link
	 */
	String getParamShareLinkPrefix();

	/**
	 * @return value of data-param-vendor
	 */
	String getParamVendor();

	/**
	 * @return value of data-param-evaluatorFontSize, or def if not specified
	 */
	int getParamFontSize(int def);

	/**
	 * @return value of data-param-evaluatorKeyboard (e.g. scientific), or def if not specified
	 */
	String getParamKeyboardType(String def);
}