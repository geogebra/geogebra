package geogebra.touch.gui.elements.header;

import geogebra.common.main.Localization;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.StandardButton;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Common superclass for worksheet and browse GUIs
 * 
 * @author Zbynek
 * 
 */
public class AuxiliaryHeaderPanel extends HorizontalPanel {

	protected final StandardButton backButton;
	protected FlowPanel backPanel;
	protected HorizontalPanel searchPanel;
	protected VerticalPanel rightPanel;
	private final Label headerText;

	protected final Localization loc;

	public AuxiliaryHeaderPanel(final Localization loc) {
		this.setStyleName("headerbar");
		this.loc = loc;
		this.backButton = new StandardButton(TouchEntryPoint.getLookAndFeel()
				.getIcons().back(), loc.getMenu("Back"));
		this.backButton.addStyleName("backButton");

		this.backPanel = new FlowPanel();
		this.backPanel.setStyleName("headerLeft");
		this.backPanel.add(this.backButton);

		this.rightPanel = new VerticalPanel();
		this.rightPanel.setStyleName("headerRight");

		this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.add(this.backPanel);

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.headerText = new Label("");
		this.add(this.headerText);

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		this.add(this.rightPanel);
	}

	public void setLabels() {
		this.backButton.setLabel(this.loc.getMenu("Back"));
	}

	public void setText(final String title) {
		this.headerText.setText(title);
	}
}
