package geogebra.touch.gui.dialogs;

import geogebra.touch.TouchApp;

public class SaveFileDialog extends FileDialog
{

	public SaveFileDialog(TouchApp app)
	{
		super(app);
		// FIXME title not shown!
		super.textBox.setText(app.getConstructionTitle());
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

		if (this.textBox.getText() != "")
		{
			this.stockStore.setItem(this.textBox.getText(), xml);
		}
	}
}
