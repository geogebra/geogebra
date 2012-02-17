package geogebra.web.html5;

import com.google.gwt.core.client.GWT;
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
	 * @return the data-param-filename article attribute as String if set else empty String
	 */
	public String getDataParamFileName() {
		return (this.getAttribute("data-param-filename") != null) ? this
		        .getAttribute("data-param-filename") : "";
	}

	/**
	 * Determines if the "data-param-gui" article attribute is set to true
	 * 
	 * @return true if "data-param-gui" is set to true else false
	 */
	public boolean getDataParamGui() {
		return (this.getAttribute("data-param-gui") != null) ? this
		        .getAttribute("data-param-gui").equals("true") : false;
	}


	/**
	 * @return the data-param-ggbbase64 article attribute as String if set else empty String
	 */
	public String getDataParamBase64String() {
		return (this.getAttribute("data-param-ggbbase64") != null) ? this
		        .getAttribute("data-param-ggbbase64") : "";
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
}
