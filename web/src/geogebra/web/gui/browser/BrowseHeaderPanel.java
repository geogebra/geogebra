package geogebra.web.gui.browser;

import geogebra.common.main.App;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.events.LogOutEvent;
import geogebra.common.move.ggtapi.events.LoginEvent;
import geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import geogebra.common.move.ggtapi.operations.LogInOperation;
import geogebra.common.move.operations.NetworkOperation;
import geogebra.common.move.views.BooleanRenderable;
import geogebra.common.move.views.EventRenderable;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.main.AppW;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.AuxiliaryHeaderPanel;
import geogebra.web.gui.browser.SearchPanel.SearchListener;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

public class BrowseHeaderPanel extends AuxiliaryHeaderPanel implements
		ResizeListener, BooleanRenderable, EventRenderable {

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
	private FlowPanel userPanel;
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
			
			
			this.userPanel = new FlowPanel();
			this.userPanel.setStyleName("userPanel");
			this.profilePanel.add(this.userPanel);
			
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
                public void onClick() {
					App.debug("logout");
	                app.getLoginOperation().performLogOut();
	                
                }});
			
			userPanel.add(optionsPanel);
			optionsPanel.setVisible(false);
			
			profilePanel.addDomHandler(new ClickHandler(){
				@Override
                public void onClick(ClickEvent event) {
					((AppW)app).togglePopup(optionsPanel);
					event.stopPropagation();
                }},ClickEvent.getType());
		}
		this.rightPanel.clear();
		this.profileImage.setUrl(user.getImageURL());
		this.rightPanel.add(this.profilePanel);
		
	}
	public void onResize() {
		//this.setWidth(Window.getClientWidth() + "px");
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
	}

	@Override
    public void renderEvent(BaseEvent event) {
	    if(event instanceof LoginEvent){
	    	this.onLogin(((LoginEvent)event).isSuccessful(), ((LoginEvent)event).getUser());
	    }
	    if(event instanceof LogOutEvent){
	    	this.onLogout();
	    }
    }
}
