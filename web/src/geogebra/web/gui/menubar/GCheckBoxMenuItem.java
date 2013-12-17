package geogebra.web.gui.menubar;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author gabor
 * 
 * Menuitem that uses checkbox
 *
 */
public class GCheckBoxMenuItem extends MenuItem {

	private FlowPanel panel;
	private CheckBox checkbox;

	/**
	 * @param html
	 * creates a new GChecbox menuitem
	 */
	public GCheckBoxMenuItem(SafeHtml html) {
	    super(html);
	    initGui(html, true);
	    
    }
	
	private void initGui(SafeHtml html, boolean showCheckbox) {
		addStyleName("GChecBoxMenuItem");
		checkbox = new CheckBox();
	    panel = new FlowPanel();
	    if (showCheckbox) {
	    	panel.add(checkbox);
	    }
	    panel.add(new HTML(html));
	    this.setHTML(panel.getElement().getInnerHTML());
	}

	/**
	 * @param html menuItems html
	 * @param showCheckbox wether to show checkbox
	 */
	public GCheckBoxMenuItem(SafeHtml html, boolean showCheckbox) {
	    super(html);
	    initGui(html, showCheckbox);
    }

	public void setSelected(boolean selected) {
	    checkbox.setValue(selected);
	    this.setHTML(panel.getElement().getInnerHTML());	    
    }
	
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
		return checkbox.addValueChangeHandler(handler);
	}

	public boolean isSelected() {
	    return checkbox.getValue() == true;
    }
	
	

}
