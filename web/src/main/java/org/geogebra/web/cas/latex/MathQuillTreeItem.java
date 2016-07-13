package org.geogebra.web.cas.latex;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.web.gui.view.algebra.EquationEditorListener;
import org.geogebra.web.web.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.web.gui.view.algebra.ScrollableSuggestionDisplay;

import com.google.gwt.dom.client.Element;

public class MathQuillTreeItem extends RadioTreeItem
		implements EquationEditorListener {

	public MathQuillTreeItem(GeoElement geo0) {
		super(geo0);
	}

	public MathQuillTreeItem(Kernel kernel) {
		super(kernel);
	}

	/**
	 * Creates the specific tree item due to the type of the geo element.
	 * 
	 * @param geo0
	 *            the geo element which is the item for.
	 * @return The appropriate RadioTreeItem descendant.
	 */
	public static RadioTreeItem create(GeoElement geo0) {
		if (geo0.isMatrix()) {
			return new MatrixTreeItem(geo0);
		} else if (geo0.isGeoCurveCartesian()) {
			return new ParCurveTreeItem(geo0);
		} else if (geo0.isGeoFunctionConditional()) {
			return new CondFunctionTreeItem(geo0);
		}
		return new MathQuillTreeItem(geo0);
	}

	@Override
	public RadioTreeItem copy() {
		return new MathQuillTreeItem(geo);
	}

	/**
	 * This method can be used to invoke a keydown event on MathQuillGGB, e.g.
	 * key=8,alt=false,ctrl=false,shift=false will trigger a Backspace event
	 * 
	 * @param key
	 *            keyCode of the event, which is the same as "event.which", used
	 *            at keydown
	 * @param alt
	 *            boolean
	 * @param ctrl
	 *            boolean
	 * @param shift
	 *            boolean
	 */
	@Override
	public void keydown(int key, boolean alt, boolean ctrl, boolean shift) {
		if (isMinMaxPanelVisible()) {
			return;
		}
		if (commonEditingCheck()) {
			MathQuillHelper.triggerKeydown(this, latexItem.getElement(), key,
					alt, ctrl, shift);
		}
	}

	/**
	 * This method should be used to invoke a keypress on MathQuillGGB, e.g.
	 * keypress(47, false, false, false); will trigger a '/' press event... This
	 * method should be used instead of "keydown" in case we are interested in
	 * the Character meaning of the key (to be entered in a textarea) instead of
	 * the Controller meaning of the key.
	 * 
	 * @param character
	 *            charCode of the event, which is the same as "event.which",
	 *            used at keypress
	 * @param alt
	 *            boolean maybe not useful
	 * @param ctrl
	 *            boolean maybe not useful
	 * @param shift
	 *            boolean maybe not useful
	 */
	@Override
	public void keypress(char character, boolean alt, boolean ctrl,
			boolean shift, boolean more) {
		if (isMinMaxPanelVisible()) {
			return;
		}

		if (commonEditingCheck()) {
			MathQuillHelper.triggerKeypress(this, latexItem.getElement(),
					character, alt, ctrl, shift, more);
		}
	}

	/**
	 * This method can be used to invoke a keyup event on MathQuillGGB, e.g.
	 * key=13,alt=false,ctrl=false,shift=false will trigger a Enter event
	 * 
	 * @param key
	 *            keyCode of the event, which is the same as "event.which", used
	 *            at keyup
	 * @param alt
	 *            boolean
	 * @param ctrl
	 *            boolean
	 * @param shift
	 *            boolean
	 */
	@Override
	public final void keyup(int key, boolean alt, boolean ctrl, boolean shift) {
		if (isMinMaxPanelVisible()) {
			return;
		}

		if (commonEditingCheck()) {
			MathQuillHelper.triggerKeyUp(latexItem.getElement(), key, alt, ctrl,
					shift);
		}
	}

	@Override
	public final Element getLaTeXElement() {
		return latexItem.getElement();
	}

	@Override
	public void updatePosition(ScrollableSuggestionDisplay sug) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean resetAfterEnter() {
		return true;
	}

	@Override
	public String getLaTeX() {
		// TODO atm needed for CAS only
		return null;
	}

	@Override
	public boolean isForCAS() {
		return false;
	}

	@Override
	public void scrollCursorIntoView() {
		if (latexItem != null) {
			MathQuillHelper.scrollCursorIntoView(this, latexItem.getElement(),
					isInputTreeItem());
		}
	}

}
