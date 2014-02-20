package geogebra.html5.gui.browser;
import geogebra.common.main.Localization;
import geogebra.common.move.operations.NetworkOperation;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.ResizeListener;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class BrowseHeaderPanel extends AuxiliaryHeaderPanel implements
		ResizeListener {

	/*public interface SearchListener {
		void onSearch(String query);
	}*/

	/*private Panel underline;
	private TextBox query;
	private final FastButton searchButton;
	private FastButton cancelButton;
	private final List<SearchListener> listeners;*/
	private BrowseGUI browseGUI;
	private NetworkOperation op;
	
	private FlowPanel signInPanel;
	private Button signInButton;
	
	private FlowPanel profilePanel;
	private Image profileImage;
	private Label userName;
	private FlowPanel userPanel;
	private Image optionsArrow;

	public BrowseHeaderPanel(final Localization loc, final BrowseGUI browseGUI,
			NetworkOperation op) {
		super(loc);
		this.browseGUI = browseGUI;
		this.op = op;

		this.backButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				browseGUI.close();
			}
		});

		//TODO: Make sign in
		this.signInPanel = new FlowPanel();
		
		//TODO: Insert in rightPanel if NOT SIGNED IN
		//TODO: Translate Sign in
		this.signInButton = new Button("Sign in");
		this.signInPanel.add(this.signInButton);
		//this.rightPanel.add(this.signInPanel);
		
		//TODO: Insert in rightPanel if SIGNED IN
		this.profilePanel = new FlowPanel();
		this.profilePanel.setStyleName("profilePanel");
		this.userPanel = new FlowPanel();
		this.userPanel.setStyleName("userPanel");
		this.userName = new Label("Steffi");
		this.optionsArrow = new Image(BrowseResources.INSTANCE.arrow_options());
		this.optionsArrow.setStyleName("optionsArrow");
		this.userPanel.add(this.userName);
		this.userPanel.add(this.optionsArrow);
		this.profilePanel.add(this.userPanel);
		
		this.profileImage = new Image(BrowseResources.INSTANCE.user_image());
		this.profileImage.setStyleName("profileImage");
		this.profilePanel.add(this.profileImage);
		
		this.rightPanel.add(this.profilePanel);
		
		this.add(this.rightPanel);
		
		//TODO: Set correct text
		this.setText("GeoGebraTube");
		setLabels();
		
		//Steffi: Search is in SearchPanel now! Hope it works correctly!
		/*this.searchPanel = new HorizontalPanel();
		this.searchPanel.setStyleName("searchPanel");
		this.listeners = new ArrayList<SearchListener>();

		this.query = new TextBox();
		this.query.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(final KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
					event.preventDefault();
					return;
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					doSearch();
				}
			}
		});

		this.query.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(final FocusEvent event) {
				onFocusQuery();
			}
		});

		this.query.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(final BlurEvent event) {
				onBlurQuery();
			}
		});

		this.searchButton = new StandardButton(BrowseResources.INSTANCE.search());
		this.searchButton.addStyleName("searchButton");
		this.searchButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				doSearch();
			}
		});

		this.cancelButton = new StandardButton(BrowseResources.INSTANCE.dialog_cancel());
		this.cancelButton.addStyleName("cancelButton");
		this.cancelButton.setVisible(false);
		this.cancelButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				onCancel();
			}
		});

		// Input Underline for Android
		this.underline = new LayoutPanel();
		this.underline.setStyleName("inputUnderline");
		this.underline.addStyleName("inactive");

		this.searchPanel.add(this.searchButton);
		this.searchPanel.add(this.query);
		this.searchPanel.add(this.cancelButton);

		this.rightPanel.add(this.searchPanel);
		this.rightPanel.add(this.underline);

		this.op = op;
		op.getView().add(this);
		setLabels();*/
	}

	/*void doSearch() {
		if (!this.query.getText().equals("")) {
			fireSearchEvent();
		} else {
			this.cancelButton.setVisible(false);
		}
		this.query.setFocus(false);
		this.underline.removeStyleName("active");
		this.underline.addStyleName("inactive");
	}*/

	/*void onCancel() {
		this.query.setFocus(false);
		this.query.setText("");
		this.underline.removeStyleName("active");
		this.underline.addStyleName("inactive");
		this.cancelButton.setVisible(false);
		this.browseGUI.loadFeatured();
	}*/

	/*void onFocusQuery() {
		this.query.setFocus(true);
		this.cancelButton.setVisible(true);
		this.underline.removeStyleName("inactive");
		this.underline.addStyleName("active");
	}*/

	/*void onBlurQuery() {
		if (this.query.getText().equals("")) {
			this.query.setFocus(false);
			this.underline.removeStyleName("active");
			this.underline.addStyleName("inactive");
			this.cancelButton.setVisible(false);
		}
	}*/

	/*public boolean addSearchListener(final SearchListener l) {
		return this.listeners.add(l);
	}*/

	/*private void fireSearchEvent() {
		for (final SearchListener s : this.listeners) {
			s.onSearch(this.query.getText());
		}
	}*/

	public void onResize() {
		this.setWidth(Window.getClientWidth() + "px");
	}

	/*@Override
	public void render(boolean b) {
		this.setText(this.loc.getMenu("Worksheets") + (b ? "" : " (Offline)"));

	}*/

	@Override
	public void setLabels() {
		super.setLabels();
		//render(this.op.getOnline());
	}
}
