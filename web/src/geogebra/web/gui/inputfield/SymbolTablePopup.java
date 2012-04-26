package geogebra.web.gui.inputfield;

import sun.java2d.pipe.SpanShapeRenderer.Simple;
import geogebra.common.gui.util.TableSymbols;
import geogebra.web.main.Application;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.PopupPanel;

public class SymbolTablePopup extends PopupPanel implements ClickHandler, KeyUpHandler {
	
	FlexTable symbolTable = null;
	private Application app;
	private AutoCompleteTextField textField;

	public SymbolTablePopup(Application app,
            AutoCompleteTextField autoCompleteTextField) {
	   this.app = app;
	   this.textField = textField;
	   createSymbolTable();
	   registerListeners();
    }
	
	private void createSymbolTable() {
		symbolTable = new FlexTable();
		
		String [] icons = TableSymbols.basicSymbols(app);
		for (int i = 0; i < icons.length; i++) {
			int x = (int) Math.floor(i % 10);
			int y = (int) Math.floor(i / 10);
			Anchor a = new Anchor(icons[i]);
	        a.addClickHandler(this);
	        //sadly, it is not so nice, but I can't attach it to historyList :-(
	        a.addKeyUpHandler(this);
	        symbolTable.setWidget(x, y, a);
		}
		add(symbolTable);
	}
	
	private void registerListeners() {
		
	}

	public void onClick(ClickEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void onKeyUp(KeyUpEvent event) {
	    // TODO Auto-generated method stub
	    
    }

}
