package geogebra.web.cas.view;

import geogebra.common.cas.view.CASSubDialog;
import geogebra.common.cas.view.CASView;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.web.gui.app.VerticalPanelSmart;
import geogebra.web.main.AppW;

import java.util.List;
import java.util.Vector;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DialogBox.Caption;
import com.google.gwt.user.client.ui.DialogBox.CaptionImpl;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Dialog to substitute expressions in CAS Input.
 * @author balazs.bencze
 *
 */
public class CASSubDialogW extends CASSubDialog {
	
	private Button btSub, btEval, btNumeric;
	private Panel optionPane, btPanel;
	
	private DialogBox dialog;
	private CellTable<SubstituteValue> table;
	private SimplePager pager;
	private List<SubstituteValue> list;
	
	private AppW app;
	private CASViewW casView;

	/**
	 * Substitute dialog for CAS.
	 * 
	 * @param casView view
	 * @param prefix
	 *            before selection, not effected by the substitution
	 * @param evalText
	 *            the String which will be substituted
	 * @param postfix
	 *            after selection, not effected by the substitution
	 * @param editRow row to edit
	 */
	public CASSubDialogW(CASViewW casView, String prefix, String evalText, String postfix, int editRow) {
		super(prefix, evalText, postfix, editRow);
		App.debug(prefix + " " + evalText + " " + postfix + " :" + editRow );
		
		this.casView = casView;
		this.app = casView.getApp();
		
		createGUI();
	}

	private void createGUI() {
		Caption caption = new CaptionImpl();
		Localization loc = app.getLocalization();
		caption.setText(loc.getPlain("Substitute") + " - " + loc.getCommand("Row") + " " + (editRow + 1));
		dialog = new DialogBox(caption);
		dialog.setAutoHideEnabled(true);
		dialog.setModal(false);
		//App.debug("after loc before cell");
		GeoCasCell cell = casView.getConsoleTable().getGeoCasCell(editRow);
		initData(cell);
		//App.debug(cell.getAlgebraDescriptionDefault());
		table = new CellTable<SubstituteValue>();
		table.setWidth(DEFAULT_TABLE_WIDTH + "");
		table.setHeight(DEFAULT_TABLE_HEIGHT + "");
		// do not refresh the headers and footers every time the data is updated
		table.setAutoHeaderRefreshDisabled(true);
		table.setAutoFooterRefreshDisabled(true);
		
		// create a Pager to control the table
	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
	    pager.setDisplay(table);
	    
		//App.debug("creating table");
		initData(cell);
		createTableColumns();
		fillTableColumns();

		//double fontFactor = Math.max(1, app.getGUIFontSize() / DEFAULT_FONT_SIZE);

		//App.debug("creating buttons");
		// buttons
		btEval = new Button("=");
		btNumeric = new Button("\u2248");
		btSub = new Button(loc.getPlain("\u2713"));
		
		btPanel = new HorizontalPanel();
		btPanel.add(btEval);
		btPanel.add(btNumeric);
		btPanel.add(btSub);

		// Create the JOptionPane.
		optionPane = new VerticalPanelSmart();

		// create object list
		optionPane.add(table);
		
		optionPane.add(btPanel);
		//App.debug("before setwidget");
		// Make this dialog display it.
		dialog.setWidget(optionPane);
		
    }

	private void fillTableColumns() {
		ListDataProvider<SubstituteValue> dataProvider = new ListDataProvider<CASSubDialog.SubstituteValue>();
		dataProvider.addDataDisplay(table);
		list = dataProvider.getList();
	    for (int i = 0; i < data.size(); i++) {
	    	Vector<String> vec = data.get(i);
	    	list.add(new SubstituteValue(vec.get(0), vec.get(1)));
	    }
	    
    }

	private void createTableColumns() {
		// old expression column
	    Column<SubstituteValue, String> oldVal = new Column<CASSubDialogW.SubstituteValue, String>(new EditTextCell()) {
			@Override
			public String getValue(SubstituteValue object) {
				// TODO Auto-generated method stub
				return object.getVariable();
			}
		};
		table.addColumn(oldVal, app.getPlain("OldExpression"));
		table.setColumnWidth(oldVal, 40, Unit.PX);
		
		Column<SubstituteValue, String> newVal = new Column<CASSubDialogW.SubstituteValue, String>(new EditTextCell()) {
			@Override
			public String getValue(SubstituteValue object) {
				return object.getValue();
			}
		};
		table.addColumn(newVal, app.getPlain("NewExpression"));
		table.setColumnWidth(newVal, 40, Unit.PX);	    
    }

	@Override
    protected CASView getCASView() {
	    return casView;
    }

	/**
	 * @return dialog
	 */
	public DialogBox getDialog() {
		return dialog;
	}
}
