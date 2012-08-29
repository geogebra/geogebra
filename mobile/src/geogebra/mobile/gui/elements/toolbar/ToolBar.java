package geogebra.mobile.gui.elements.toolbar;

import geogebra.mobile.controller.MobileAlgebraController;
import geogebra.mobile.gui.CommonResources;
import geogebra.mobile.gui.elements.GuiModel;
import geogebra.mobile.utils.ToolBarCommand;
import geogebra.mobile.utils.ToolBarMenu;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

/**
 * @see ButtonBar
 * 
 * @author Matthias Meisinger
 * 
 */
public class ToolBar extends ButtonBar
{

	private OptionsClickedListener[] b;
	private ToolBarCommand activeCmd;
	TextBox inputBar;

	public ToolBar()
	{
		this.addStyleName("toolbar");
	}

	/**
	 * Fill the toolBar with the default {@link ToolBarButton ToolBarButtons}
	 * and sets the default button to active.
	 * 
	 * @param model
	 *            GuiModel
	 * @see GuiModel
	 * 
	 * @param algebraController
	 *            AlgebraController responsible for handling the events
	 */
	public void makeTabletToolBar(GuiModel model,
			final MobileAlgebraController algebraController)
	{
		this.b = new ToolBarButton[11];

		this.b[0] = new ToolBarButton(ToolBarMenu.Point, model);
		this.b[1] = new ToolBarButton(ToolBarMenu.Line, model);
		this.b[2] = new ToolBarButton(ToolBarMenu.SpecialLine, model);
		this.b[3] = new ToolBarButton(ToolBarMenu.Polygon, model);
		this.b[4] = new ToolBarButton(ToolBarMenu.CircleAndArc, model);
		this.b[5] = new ToolBarButton(ToolBarMenu.ConicSection, model);
		this.b[6] = new ToolBarButton(ToolBarMenu.Mesurement, model);
		this.b[7] = new ToolBarButton(ToolBarMenu.Transformation, model);
		this.b[8] = new ToolBarButton(ToolBarMenu.SpecialObject, model);
		this.b[9] = new ToolBarButton(ToolBarMenu.ActionObject, model);

		this.b[10] = new ToolBarButton(
				CommonResources.INSTANCE.show_input_bar(), model);
		((ToolBarButton) this.b[10]).addTapHandler(new TapHandler()
		{
			@Override
			public void onTap(TapEvent event)
			{
				final PopinDialog dialog = new PopinDialog();
				dialog.setHideOnBackgroundClick(true);
				dialog.setCenterContent(true);

				RoundPanel roundPanel = new RoundPanel();

				ToolBar.this.inputBar = new TextBox();
				roundPanel.add(ToolBar.this.inputBar);

				Button button = new Button("ok");
				button.addTapHandler(new TapHandler()
				{
					@Override
					public void onTap(TapEvent e)
					{
						algebraController.newInput(ToolBar.this.inputBar
								.getText());
						dialog.hide();
					}
				});
				button.addStyleName("popinButton");
				roundPanel.add(button);

				dialog.add(roundPanel);
				dialog.show();
			}
		});

		((ToolBarButton) this.b[10]).addStyleName("rightButton");

		for (int i = 0; i < this.b.length; i++)
		{
			this.add((Widget) this.b[i]);
		}

		model.setActive((ToolBarButton) this.b[0]);
	}

	public ToolBarCommand getCommand()
	{
		return this.activeCmd;
	}
}