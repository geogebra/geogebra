package geogebra.touch.gui.dialogs;

import geogebra.touch.TouchApp;

public class SaveFileDialog extends FileDialog
{

	public SaveFileDialog(TouchApp app)
	{
		super(app);
	}

	@Override
	protected void onOK()
	{
		save(super.app.getXML());
		setDownloadName();
		hide();
	}

	@Override
	protected void onCancel()
	{
		hide();
	}

	/**
	 * Saves current file as xml String to the local storage.
	 * 
	 */
	private void save(String xml)
	{
		if (super.stockStore == null)
		{
			return;
		}

		if (!super.textBox.getText().isEmpty())
		{
			super.stockStore.setItem(super.textBox.getText(), xml);
			super.app.setConstructionTitle(super.textBox.getText());
		}
	}
	
	@Override
	public void show()
	{
		super.app.getGgbApiT().getGGB(true, super.okButton.getElement());
		super.textBox.setText(super.app.getConstructionTitle());
		super.show();
	}
	
	private void setDownloadName()
	{
	    super.okButton.getElement().setAttribute("download", super.textBox.getText()+".ggb");
	}
}
