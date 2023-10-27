package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseEvent;
import org.gwtproject.event.dom.client.TouchStartEvent;

/**
 * @author Laszlo
 *
 */
public class CheckBoxTreeItemController extends LatexTreeItemController {

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

		if (CancelEventTimer.cancelMouseEvent()
				|| checkMarbleHit(event)) {
			return;
		}

		app.closePopups();
	
		PointerEvent wrappedEvent = PointerEvent.wrapEventAbsolute(event,
				ZeroOffset.INSTANCE);
		onPointerDown(wrappedEvent, event);
		handleAVItem(event);
		if (event.getNativeButton() != NativeEvent.BUTTON_RIGHT) {
			toggleCheckbox();
		}

		Scheduler.get().scheduleDeferred(item::adjustStyleBar);
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.stopPropagation();
		app.closePopups();

		PointerEvent wrappedEvent = PointerEvent.wrapEvent(event,
				ZeroOffset.INSTANCE);

		onPointerDownMainButton(wrappedEvent);
		handleAVItem(event);
		toggleCheckbox();

		Scheduler.get().scheduleDeferred(item::adjustStyleBar);
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
