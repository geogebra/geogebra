package org.geogebra.web.shared;

import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.web.html5.gui.FastButton;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GDialogBox;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.view.button.StandardButton;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A DialogBox for Web
 *
 */
public class DialogBoxW extends GDialogBox {

	/**
	 * Style name for the button panels inside the dialogs.
	 */
	protected static final String DIALOG_BUTTON_PANEL_STYLE_NAME = "DialogButtonPanel";
	
	private ErrorHandler eh;
	/** Cancel button */
	FastButton cancelButton;

	/**
	 * creates a {@link GDialogBox}
	 * 
	 * @param autoHide
	 *            {@code true} if the dialog should be automatically hidden when
	 *            the user clicks outside of it
	 * @param modal
	 *            {@code true} if keyboard and mouse events for widgets not
	 *            contained by the dialog should be ignored
	 * @param eh
	 *            error handler
	 * @param root
	 *            root for positioning
	 * @param app
	 *            application
	 */
	public DialogBoxW(boolean autoHide, boolean modal, ErrorHandler eh,
			Panel root, App app) {
		super(autoHide, modal, root, app);
		addResizeHandler();
		if (app.isUnbundledOrWhiteboard()) {
			this.setStyleName("MaterialDialogBox");
		} else {
			this.addStyleName("DialogBox");
		}
		this.addStyleName("GeoGebraFrame");
		this.setGlassEnabled(modal);
		this.eh = eh;
		addMainChildClass();
	}
	
	/**
	 * creates a {@link GDialogBox} with {@code autoHide = false} and
	 * {@code modal = true}.
	 * 
	 * @param root
	 *            root for positioning
	 * @param app
	 *            app
	 */
	public DialogBoxW(Panel root, App app) {
		this(false, true, null, root, app);
	}

	/**
	 * close dialog on ESC 
	 */
	@Override
	protected void onPreviewNativeEvent(final NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);
		if (event.getTypeInt() == Event.ONKEYUP
				&& event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
			onCancel();
		}
	}
	
	@Override
	public void show() {
		super.show();
		if (eh != null) {
			eh.resetError();
		}
	}

	private void addResizeHandler() {
	    Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				onWindowResize();
			}
		});
    }

	/**
	 * Update position when window is resized
	 */
	protected void onWindowResize() {
		if (isShowing() && !(this instanceof HasKeyboardPopup)) {
				centerAndResize(0);
		}
	}

	/**
	 * closes the dialog
	 */
	protected void onCancel() {
		hide();
	}

	/**
	 * Adds a little cross to cancel the dialog if there is already a panel
	 * attached to the Dialogbox. If the first child of the Dialogbox is not a
	 * Panel this will do nothing!
	 * 
	 * Pulled up from SaveDialogW
	 */
	public void addCancelButton() {
		if (getWidget() instanceof Panel
				&& !(getWidget() instanceof SimplePanel)) {
			SimplePanel cancel = new SimplePanel();
			this.cancelButton = new StandardButton(
					SharedResources.INSTANCE.dialog_cancel(), app);
			this.cancelButton.addStyleName("cancelSaveButton");
			this.cancelButton.addFastClickHandler(new FastClickHandler() {
				@Override
				public void onClick(Widget source) {
					onCancel();
				}
			});

			cancel.add(this.cancelButton);

			((Panel) getWidget()).add(cancel);
		}
	}
}
