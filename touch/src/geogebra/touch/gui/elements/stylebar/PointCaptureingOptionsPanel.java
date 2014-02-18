package geogebra.touch.gui.elements.stylebar;

import geogebra.touch.model.TouchModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

public class PointCaptureingOptionsPanel extends FlowPanel {
	private Button[] buttons = { new Button("Automatic"),
			new Button("SnapToGrid"), new Button("FixedToGrid"),
			new Button("off") };
	private final FlowPanel buttonPanel;

	PointCaptureingOptionsPanel(final TouchModel touchModel) {
		this.addStyleName("captionBar"); // TODO (?)

		this.buttonPanel = new FlowPanel();
		this.buttonPanel.setStyleName("styleBarButtonPanel");

		for (int i = 0; i < this.buttons.length; i++) {
			final int index = i;
			this.buttons[i].setText(touchModel.getKernel().getApplication()
					.getLocalization().getMenu(this.buttons[i].getText()));
			if (this.buttons[i].getText().equalsIgnoreCase("Automatic")) {
				this.buttons[i].setText(touchModel.getKernel().getApplication()
						.getLocalization().getPlain(this.buttons[i].getText()));
			}

			this.buttons[i].addDomHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					touchModel.getGuiModel().setPointCapturingMode(index);
					touchModel.getGuiModel().closeAllOptions();
				}
			}, ClickEvent.getType());
			this.buttonPanel.add(this.buttons[i]);
		}

		this.add(this.buttonPanel);
	}
}
