package geogebra.mobile.gui.elements.toolbar;

import geogebra.mobile.utils.ToolBarCommand;

import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;

public class OptionButton extends ToolButton
{

	OptionsClickedListener ancestor;

	public OptionButton(ToolBarCommand cmd, OptionsClickedListener ancestor)
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

	void getClicked()
	{
		this.ancestor.optionClicked(super.getCmd());
	}

}
