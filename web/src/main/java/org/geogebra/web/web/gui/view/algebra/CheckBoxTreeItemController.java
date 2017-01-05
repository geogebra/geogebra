package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.web.gui.inputfield.InputSuggestions;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

/**
 * @author Laszlo
 *
 */
public class CheckBoxTreeItemController extends LatexTreeItemController
		implements ValueChangeHandler<Boolean> {

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
		Log.debug("CB mouseDown");
		if (isCheckboxHit(event)) {
			stopEdit();
			return;
		}

		super.onMouseDown(event);
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		Log.debug("CB valueChange");
		checkboxHit = true;
		stopEdit();
		((GeoBoolean) item.geo).setValue(event.getValue());
		item.geo.updateCascade();
		item.kernel.notifyRepaint();
	
	}

	public boolean isCheckboxHit(MouseEvent<?> event) {
		return checkboxHit;
		// return isWidgetHit(((CheckboxTreeItem) item).checkBox, event);
	}

	@Override
	protected boolean canEditStart(MouseEvent<?> event) {
		boolean hit = isCheckboxHit(event);
		Log.debug("CHECBOX HIT: " + hit);
		checkboxHit = false;
		return super.canEditStart(event) && !hit;
	}
}