package geogebra.web.gui.util;

import geogebra.common.main.App;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.views.SuccessErrorRenderable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;

/**
 * @author gabor
 * 
 * Dialog for signing in users
 *
 */
public class SignInDialogW extends DialogBox implements SuccessErrorRenderable {
	
	private App app;
	private Button cancel;
	/**
	 * userName for forum textfield
	 */


	/**
	 * creates a SignInDialog for log in to different
	 * accounts
	 */
	public SignInDialogW(final App app) {
		super(false, true);
		this.app = app;
		
		addStyleName("signInDialog");
		FlowPanel container = new FlowPanel();
		container.addStyleName("signInDialogContainer");
		
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("headerPanel");
		final SignInDialogW t = this;
		buttonPanel.add(cancel = new Button(app.getMenu("X"), new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				t.hide();
			}
		}));
		container.add(buttonPanel);
		
		FlowPanel ggtLoginPanel = new FlowPanel();
		ggtLoginPanel.addStyleName("ggtLoginPanel");
		
		FlowPanel ggtLoginHeader = new FlowPanel();
		ggtLoginHeader.addStyleName("headerLabel");
		Label ggtLogin = new Label(app.getMenu("LogIntoGeoGebraTube"));
		ggtLoginHeader.add(ggtLogin);
		ggtLoginPanel.add(ggtLoginHeader);
		
		Frame ggbFrame = new Frame(app.getLoginOperation().getLoginURL());
		ggbFrame.addStyleName("ggtFrame");
		
		ggbFrame.addLoadHandler(new LoadHandler() {

			public void onLoad(LoadEvent event) {
				
				Window.alert("frame loaded");
			}
		});
		ggtLoginPanel.add(ggbFrame);
		container.add(ggtLoginPanel);
		
//		((AppW) app).getLoginOperation().getView().add(this);		
		
		add(container);
	}
	
	@Override
    public void show() {
		super.show();
	}


	public void success(BaseEvent event) {
	    this.hide();
    }

	public void fail(BaseEvent event) {
    }

}
