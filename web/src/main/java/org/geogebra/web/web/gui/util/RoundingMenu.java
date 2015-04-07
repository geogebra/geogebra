package org.geogebra.web.web.gui.util;

import org.geogebra.common.main.App;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

public class RoundingMenu extends MenuBar {

	public interface IRoundingMenuListener {
		void onChange(int index);
	}
	
	private App app;
	private IRoundingMenuListener listener;
	
	private class RoundingCommand implements Command {
		private int index;
		public RoundingCommand(int idx) {
			index = idx;
		}
		public void execute() {
	        listener.onChange(index);
        }
		
	}

	public RoundingMenu(App app, final IRoundingMenuListener listener) {
		super(true);
		this.app = app;
		this.listener = listener;
		String[] items = app.getLocalization().getRoundingMenu();
		for (int i=0; i < items.length; i++) {
			addItem(items[i], new RoundingCommand(i));
		}
		
	}

}
