package org.geogebra.web.full.gui;

import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.browser.BrowseResources;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Common superclass for worksheet and browse GUIs
 * 
 * @author Zbynek
 * 
 */
public class AuxiliaryHeaderPanel extends FlowPanel {

	/** back button */
	protected StandardButton backButton;
	private FlowPanel backPanel;
	/** panel on the right */
	protected FlowPanel rightPanel;
	/** title label */
	private final Label headerText;
	/** header panel */
	protected final MyHeaderPanel gui;
	/** localization */
	protected final Localization loc;

	/**
	 * @param loc
	 *            localization
	 * @param gui
	 *            header panel
	 */
	protected AuxiliaryHeaderPanel(final Localization loc,
	        final MyHeaderPanel gui) {
		this.setStyleName("headerbar");
		this.loc = loc;
		this.gui = gui;

		addBackPanel();

		this.rightPanel = new FlowPanel();
		this.rightPanel.setStyleName("headerSecond");

		this.headerText = new Label("");
		this.headerText.addStyleName("locationTitle");
		this.add(this.headerText);

	}

	private void addBackPanel() {
		this.backPanel = new FlowPanel();
		this.backPanel.setStyleName("headerFirst");
		this.backPanel.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				gui.close();
			}
		}, ClickEvent.getType());

		// TODO - use new icon; this is just a placeholder
		this.backButton = new StandardButton(BrowseResources.INSTANCE.back());
		this.backButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				gui.close();
			}
		});
		this.backButton.addStyleName("backButton");
		this.backPanel.add(this.backButton);

		SimplePanel ggbLogoPanel = new SimplePanel();
		ggbLogoPanel.setStyleName("ggbLogoPanel");
		NoDragImage image = new NoDragImage(GuiResources.INSTANCE.header_back()
		        .getSafeUri().asString());
		ggbLogoPanel.add(image);
		this.backPanel.add(ggbLogoPanel);
		this.add(this.backPanel);
	}

	/** Update header */
	public void setLabels() {
		// overriden in subclasses
	}

	/**
	 * @param title
	 *            set header text
	 */
	public void setText(final String title) {
		this.headerText.setText(title);
	}
}
