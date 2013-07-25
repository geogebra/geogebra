package geogebra.touch.gui.algebra;

import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.elements.ArrowImageButton;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.laf.LookAndFeel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.FlowPanel;

public class AlgebraViewArrowPanel extends FlowPanel {
	private static LookAndFeel getLaf()
	{
		return TouchEntryPoint.getLookAndFeel();
	}
	
	StandardImageButton algebraViewButton;
	
	public AlgebraViewArrowPanel(final TabletGUI gui){
		super();
		this.algebraViewButton = new ArrowImageButton(getLaf().getIcons().triangle_left());

		this.setStyleName("algebraViewArrowPanel");
		this.algebraViewButton.setStyleName("arrowRight");
		this.add(this.algebraViewButton);
		
		this.algebraViewButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.stopPropagation();
			}
		}, ClickEvent.getType());

		this.algebraViewButton.addDomHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(MouseDownEvent event)
			{
				event.stopPropagation();
				gui.toggleAlgebraView();
				if (TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel() != null)
				{
					TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel().enableDisableButtons();
				}
			}
		}, MouseDownEvent.getType());

		this.algebraViewButton.addDomHandler(new TouchStartHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				event.stopPropagation();
			}
		}, TouchStartEvent.getType());
	}

	public void setAlgebraVisible(boolean algebraVisible) {
		this.algebraViewButton.setStyleName(algebraVisible ? "arrowRight" : "arrowLeft");
		
	}
}
