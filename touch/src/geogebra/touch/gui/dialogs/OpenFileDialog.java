package geogebra.touch.gui.dialogs;

import geogebra.touch.TouchApp;

public class OpenFileDialog extends FileDialog
{

	InfoDialog infoDialog;

	public OpenFileDialog(TouchApp app)
	{
		super(app);
		this.infoDialog = new InfoDialog(app.getLocalization(),super.stockStore);
	}

	@Override
	protected void onOK()
	{
		if (!super.app.getXML().equals(super.stockStore.getItem(super.app.getConstructionTitle())))
		{
			saveBeforeOpen();
		}

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

	private void saveBeforeOpen()
	{
		this.infoDialog.show(super.app.getConstructionTitle(), super.app.getXML());
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
		this.infoDialog.setLabels();
	}
}