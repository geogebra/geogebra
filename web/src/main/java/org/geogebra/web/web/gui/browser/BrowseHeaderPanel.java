package org.geogebra.web.web.gui.browser;

import org.geogebra.common.main.App;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.operations.NetworkOperation;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.ResizeListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.AuxiliaryHeaderPanel;
import org.geogebra.web.web.gui.browser.SearchPanel.SearchListener;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class BrowseHeaderPanel extends AuxiliaryHeaderPanel implements
		ResizeListener, BooleanRenderable, EventRenderable {

	/** contains width of the leftHeader and margin of the searchDiv **/
	private final int WIDTH_HEADER_FIRST = 105;
	/*public interface SearchListener {
		void onSearch(String query);
	}*/

	/*private Panel underline;
	private TextBox query;
	private final FastButton searchButton;
	private FastButton cancelButton;
	private final List<SearchListener> listeners;*/
	private NetworkOperation op;
	
	private FlowPanel signInPanel;
	private Button signInButton;
	
	private FlowPanel profilePanel;
	private Image profileImage;
	private LogInOperation login;
	private App app;
	
	private FlowPanel optionsPanel;
	private FlowPanel arrowPanel;
	private FlowPanel optionsPanelContent;

	private SearchPanel searchPanel;
	private BrowseGUI bg;

	private StandardButton logoutButton;

	public BrowseHeaderPanel(final App app, final BrowseGUI browseGUI, NetworkOperation op) {
		super(app.getLocalization(), browseGUI);
		this.bg = browseGUI;
		this.op = op;
		this.app = app;
		this.login = app.getLoginOperation();		
		this.signInPanel = new FlowPanel();
		
		addSearchPanel();
		
		this.add(this.rightPanel);
		
		createSignIn();
		setLabels();
	}
	
	private void addSearchPanel() { 
		this.searchPanel = new SearchPanel((AppW) app); 
		this.searchPanel.addSearchListener(new SearchListener() { 
			@Override 
			public void onSearch(final String query) { 
				BrowseHeaderPanel.this.bg.displaySearchResults(query); 
			} 
		}); 
		this.add(this.searchPanel); 
	} 
	
	private void createSignIn() {
		 op.getView().add(this);
		    
		    // this methods should be called only from AppWapplication or AppWapplet
		   
		   login.getView().add(this);
		   if (login.isLoggedIn()) {
			   onLogin(true, login.getModel().getLoggedInUser());
		   }else{
			   onLogout();
		   }
		   
		   if (!op.isOnline()) {
			   render(false);
		   }
	    
    }

	protected void clearSearchPanel() {
		this.searchPanel.onCancel();
	}

	private void onLogout() {
		if(this.signInButton == null){
			this.signInButton = ((AppW) app).getLAF().getSignInButton(app);
			
			this.signInPanel.add(this.signInButton);
		}
		this.rightPanel.clear();
		this.rightPanel.add(this.signInPanel);
	    
    }



	private void onLogin(boolean successful,GeoGebraTubeUser user){
		if(!successful){
			return;
		}
		if(this.profilePanel == null){
			this.profilePanel = new FlowPanel();
			this.profilePanel.setStyleName("profilePanel");
			
			
			this.profileImage = new Image();
			this.profileImage.setStyleName("profileImage");
			this.profileImage.setHeight("40px");
			this.profilePanel.add(this.profileImage);
			
			final PopupPanel popup = new PopupPanel();
			popup.addStyleName("optionsPopup");
			popup.setAutoHideEnabled(true);
			popup.addAutoHidePartner(profilePanel.getElement());
			this.optionsPanel = new FlowPanel();
			this.optionsPanel.setStyleName("profileOptionsPanel");
			
			arrowPanel = new FlowPanel();
			Image arrow = new Image(GuiResources.INSTANCE.arrow_submenu_up());
			arrowPanel.add(arrow);
			arrowPanel.setStyleName("arrow");
			optionsPanel.add(arrowPanel);
			
			optionsPanelContent = new FlowPanel();
			optionsPanelContent.setStyleName("profileOptionsContent");
			optionsPanel.add(optionsPanelContent);
			
			logoutButton = new StandardButton(app.getMenu("SignOut"));
			logoutButton.addStyleName("logoutButton");
			logoutButton.addStyleName("gwt-Button");
			optionsPanelContent.add(logoutButton);
			
			logoutButton.addFastClickHandler(new FastClickHandler(){

				@Override
                public void onClick(Widget source) {
					App.debug("logout");
	                app.getLoginOperation().performLogOut();
	                togglePopup(popup);
                }});
						
			popup.add(optionsPanel);
			profilePanel.addDomHandler(new ClickHandler(){
				@Override
                public void onClick(ClickEvent event) {
					togglePopup(popup);
					event.stopPropagation();
                }},ClickEvent.getType());
		}
		this.rightPanel.clear();
		if(user.getImageURL()!= null){
			this.profileImage.setUrl(user.getImageURL());
		}else{
			this.profileImage.setUrl(GuiResources.INSTANCE.menu_icon_help().getSafeUri());
		}
			
		this.rightPanel.add(this.profilePanel);
		
	}
	
	/**
	 * show / hide popupPanel.
	 * @param p PopupPanel
	 */
	void togglePopup(PopupPanel p) {
		if (p.isShowing()) {
			p.hide();
		} else {
			p.show();
		}
	}
	
	@Override
	public void onResize() {
		this.searchPanel.setWidth(getRemainingWidth() + "px");
	}

	/**
	 * 
	 * @return the remaining width for the searchPanel.
	 */
	private int getRemainingWidth() {
		int rightPanelWidth;
		if (this.rightPanel.getOffsetWidth() == 0) {
			if (this.signInButton != null && this.signInButton.isVisible()) {
				rightPanelWidth = 150;
			} else {
				rightPanelWidth = 60;
			}
		} else {
			rightPanelWidth = this.rightPanel.getOffsetWidth();
		}
		
	    return (int) app.getWidth() - WIDTH_HEADER_FIRST - rightPanelWidth;
    }

	@Override
	public void render(boolean b) {
		if (this.signInButton != null) {
			this.signInButton.setEnabled(b);
		}
	}

	@Override
	public void setLabels() {
		super.setLabels();
		if(this.signInButton != null){
			this.signInButton.setText(loc.getMenu("SignIn"));
		}
		if(this.logoutButton != null){
			this.logoutButton.setText(app.getMenu("SignOut"));
		}
		if(this.searchPanel != null){
			this.searchPanel.setLabels();
		}
		app.getGuiManager().invokeLater(new Runnable(){
			@Override
			public void run() {
				onResize();
			}
		});
	}

	@Override
    public void renderEvent(BaseEvent event) {
	    if(event instanceof LoginEvent){
	    	this.onLogin(((LoginEvent)event).isSuccessful(), ((LoginEvent)event).getUser());
	    }
	    if(event instanceof LogOutEvent){
	    	this.onLogout();
	    }
	    onResize();
    }
}
