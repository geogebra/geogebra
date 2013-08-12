package geogebra.touch.gui.elements.stylebar;

import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.touch.model.TouchModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

public class CaptionBar extends FlowPanel {

	private Button[] buttons = { new Button("_"), new Button("A"),
			new Button("A = (1,1)"), new Button("(1,1)") };
	private FlowPanel buttonPanel;

	public CaptionBar(final TouchModel touchModel) {
		this.addStyleName("captionBar");

		this.buttonPanel = new FlowPanel();
		this.buttonPanel.setStyleName("styleBarButtonPanel");

		for (int i = 0; i < this.buttons.length; i++) {
			final int index = i;

			this.buttons[i].addDomHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (touchModel.getTotalNumber() > 0) {
						// -1: anything other than 0 (move-mode)
						EuclidianStyleBarStatic.applyCaptionStyle(
								touchModel.getSelectedGeos(), -1, index);
					}
					touchModel.setCaptionMode(index);
				}
			}, ClickEvent.getType());
			this.buttonPanel.add(this.buttons[i]);
		}

		this.add(this.buttonPanel);
	}

}
