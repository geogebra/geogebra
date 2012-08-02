package geogebra.mobile.gui.elements.toolbar;

import geogebra.mobile.utils.ToolBarCommand;

import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;

public class OptionButton extends ToolButton
{

	ToolBarButtonInterface ancestor;

	public OptionButton(ToolBarCommand cmd, ToolBarButtonInterface ancestor)
	{
		super(cmd);

		this.ancestor = ancestor;

		this.addTapHandler(new TapHandler()
		{
			@Override
			public void onTap(TapEvent event)
			{
				getClicked();
			}
		});
	}

	private void getClicked()
	{
		ancestor.optionClicked(super.getCmd());
	}

}
