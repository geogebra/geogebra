package geogebra.html5.main;

import geogebra.common.main.App;
import geogebra.html5.awt.GGraphics2DW;
import geogebra.html5.euclidian.EuclidianViewWeb;

import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Utility class for DrawEquationWeb. Maintains a map of elements created by
 * DrawEquationWeb and manages their visibility and removal.
 * 
 * @author G. Sturr
 * 
 */
public class DrawElementManager {

	// dummy element for a graphics canvas with no parent element
	private Element dummyParent;

	// dummy graphics environment used as a key in elementMapCollection when a
	// graphics canvas has no parent element
	private GGraphics2DW dummyG2;

	private class ElementRecord {
		public Element element = null;
		public int age = 0;

		public ElementRecord(Element element, int age) {
			this.element = element;
			this.age = age;
		}
	}

	private HashMap<GGraphics2DW, HashMap<String, ElementRecord>> elementMapCollection;

	/**
	 * Constructs new DrawElementManager
	 */
	public DrawElementManager() {

		elementMapCollection = new HashMap<GGraphics2DW, HashMap<String, ElementRecord>>();

		// Create a dummy parent element for a canvas with no parent.
		Canvas dummyCanvas = Canvas.createIfSupported();
		dummyParent = DOM.createDiv();
		dummyParent.addClassName("dummyParent");
		dummyParent.getStyle().setVisibility(Style.Visibility.HIDDEN);
		dummyParent.getStyle().setZIndex(-1);
		dummyParent.appendChild(dummyCanvas.getElement());

		RootPanel.get().getElement().appendChild(dummyParent);

		// Create a dummy graphics environment to act as a key in the
		// elementMapCollection for all cases that use the dummyParent for
		// temporary element storage.

		dummyG2 = new GGraphics2DW(dummyCanvas);
		elementMapCollection.put(dummyG2, new HashMap<String, ElementRecord>());

	}

	// =================================================================
	// Getters/Setters
	// =================================================================

	/**
	 * @param g2
	 * @param stringID
	 * @return Element from map collection using the given graphics environment
	 *         and id string as keys
	 */
	public Element getElement(GGraphics2DW g2, String stringID) {

		HashMap<String, ElementRecord> elementMap = elementMapCollection
		        .get(dummyCheck(g2));

		if (elementMap == null) {
			return null;
		}
		if (elementMap.get(stringID) == null) {
			return null;
		}
		return elementMap.get(stringID).element;
	}

	/**
	 * @param g2
	 * @return Element map using the given graphics environment as a key.
	 *         Creates a new map if none exists.
	 */
	private HashMap<String, ElementRecord> getElementMap(GGraphics2DW g2) {

		HashMap<String, ElementRecord> elementMap = elementMapCollection
		        .get(dummyCheck(g2));
		if (elementMap == null) {
			elementMap = new HashMap<String, ElementRecord>();
			elementMapCollection.put(g2, elementMap);
		}
		return elementMap;
	}

	/**
	 * Registers an element in the map using the given graphics environment and
	 * id string as keys.
	 * 
	 * @param g2
	 * @param elem
	 * @param stringID
	 * @param age
	 */
	public void registerElement(GGraphics2DW g2, Element elem, String stringID,
	        int age) {

		getElementMap(dummyCheck(g2)).put(stringID,
		        new ElementRecord(elem, age));
		// debugMapCollection();
	}

	private GGraphics2DW dummyCheck(GGraphics2DW g2) {
		if (g2.getCanvas().getElement().getParentElement() == null) {
			return dummyG2;
		}
		return g2;
	}

	public void setElementAge(GGraphics2DW g2, String stringID, int age) {
		getElementMap(g2).get(stringID).age = age;
	}

	// =================================================================
	// Element Visibility/Removal
	// =================================================================

	/**
	 * Clears all elements from a given EuclidianView by either hiding them or
	 * removing them if they have been unused for a long enough time.
	 * 
	 * Note: This also clears temporary elements attached to the dummyParent
	 * 
	 * @param ev
	 */
	public void clearLaTeXes(EuclidianViewWeb ev) {
		clearLaTeXes(ev.g2p);
		clearLaTeXes(dummyG2);
	}

	/**
	 * Clears all elements from a given graphics environment by either hiding
	 * them or removing them if they have been unused for a long enough time.
	 * 
	 * @param g2
	 */
	public void clearLaTeXes(GGraphics2DW g2) {

		// App.debug("clearing latexs");
		HashMap<String, ElementRecord> elementMap = getElementMap(g2);

		Iterator<String> it = elementMap.keySet().iterator();
		while (it.hasNext()) {

			String keyString = it.next();
			Element elem = elementMap.get(keyString).element;
			int age = elementMap.get(keyString).age;

			// App.debug("elem: " + elem.getInnerText() + "age: " + age);

			// if old enough, remove element, otherwise increment age counter
			// and hide the element
			if (age > 5) {
				elem.removeFromParent();
				elementMap.remove(keyString);
			} else {
				elementMap.get(keyString).age++;
				elem.getStyle().setDisplay(Style.Display.NONE);
			}
		}
	}

	/**
	 * Removes all elements associated with a EuclidianView.
	 * 
	 * Note: This also removes temporary elements attached to the dummyParent
	 * 
	 * @param ev
	 */
	public void deleteLaTeXes(EuclidianViewWeb ev) {
		deleteLaTeXes(ev.g2p);
		deleteLaTeXes(dummyG2);
	}

	/**
	 * Removes all elements associated with a graphics environment
	 * 
	 * @param g2
	 */
	public void deleteLaTeXes(GGraphics2DW g2) {
		// App.debug("deleting latexs");
		HashMap<String, ElementRecord> elementMap = getElementMap(g2);

		Iterator<ElementRecord> it = elementMap.values().iterator();
		while (it.hasNext()) {
			ElementRecord elemValue = it.next();
			elemValue.element.removeFromParent();
		}

		elementMap.clear();
	}

	// =================================================================
	// Parent Element
	// =================================================================

	private native Element newDocumentFragment() /*-{
		return $doc.createDocumentFragment();
	}-*/;

	/**
	 * Return the parent element a graphics environment canvas element. If a
	 * parent does not exist, a document fragment is returned instead.
	 * 
	 * @param g2
	 * @return
	 */
	public Element getParentElement(GGraphics2DW g2) {

		if (g2.getCanvas().getParent() == null) {
			// App.debug("getting dummy parent");
			return dummyParent;
		}
		return g2.getCanvas().getCanvasElement().getParentElement();
	}

	// =================================================================
	// Debug
	// =================================================================
	private void debugMapCollection() {

		App.debug("element maps: ");
		Iterator<GGraphics2DW> it = elementMapCollection.keySet().iterator();
		while (it.hasNext()) {
			GGraphics2DW g2 = it.next();
			App.debug("map size: " + getElementMap(g2).size());
		}
	}

}
