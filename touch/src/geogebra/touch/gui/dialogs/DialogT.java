package geogebra.touch.gui.dialogs;

import geogebra.touch.TouchEntryPoint;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.PopupPanel;

public abstract class DialogT extends PopupPanel {

	public DialogT(boolean autoHide, boolean modal) {
		super(autoHide, modal);
	}

	@Override
	protected void onPreviewNativeEvent(final NativePreviewEvent event) {
		if (!this.isVisible()) {
			return;
		}

		super.onPreviewNativeEvent(event);

		final Event nativeEvent = Event.as(event.getNativeEvent());
		if (nativeEvent.getTypeInt() == Event.ONMOUSEDOWN
				&& TouchEntryPoint.getLookAndFeel().isMouseDownIgnored()) {
			event.cancel();
			nativeEvent.preventDefault();
			nativeEvent.stopPropagation();
		}
	}

}
