package geogebra.html5.gui.util;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.user.client.ui.ListBox;

public class IconListBox extends ListBox {

	public IconListBox() {
		// TODO Auto-generated constructor stub
	}

	public IconListBox(boolean isMultipleSelect) {
		super(isMultipleSelect);
		// TODO Auto-generated constructor stub
	}

	public IconListBox(Element element) {
		super(element);
		// TODO Auto-generated constructor stub
	}

	public void setIcons(List<String> urls) { 
		for (String url: urls) {
			addItem("----");
		}
		
		SelectElement selectElement = SelectElement.as(getElement());
		NodeList<OptionElement> options = selectElement.getOptions();

		for (int i = 0; i < options.getLength(); i++) {
		     options.getItem(i).getStyle().setBackgroundImage(urls.get(i));;
		}
	}
}
