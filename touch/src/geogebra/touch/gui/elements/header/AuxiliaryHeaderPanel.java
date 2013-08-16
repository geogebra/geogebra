package geogebra.touch.gui.elements.header;

import geogebra.common.main.Localization;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.StandardImageButton;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AuxiliaryHeaderPanel extends HorizontalPanel {

	private final StandardImageButton backButton;
	protected HorizontalPanel backPanel;
	protected HorizontalPanel searchPanel;
	protected VerticalPanel rightPanel;
	private Label headerText;
	private final Label backLabel;
	private Localization loc;

	public AuxiliaryHeaderPanel(String title, Localization loc) {
		this.setStyleName("headerbar");
		this.loc = loc;
		this.backButton = new StandardImageButton(TouchEntryPoint
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
		this.headerText = new Label(title);
		this.add(this.headerText);

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		this.add(this.rightPanel);
	}

	public void setLabels() {
		this.backLabel.setText(this.loc.getMenu("Back"));
	}

	public void setText(String title) {
		this.headerText.setText(title);
	}
}
