package geogebra.touch.gui.dialogs;

import geogebra.touch.TouchEntryPoint;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.PopupPanel;

public abstract class DialogT extends PopupPanel {

	/**
	 * 
	 * @param autoHide <code>true</code> if the {@link PopupPanel} should be automatically
   *          hidden when the user clicks outside of it
	 * @param modal <code>true</code> if keyboard or mouse events that do not
   *          target the {@link PopupPanel} or its children should be ignored
	 */
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
				&& TouchEntryPoint.getLookAndFeel().receivesDoubledEvents()) {
			event.cancel();
			nativeEvent.preventDefault();
			nativeEvent.stopPropagation();
		}
	}

}
