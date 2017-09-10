/*
 * Copyright 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dom.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.DOMImplStandardBase.ClientRect;

/**
 * WebKit based implementation of {@link com.google.gwt.dom.client.DOMImplStandardBase}.
 */
class DOMImplWebkit extends DOMImplStandardBase {
	  private static class GClientRect extends JavaScriptObject {
		    
		    protected GClientRect() {
		    }

		    public final int getLeft() {
		      return toInt32(getSubPixelLeft());
		    }

		    public final int getTop() {
		      return toInt32(getSubPixelTop());
		    }

		    private final native double getSubPixelLeft() /*-{
				return this.left;
			}-*/;

		    private final native double getSubPixelTop() /*-{
				return this.top;
			}-*/;
		  }
  /**
   * Return true if using Webkit 525.x (Safari 3) or earlier.
   * 
   * @return true if using Webkit 525.x (Safari 3) or earlier.
   */
  private static native boolean isWebkit525OrBefore() /*-{
	var result = /safari\/([\d.]+)/.exec(navigator.userAgent.toLowerCase());
	if (result) {
		var version = (parseFloat(result[1]));
		if (version < 526) {
			return true;
		}
	}
	return false;
}-*/;

  /**
   * Webkit events sometimes target the text node inside of the element instead
   * of the element itself, so we need to get the parent of the text node.
   */
  @Override
  public native EventTarget eventGetTarget(NativeEvent evt) /*-{
	var target = evt.target;
	if (target && target.nodeType == 3) {
		target = target.parentNode;
	}
	return target;
}-*/;

  /**
   * Webkit based browsers require that we set the webkit-user-drag style
   * attribute to make an element draggable.
   */
  @Override
  public void setDraggable(Element elem, String draggable) {
    super.setDraggable(elem, draggable);
    if ("true".equals(draggable)) {
      elem.getStyle().setProperty("webkitUserDrag", "element");
    } else {
      elem.getStyle().clearProperty("webkitUserDrag");
    }
  }

  public Element getLegacyDocumentScrollingElement(Document doc) {
    // Old WebKit needs body.scrollLeft in both quirks mode and strict mode.
    return doc.getBody();
  }
  
  public void setScrollLeft(Document doc, int left) {
	    ensureDocumentScrollingElement(doc).setScrollLeft(left);
	  }
  
  public void setScrollTop(Document doc, int top) {
	    ensureDocumentScrollingElement(doc).setScrollTop(top);
	  }
  
  public int getScrollLeft(Document doc) {
	    return ensureDocumentScrollingElement(doc).getScrollLeft();
	  }
  
  public int getScrollTop(Document doc) {
	    return ensureDocumentScrollingElement(doc).getScrollTop();
	  }
  
  private Element ensureDocumentScrollingElement(Document document) {
	    // In some case (e.g SVG document and old Webkit browsers), getDocumentScrollingElement can
	    // return null. In this case, default to documentElement.
	    Element scrollingElement = getDocumentScrollingElement(document);
	    return scrollingElement != null ? scrollingElement : document.getDocumentElement();
	  }

  Element getDocumentScrollingElement(Document doc) {
	    // Uses http://dev.w3.org/csswg/cssom-view/#dom-document-scrolling element to
	    // avoid trying to guess about browser behavior.
	    if (getNativeDocumentScrollingElement(doc) != null) {
	      return getNativeDocumentScrollingElement(doc);
	    }

	    return getLegacyDocumentScrollingElement(doc);
	  }
  
  final native Element getNativeDocumentScrollingElement(Document doc) /*-{
	return doc.scrollingElement;
}-*/;
	  
	  @Override
	  public int getAbsoluteLeft(Element elem) {
	    GClientRect rect = getBoundingClientRect(elem);
	    double left = rect.getSubPixelLeft()
	        + getScrollLeft(elem.getOwnerDocument());
	    return toInt32(left);
	  }

	  @Override
	  public int getAbsoluteTop(Element elem) {
	    GClientRect rect = getBoundingClientRect(elem);
	    double top = rect.getSubPixelTop()
	        + getScrollTop(elem.getOwnerDocument());
	    return toInt32(top);
	  }
	  
	  private static native GClientRect getBoundingClientRect(Element element) /*-{
		return element.getBoundingClientRect && element.getBoundingClientRect();
	}-*/;
}
