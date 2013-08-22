package geogebra.touch.gui.elements.header;

import geogebra.common.main.Localization;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.FastButton;
import geogebra.touch.gui.elements.StandardButton;

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

	protected final FastButton backButton;
	protected HorizontalPanel backPanel;
	protected HorizontalPanel searchPanel;
	protected VerticalPanel rightPanel;
	private final Label headerText;
	private final Label backLabel;
	protected final Localization loc;

	public AuxiliaryHeaderPanel(final Localization loc) {
		this.setStyleName("headerbar");
		this.loc = loc;
		this.backButton = new StandardButton(TouchEntryPoint
				.getLookAndFeel().getIcons().back());
		this.backButton.addStyleName("backButton");

		this.backPanel = new HorizontalPanel();
		this.backPanel.setStyleName("headerLeft");
		this.backPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.backPanel.add(this.backButton);

		this.backLabel = new Label(loc.getMenu("Back"));
		this.backLabel.addStyleName("backLabel");
		this.backPanel.add(this.backLabel);

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
		this.backLabel.setText(this.loc.getMenu("Back"));
	}

	public void setText(final String title) {
		this.headerText.setText(title);
	}
}
