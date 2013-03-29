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
		String fileAsXML = super.stockStore.getItem(super.fileList.getItemText(super.fileList.getSelectedIndex()));
		try
		{
			super.app.loadXML(fileAsXML);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hide();
	}

	@Override
	protected void onCancel()
	{
		hide();
	}
}