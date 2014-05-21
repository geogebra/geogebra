package geogebra.web.gui.view.functioninspector;

import org.gwt.advanced.client.datamodel.Editable;
import org.gwt.advanced.client.datamodel.EditableGridDataModel;
import org.gwt.advanced.client.ui.widget.GridPanel;
import org.gwt.advanced.client.ui.widget.cell.LabelCell;

public class InspectorTableW extends GridPanel{
	public static final int INTERVAL_TABLE = 1;
	public static final int POINTS_TABLE = 1;
	
	private Editable model;
	public InspectorTableW(int tableType) {
		model = new EditableGridDataModel(new Object[][]{new String[]{"w","q"}});
		
		createEditableGrid(
		        new String[]{"Prop", "Val"},
		        new Class[]{LabelCell.class, LabelCell.class},
		        null
		    ).setModel(model); 
		setTopToolbarVisible(false);
		setTopPagerVisible(false);
		setArrowsVisible(false);
		setBottomToolbarVisible(false);
		setPageNumberBoxDisplayed(false);
		setBottomPagerVisible(false);
		setBorderWidth(1);
		adjust();
		display();
	}
	
	public Editable getModel() {
	    return model;
    }
	public void setModel(Editable model) {
	    this.model = model;
    }

}
