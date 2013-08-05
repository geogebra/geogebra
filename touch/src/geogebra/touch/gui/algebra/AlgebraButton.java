package geogebra.touch.gui.algebra;

import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.laf.LookAndFeel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;

public class AlgebraButton extends StandardImageButton {
	private static LookAndFeel getLaf() {
		return TouchEntryPoint.getLookAndFeel();
	}

	public AlgebraButton(final TabletGUI gui) {
		super(getLaf().getIcons().triangle_left());

		this.setStyleName("arrowRight");

		this.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
			}
		}, ClickEvent.getType());

		this.addDomHandler(TouchEntryPoint.getLookAndFeel().getAlgebraButtonClickHandler(gui), MouseDownEvent.getType());

		this.addDomHandler(TouchEntryPoint.getLookAndFeel().getAlgebraButtonTouchStartHandler(gui), TouchStartEvent.getType());
	}

	public void setAlgebraVisible(boolean algebraVisible) {
		this.setStyleName(algebraVisible ? "arrowRight" : "arrowLeft");

	}

}
