package geogebra.html5.gui;

import geogebra.common.main.Localization;
import geogebra.html5.gui.browser.BrowseResources;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Common superclass for worksheet and browse GUIs
 * 
 * @author Zbynek
 * 
 */
public class AuxiliaryHeaderPanel extends FlowPanel {

	protected final StandardButton backButton;
	private FlowPanel backPanel;
	/*protected HorizontalPanel searchPanel;*/
	protected FlowPanel rightPanel;
	private final Label headerText;

	protected final Localization loc;

	protected AuxiliaryHeaderPanel(final Localization loc, final MyHeaderPanel gui) {
		this.setStyleName("headerbar");
		this.loc = loc;
		this.backButton = new StandardButton(BrowseResources.INSTANCE.back());
		this.backButton.addStyleName("backButton");

		this.backPanel = new FlowPanel();
		this.backPanel.setStyleName("headerFirst");
		this.backPanel.add(this.backButton);

		this.rightPanel = new FlowPanel();
		this.rightPanel.setStyleName("headerSecond");

		//this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		this.add(this.backPanel);

		//this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.headerText = new Label("");
		this.headerText.addStyleName("locationTitle");
		this.add(this.headerText);
		this.backButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				gui.close();
			}
		});
		//this.add(this.rightPanel);
	}

	public void setLabels() {
		//this.backButton.setLabel(this.loc.getMenu("Back"));
	}

	public void setText(final String title) {
		this.headerText.setText(title);
	}
}
