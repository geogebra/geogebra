package geogebra.touch.gui.algebra;

import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.laf.LookAndFeel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

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

		this.addDomHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.stopPropagation();
				gui.toggleAlgebraView();
				if (TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel() != null) {
					TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel()
							.enableDisableButtons();
				}
			}
		}, MouseDownEvent.getType());

		this.addDomHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				event.stopPropagation();
			}
		}, TouchStartEvent.getType());

	}

	public void setAlgebraVisible(boolean algebraVisible) {
		this.setStyleName(algebraVisible ? "arrowRight" : "arrowLeft");
		
	}

}
