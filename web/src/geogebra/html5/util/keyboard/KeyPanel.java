package geogebra.html5.util.keyboard;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;

public class KeyPanel extends FlowPanel {

	FlowPanel[] colum;
	private String[] symbolString;

	public KeyPanel(String[] symbolStrings, int rowLength, ClickHandler handler) {

		colum = new FlowPanel[rowLength];
		for (int i = 0; i < rowLength; i++) {
			colum[i] = new FlowPanel();
			colum[i].addStyleName("KeyPanelColum");
			add(colum[i]);
		}

		this.symbolString = symbolStrings;
		buildSymbolTable(handler);

		this.addStyleName("KeyPanel");
		this.getElement().getStyle().setProperty("flex", rowLength + "");
	}

	public void setSpecialButton(String feedBack, boolean largeButton,
	        int position, ClickHandler handler) {
		int col = position % colum.length;
		int row = position / colum.length;
		KeyBoardButton b = new KeyBoardButton(symbolString[position], feedBack,
		        largeButton);
		if (handler != null) {
			b.addDomHandler(handler, ClickEvent.getType());
		}
		colum[col].remove(row);
		colum[col].insert(b, row);
	}

	private void buildSymbolTable(ClickHandler handler) {
		for (int i = 0; i < symbolString.length; i++) {
			if (symbolString[i] != null) {
				int col = (int) Math.floor(i % colum.length);
				KeyBoardButton b = new KeyBoardButton(symbolString[i]);
				if (handler != null) {
					b.addDomHandler(handler, ClickEvent.getType());
				}
				colum[col].add(b);
			}
		}
	}

}
