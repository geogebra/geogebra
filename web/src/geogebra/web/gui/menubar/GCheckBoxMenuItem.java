package geogebra.web.gui.menubar;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuItem;

public class GCheckBoxMenuItem extends MenuItem {

	private FlowPanel panel;
	private CheckBox checkbox;

	/**
	 * @param html
	 * creates a new GChecbox menuitem
	 */
	public GCheckBoxMenuItem(SafeHtml html) {
	    super(html);
	    checkbox = new CheckBox();
	    panel = new FlowPanel();
	    panel.add(checkbox);
	    panel.add(new HTML(html));
	    this.setHTML(panel.getElement().getInnerHTML());
    }

	public void setSelected(boolean selected) {
	    checkbox.setValue(selected);
	    this.setHTML(panel.getElement().getInnerHTML());
	    
    }
	
	

}
