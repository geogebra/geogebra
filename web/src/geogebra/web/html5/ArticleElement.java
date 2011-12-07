package geogebra.web.html5;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TagName;

@TagName(ArticleElement.TAG)
public class ArticleElement extends Element {
	
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

}
