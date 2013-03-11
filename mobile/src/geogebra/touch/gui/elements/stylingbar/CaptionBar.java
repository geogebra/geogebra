package geogebra.touch.gui.elements.stylingbar;

import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.touch.model.TouchModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

public class CaptionBar extends RoundPanel
{

	public CaptionBar(final TouchModel touchModel)
	{
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
			add(button[i]);
		}

		button[0].setText("_");
		button[1].setText("A");
		button[2].setText("A = (1,1)");
		button[3].setText("(1,1)");
	}

}
