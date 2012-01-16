package geogebra.web.html5;

import geogebra.common.main.AbstractApplication;


import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TagName;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

@TagName(ArticleElement.TAG)
public final class ArticleElement extends Element {
	
	static final String TAG = "article";
	
	/**
	 * @param element
	 * Assert, that the given {@link Element} is compatible with this class and
	 * automatically typecast it.
	 * @return
	 */
	public static ArticleElement as (Element element) {
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
	
	public String getDataParamFileName() {
		return (this.getAttribute("data-param-filename") != null) ? this.getAttribute("data-param-filename") : "";
	}
	
	public String getDataParamBase64String() {
		return this.getAttribute("data-param-ggbbase64");
	}

}
