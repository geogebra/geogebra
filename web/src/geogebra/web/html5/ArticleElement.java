package geogebra.web.html5;

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
		assert element.getTagName().equalsIgnoreCase(TAG);
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
	 * @return the data-param-id article attribute as String if set else "ggbApplet"
	 */
	public String getDataParamId() {
		String ret = this.getAttribute("data-param-id");
		if ((ret == null) || !ret.matches("[A-Za-z0-9]+"))
			return "ggbApplet";
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
	 * Determines if the "data-param-gui" article attribute is set to true
	 * 
	 * @return the data-param-gui (default: false)
	 */
	public boolean getDataParamGui() {
		return ("true".equals(this.getAttribute("data-param-gui")));
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
	 * @return the data-param-showToolBar (default: false)
	 */
	public boolean getDataParamShowToolBar() {
		return ("true".equals(this.getAttribute("data-param-showToolBar")));
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
}
