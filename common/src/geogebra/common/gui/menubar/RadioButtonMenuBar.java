package geogebra.common.gui.menubar;

public interface RadioButtonMenuBar extends MenuInterface{

	public void addRadioButtonMenuItems(MyActionListener actionListener,
			String[] items, final String[] actionCommands, int selectedPos);
	
	public void setSelected(int pos);
}
