package geogebra.touch.gui.dialogs;

import geogebra.touch.TouchApp;

public class OpenFileDialog extends FileDialog
{
	public OpenFileDialog(TouchApp app)
	{
		super(app);
	}

	@Override
	protected void onOK()
	{
		String fileAsXML = super.stockStore.getItem(super.textBox.getText());
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
	
	//to remove the last input in the textBox, maybe there's a better solution
	@Override
	public void show()
	{
		super.textBox.setText("");
		super.show();
	}
}