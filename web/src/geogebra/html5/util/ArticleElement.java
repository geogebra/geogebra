package geogebra.html5.util;

import geogebra.html5.main.AppWeb;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TagName;
import com.google.gwt.user.client.ui.Widget;

@TagName(ArticleElement.TAG)
public final class ArticleElement extends Element {

	static final String TAG = "article";

	/**
	 * @param element
	 *            Assert, that the given {@link Element} is compatible with this
	 *            class and automatically typecast it.
	 * @return
	 */
	public static ArticleElement as(Element element) {
		//assert element.getTagName().equalsIgnoreCase(TAG);
		return (ArticleElement) element;
	}

	protected ArticleElement() {
	}

	public void add(Widget w) {
		this.appendChild(w.getElement());
	}

	public void clear() {
		for (int i = 0; i < this.getChildCount(); i++) {
			this.removeChild(this.getChild(i));
		}

	}

	public boolean remove(Widget w) {
		for (int i = 0; i < this.getChildCount(); i++) {
			if (this.getChild(i).equals(w.getElement())) {
				this.removeChild(this.getChild(i));
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the data-param-id article attribute as String if set else AppWeb.DEFAULT_APPLET_ID
	 */
	public String getDataParamId() {
		String ret = this.getAttribute("data-param-id");
		if ((ret == "") || !ret.matches("[A-Za-z0-9]+"))
			return AppWeb.DEFAULT_APPLET_ID;
		return ret;
	}
	
	/** 
	* @return the data-param-filename article attribute as String if set else empty String 
	*/ 
	public String getDataParamFileName() { 
		String ret = this.getAttribute("data-param-filename"); 
		return (ret != null) ? ret : ""; 
	}

	/**
	 * Determines if the "data-param-guiOff" article attribute is set to true
	 * If it is set to true, the GuiManager should never be created,
	 * and only a single Graphics View should show in web applets,
	 * regardless of what is there in the ggb file's construction.
	 * This is for speedup, but its drawback is that it should be
	 * decided before the AppW is created.
	 * 
	 * @return the data-param-guiOff (default: false)
	 */
	public boolean getDataParamGuiOff() {
		return ("true".equals(this.getAttribute("data-param-guiOff")));
	}

	/**
	 * Determines if the "data-param-enableLabelDrags" article attribute is set to true
	 * 
	 * @return the data-param-enableLabelDrags (default: true)
	 */
	public boolean getDataParamEnableLabelDrags() {
		return (!"false".equals(this.getAttribute("data-param-enableLabelDrags")));
	}

	/**
	 * Determines if the "data-param-enableRightClick" article attribute is set to true
	 * 
	 * @return the data-param-enableRightClick (default: true)
	 */
	public boolean getDataParamEnableRightClick() {
		return (!"false".equals(this.getAttribute("data-param-enableRightClick")));
	}


	/**
	 * @return the data-param-ggbbase64 article attribute as String if set else empty String
	 */
	public String getDataParamBase64String() {
		String ret = this.getAttribute("data-param-ggbbase64");
		return ( ret != null) ? ret : "";
	}
	
	/**
	 * @return the data-param-showMenuBar (default: false)
	 */
	public boolean getDataParamShowMenuBar() {
		return ("true".equals(this.getAttribute("data-param-showMenuBar")));
	}

	/**
	 * @return the data-param-showMenuBar (default: true)
	 */
	public boolean getDataParamShowMenuBarDefaultTrue() {
		return (!"false".equals(this.getAttribute("data-param-showMenuBar")));
	}
	
	/**
	 * @return the data-param-showToolBar (default: false)
	 */
	public boolean getDataParamShowToolBar() {
		return ("true".equals(this.getAttribute("data-param-showToolBar")));
	}

	/**
	 * @return the data-param-showToolBar (default: true)
	 */
	public boolean getDataParamShowToolBarDefaultTrue() {
		return (!"false".equals(this.getAttribute("data-param-showToolBar")));
	}
	
	/**
	 * 
	 * @return the data-param-customToolBar (default: null)
	 */
	public String getDataParamCustomToolBar() {
		return this.getAttribute("data-param-customToolBar");
	}
	
	/**
	 * @return the data-param-showAlgebraInput (default: false)
	 */
	public boolean getDataParamShowAlgebraInput() {
		return ("true".equals(this.getAttribute("data-param-showAlgebraInput")));
	}

	/**
	 * @return the data-param-showAlgebraInput (default: true)
	 */
	public boolean getDataParamShowAlgebraInputDefaultTrue() {
		return (!"false".equals(this.getAttribute("data-param-showAlgebraInput")));
	}

	/**
	 * @return the data-param-showResetIcon (default: false)
	 */
	public boolean getDataParamShowResetIcon() {
		return ("true".equals(this.getAttribute("data-param-showResetIcon")));
	}
	
	/**
	 * @return the data-param-showAnimationButton (default: true)
	 */
	public boolean getDataParamShowAnimationButton() {
		return (!"false".equals(this.getAttribute("data-param-showAnimationButton")));
	}
	
	/**
	 * eg "de"
	 * 
	 * @return the data-param-showResetIcon (default: null)
	 */
	public String getDataParamLanguage() {
		return this.getAttribute("data-param-language");
	}
	
	/**
	 * 
	 * eg "AT"
	 * 
	 * @return the data-param-showResetIcon (default: null)
	 */
	public String getDataParamCountry() {
		return this.getAttribute("data-param-country");
	}

	/**
	 * 
	 * @return the data-param-allowJSscripting (default: true)
	 */
	public boolean getDataParamUseBrowserForJS() {
		return (!"false".equals(this.getAttribute("data-param-useBrowserForJS")));
	}
	
	
	
	/**
	 * @return the data-param-enableShiftDragZoom (default: true)
	 */
	public boolean getDataParamShiftDragZoomEnabled() {
		return (!"false".equals(this.getAttribute("data-param-enableShiftDragZoom")));
	}
	

	/**
	 * @return integer value of the data-param-width, 0 if not present
	 */
	public int getDataParamWidth() {
		String width = this.getAttribute("data-param-width");
	    return (width != null && !width.equals("")) ? Integer.parseInt(width, 10)  : 0; 
    }

	/**
	 * @return integer value of the data-param-height, 0 if not present
	 */
	public int getDataParamHeight() {
		String height = this.getAttribute("data-param-height");
		return (height != null && !height.equals("")) ? Integer.parseInt(height, 10)  : 0; 
	}

	public String getDataParamBorder() {
		return this.getAttribute("data-param-borderColor");
	}

	/**
	 * @return the data-param-showLogging (default: false)
	 */
	public boolean getDataParamShowLogging() {
		return ("true".equals(this.getAttribute("data-param-showLogging")));
	}
	
	/**
	 * @return the data-param-allowSymbolTable (default: true)
	 */
	public boolean getDataParamAllowSymbolTable() {
		return (!"false".equals(this.getAttribute("data-param-allowSymbolTable")));
	}
	
	/**
	 * 
	 * @return that the article element has (inherited) direction attribute
	 */
	public native boolean isRTL() /*-{
		var style;
		if ($wnd.getComputedStyle) {
			style = $wnd.getComputedStyle(this);
			return style.direction === "rtl";
		} else if (this.currentStyle) {
			return this.currentStyle.direction === "rtl";
		}
		return false;
	 }-*/;
	
	private static native String getTransform(JavaScriptObject style) /*-{
	return 	style.transform ||
			style.webkitTransform ||
			style.MozTransform ||
			style.msTransform ||
			style.oTransform ||
			"";
	}-*/;


	
	private float getEnvScaleX() {		
		return envScale("x");
	};
	
	
	private float getEnvScaleY() {
		return envScale("y");
	}

	private native float envScale(String type) /*-{
		var matrixRegex = /matrix\((-?\d*\.?\d+),\s*0,\s*0,\s*(-?\d*\.?\d+),\s*0,\s*0\)/,
			style = $wnd.getComputedStyle(this),
			transform,
			matches;
		if (style) {
			transform = @geogebra.html5.util.ArticleElement::getTransform(Lcom/google/gwt/core/client/JavaScriptObject;)(style),
			matches = transform.match(matrixRegex); 
			if (matches && matches.length) {
				if (type === "x") {
					return $wnd.parseFloat(matches[1]);
				} else {
					return $wnd.parseFloat(matches[2]);
				}
		   	} else if (transform.indexOf("scale") === 0) {
		   		return $wnd.parseFloat(transform.substr(transform.indexOf("(") + 1));
			}
		   		
		}
		return 1;		
	}-*/;
	
	/**
	 * @return the CSS scale attached to the article element
	 */
	public float getScaleX() {
		//no instance fields in subclasses of Element, so no way to asign it to a simple field
		if ("".equals(this.getAttribute("data-scaley"))) {
			this.setAttribute("data-scaley", String.valueOf(getEnvScaleY()));
		}
		return Float.parseFloat(this.getAttribute("data-scaley"));
	}
	
	/**
	 * @return the CSS scale attached to the article element
	 * 
	 */
	public float getScaleY() {
		//no instance fields in subclasses of Element, so no way to asign it to a simple field
		if ("".equals(this.getAttribute("data-scalex"))) {
			this.setAttribute("data-scalex", String.valueOf(getEnvScaleY()));
		}
		return Float.parseFloat(this.getAttribute("data-scalex"));
	}

	
	
	
}
