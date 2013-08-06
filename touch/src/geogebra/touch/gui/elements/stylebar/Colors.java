package geogebra.touch.gui.elements.stylebar;

import geogebra.common.awt.GColor;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.ToolBarCommand;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A {@link VerticalPanel} which contains the different color-choices.
 */
public class Colors extends FlowPanel {
    StyleBar styleBar;
    TouchModel touchModel;

    public Colors(StyleBar stylingBar, TouchModel touchModel) {
	this.styleBar = stylingBar;
	this.touchModel = touchModel;
    }

    private void addColorButton(final GColor color) {
	final PushButton button = new PushButton();
	button.setStyleName("button");

	button.getElement().getStyle().setBackgroundImage("initial");

	// windows explorer didn't like .getStyle().setBackgroundColor(...), so
	// I
	// replaced it:
	button.getElement().setAttribute("style", "background: " + GColor.getColorString(color));

	button.addDomHandler(new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		Colors.this.styleBar.updateColor(GColor.getColorString(color));
		Colors.this.touchModel.getGuiModel().setColor(color);
		if (Colors.this.touchModel.lastSelected() != null && Colors.this.touchModel.isColorChangeAllowed()
			&& StyleBarStatic.applyColor(Colors.this.touchModel.getSelectedGeos(), color)) {
		    Colors.this.touchModel.lastSelected().updateRepaint();
		}

		if (Colors.this.touchModel.getCommand().equals(ToolBarCommand.Pen)
			|| Colors.this.touchModel.getCommand().equals(ToolBarCommand.FreehandShape)) {
		    Colors.this.styleBar.euclidianView.getEuclidianController().getPen().setPenColor(color);
		}

		Colors.this.touchModel.storeOnClose();
	    }
	}, ClickEvent.getType());

	this.add(button);
    }

    protected void drawColorChoice(List<GColor> listOfColors) {
	this.clear();
	for (final GColor color : listOfColors) {
	    this.addColorButton(color);
	}
    }
}
