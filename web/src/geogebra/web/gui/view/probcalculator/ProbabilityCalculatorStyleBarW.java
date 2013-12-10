package geogebra.web.gui.view.probcalculator;

import geogebra.common.gui.view.probcalculator.ProbabiltyCalculatorStyleBar;
import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.menubar.GRadioButtonMenuItem;

import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author gabor Probability Calculator Stylebar for web
 *
 */
public class ProbabilityCalculatorStyleBarW extends
        ProbabiltyCalculatorStyleBar implements ValueChangeHandler<Boolean> {
	
	private MenuBar wrappedToolbar;
	private MenuItem btnRounding;
	private MyMenuBar roundingPopup;

	public ProbabilityCalculatorStyleBarW(App app, ProbabilityCalculatorViewW probCalc) {
		this.wrappedToolbar = new MenuBar();
		this.probCalc = probCalc;
		this.app = app;
		
		createGUI();
	}

	private void createGUI() {
		wrappedToolbar.clearItems();
		buildOptionsButton();
	    
    }

	private void buildOptionsButton() {
	    roundingPopup = createRoundingPopup();
	    Image img = new Image(AppResources.INSTANCE.triangle_down());
	    btnRounding = new MenuItem(img.getElement().getInnerHTML(), true, roundingPopup);
	    
	    updateMenuDecimalPlaces(roundingPopup);
	    
	    
    }
	
	/**
	 * Update the menu with the current number format.
	 */
	private void updateMenuDecimalPlaces(MyMenuBar menu) {
		int printFigures = probCalc.getPrintFigures();
		int printDecimals = probCalc.getPrintDecimals();

		if (menu == null)
			return;
		int pos = -1;

		if (printFigures >= 0) {
			if (printFigures > 0 && printFigures < App.figuresLookup.length)
				pos = App.figuresLookup[printFigures];
		} else {
			if (printDecimals > 0 && printDecimals < App.decimalsLookup.length)
				pos = App.decimalsLookup[printDecimals];
		}

		try {
			 //TODO: find out someting for this!!!!!!List<GRadioButtonMenuItem> m = menu.getMenuItems();
			///((GRadioButtonMenuItem) m.get(pos)).setSelected(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private MyMenuBar createRoundingPopup() {
		MyMenuBar menu = new MyMenuBar();
		
		String[] strDecimalSpaces = app.getLocalization().getRoundingMenu();
		addRadioButtonMenuItems(menu, this, strDecimalSpaces, App.strDecimalSpacesAC, 0);
		
		return menu;
	}

	private void addRadioButtonMenuItems(MenuBar menu,
            ValueChangeHandler<Boolean> al,
            String[] items, String[] actionCommands, int selectedPos) {
		GRadioButtonMenuItem mi;
		
		for (int i = 0; i < items.length; i++) {
			if (items[i] == "---") {
				//add separator with css
			} else {
				String text = app.getMenu(items[i]);
				mi = new GRadioButtonMenuItem(text, actionCommands[i], "probstylebarradio");
				if (i == selectedPos) {
					mi.setSelected(true);
				}
				mi.addValueChangeHandler(al);
				menu.addItem(mi.getMenuItem());
				
				
			}
		}
	    
    }

	public void updateLayout() {
	    // TODO Auto-generated method stub
	    
    }

	public void onValueChange(ValueChangeEvent<Boolean> event) {
	    // TODO Auto-generated method stub
	    
    }
	
	private class MyMenuBar extends MenuBar {
		
		public MyMenuBar() {
			super();
		}
		
		public List<MenuItem> getMenuItems() {
			return this.getItems();
		}
		
	}

}
