package geogebra.touch.gui.elements.stylingbar;

import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.touch.model.TouchModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;

public class CaptionBar extends PopupPanel
{

	private LayoutPanel contentPanel;

	public CaptionBar(final TouchModel touchModel)
	{
		this.contentPanel = new LayoutPanel();
		this.addStyleName("StyleBarOptions");

		Button[] button = new Button[4];
		for (int i = 0; i < button.length; i++)
		{
			final int index = i;
			button[i] = new Button();
			button[i].addDomHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					if (touchModel.getTotalNumber() > 0)
					{
						// -1: anything other than 0 (move-mode)
						EuclidianStyleBarStatic.applyCaptionStyle(touchModel.getSelectedGeos(), -1, index);
					}
					touchModel.setCaptionMode(index);
				}
			}, ClickEvent.getType());
			this.contentPanel.add(button[i]);

			this.setWidget(this.contentPanel);
		}

		button[0].setText("_");
		button[1].setText("A");
		button[2].setText("A = (1,1)");
		button[3].setText("(1,1)");
	}

}
