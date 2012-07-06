package geogebra.web.gui.menubar;

import com.google.gwt.user.client.Command;

public abstract class RadioButtonCommand implements Command {
	
	private RadioButtonMenuBarW menuBar;
	private int itemIndex;

	public RadioButtonCommand(RadioButtonMenuBarW menu, Integer index){
		super();
		menuBar = menu;
		itemIndex = index;
	}
	
	public void execute() {
        exec();
        setSelection();
    }
	
	public abstract void exec();
	
	private void setSelection(){
		menuBar.setSelected_old(itemIndex);
	}


	
	
}
