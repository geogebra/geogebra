package geogebra.touch.gui.dialogs;

import geogebra.touch.TouchApp;

public class OpenFileDialog extends FileDialog
{

	//InfoDialog infoDialog;

	public OpenFileDialog(TouchApp app)
	{
		super(app);		
		//FIXME the glass pane has z-index 20, we must go higher
		this.getElement().getStyle().setZIndex(42);
	}

	@Override
	protected void onOK()
	{

		String fileAsXML = super.fm.getFile(super.textBox.getText());
		if (fileAsXML != null)
		{
			try
			{
				super.app.loadXML(fileAsXML);
				super.app.setConstructionTitle(super.textBox.getText());
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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