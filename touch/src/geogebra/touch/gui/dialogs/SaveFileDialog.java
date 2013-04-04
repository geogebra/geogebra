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
		if (this.stockStore == null)
		{
			return;
		}

		if (!this.textBox.getText().isEmpty())
		{
			this.stockStore.setItem(this.textBox.getText(), xml);
			super.app.setConstructionTitle(super.textBox.getText());
		}
	}
	
	@Override
	public void show()
	{
		this.textBox.setText(super.app.getConstructionTitle());
		super.show();
	}
}
