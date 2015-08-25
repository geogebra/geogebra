package org.geogebra.web.web.gui.dialog;

import org.geogebra.web.html5.gui.FastButton;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GDialogBox;
import org.geogebra.web.html5.main.ErrorHandler;
import org.geogebra.web.web.gui.browser.BrowseResources;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A DialogBox for Web
 *
 */
public class DialogBoxW extends GDialogBox {
	
	private ErrorHandler eh;
	FastButton cancelButton;


	/**
	 * creates a {@link DialogBox}
	 * @param autoHide {@code true} if the dialog should be automatically hidden when the user clicks outside of it
	 * @param modal {@code true}  if keyboard and mouse events for widgets not contained by the dialog should be ignored
	 */
	public DialogBoxW(boolean autoHide, boolean modal, ErrorHandler eh) {
		super(autoHide, modal);
		addResizeHandler();
		this.addStyleName("DialogBox");
		this.addStyleName("GeoGebraFrame");
		this.setGlassEnabled(true);
		this.eh = eh;
	}
	
	/**
	 * creates a {@link DialogBox} with {@code autoHide = false} and {@code modal = true}.
	 */
	public DialogBoxW() {
		this(false, true, null);
	}

	/**
	 * close dialog on ESC 
	 */
	@Override
	protected void onPreviewNativeEvent(final NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);
		if (event.getTypeInt() == Event.ONKEYUP && event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
			hide();
		}
	}
	
	public void show(){ 
		super.show();
		if(eh != null){
			eh.showError(null); 
			eh.setActive(true);
		}
	} 
			 	                         
	public void hide(){ 
		super.hide(); 
		if(eh != null){
			eh.setActive(false);
		}
	}

	
	/**
	 * add resizeHandler to center the dialog
	 */
    private void addResizeHandler() {
	    Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				if (DialogBoxW.this.isShowing()) {
					center();
				}
			}
		});
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
	protected void addCancelButton() {
		if (getWidget() instanceof Panel) {
			SimplePanel cancel = new SimplePanel();
			this.cancelButton = new StandardButton(
			        BrowseResources.INSTANCE.dialog_cancel());
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
