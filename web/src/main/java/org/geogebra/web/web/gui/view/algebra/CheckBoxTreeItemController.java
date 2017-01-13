package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.web.gui.inputfield.InputSuggestions;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;

/**
 * @author Laszlo
 *
 */
public class CheckBoxTreeItemController extends LatexTreeItemController {

	private InputSuggestions sug;
	private RetexKeyboardListener retexListener;
	private boolean checkboxHit;

	/**
	 * @param item
	 *            AV item
	 */
	public CheckBoxTreeItemController(RadioTreeItem item) {
		super(item);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.stopPropagation();

		if (event.getNativeButton() != NativeEvent.BUTTON_RIGHT) {
			toggleCheckbox();
		}
		PointerEvent wrappedEvent = PointerEvent.wrapEventAbsolute(event,
				ZeroOffset.instance);
		onPointerDown(wrappedEvent);
		handleAVItem(event);
		item.updateButtonPanelPosition();
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		// event.stopPropagation();
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		event.stopPropagation();
	}

	private void toggleCheckbox() {
		GeoBoolean bool = (GeoBoolean) item.geo;
		bool.setValue(!bool.getBoolean());
		item.geo.updateCascade();
		item.kernel.notifyRepaint();

	}

	@Override
	protected boolean canEditStart(MouseEvent<?> event) {
		return false;
	}
}