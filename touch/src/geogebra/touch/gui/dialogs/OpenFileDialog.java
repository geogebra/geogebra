package geogebra.touch.gui.dialogs;

import geogebra.touch.TouchApp;
import geogebra.touch.model.GuiModel;

public class OpenFileDialog extends FileDialog
{

	//InfoDialog infoDialog;

	public OpenFileDialog(TouchApp app, GuiModel guiModel)
	{
		super(app, guiModel);		
		//FIXME the glass pane has z-index 20, we must go higher
		this.getElement().getStyle().setZIndex(42);
	}

	@Override
	protected void onOK()
	{

		boolean success = super.fm.getFile(super.textBox.getText(),super.app);
		if (success)
		{
			
				super.app.setConstructionTitle(super.textBox.getText());
			
		}
		hide();
	}



	@Override
	protected void onCancel()
	{
		hide();
	}

	@Override
	public void show()
	{
		super.textBox.setText(""); // to remove the last input in the textBox, maybe
		                           // there's a better solution
		super.show();
	}
	
	@Override
	public void setLabels()
	{

	}
}