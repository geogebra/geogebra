package org.geogebra.web.web.cas.view;

import java.util.List;
import java.util.Vector;

import org.geogebra.common.cas.view.CASSubDialog;
import org.geogebra.common.cas.view.CASView;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DialogBox.Caption;
import com.google.gwt.user.client.ui.DialogBox.CaptionImpl;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Dialog to substitute expressions in CAS Input.
 * 
 * @author balazs.bencze
 *
 */
public class CASSubDialogW extends CASSubDialog implements ClickHandler {

	private Button btSub, btEval, btNumeric;
	private VerticalPanel optionPane;
	private ScrollPanel tablePane;
	private HorizontalPanel btPanel;

	private DialogBox dialog;
	private CellTable<SubstituteValue> table;
	private List<SubstituteValue> list;

	private AppW app;
	private CASViewW casView;

	private static final int DEFAULT_TABLE_WIDTH = 225;
	private static final int DEFAULT_TABLE_HEIGHT = 240;
	private static final int DEFAULT_BUTTON_WIDTH = 40;

	/**
	 * Substitute dialog for CAS.
	 * 
	 * @param casView
	 *            view
	 * @param prefix
	 *            before selection, not effected by the substitution
	 * @param evalText
	 *            the String which will be substituted
	 * @param postfix
	 *            after selection, not effected by the substitution
	 * @param editRow
	 *            row to edit
	 */
	public CASSubDialogW(CASViewW casView, String prefix, String evalText,
	        String postfix, int editRow) {
		super(prefix, evalText, postfix, editRow);

		this.casView = casView;
		this.app = casView.getApp();

		createGUI();
	}

	private void createGUI() {
		Caption caption = new CaptionImpl();
		Localization loc = app.getLocalization();
		caption.setText(loc.getPlain("Substitute") + " - "
		        + loc.getCommand("Row") + " " + (editRow + 1));
		dialog = new DialogBox(true, false, caption);
		dialog.addStyleName("CAS_subDialog");
		dialog.addStyleName("GeoGebraPopup");
		dialog.setWidget(optionPane = new VerticalPanel());
		optionPane.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		dialog.setAutoHideEnabled(true);
		dialog.setGlassEnabled(true);

		GeoCasCell cell = casView.getConsoleTable().getGeoCasCell(editRow);
		initData(cell);

		table = new CellTable<SubstituteValue>();
		// do not refresh the headers and footers every time the data is updated
		table.setAutoHeaderRefreshDisabled(true);
		table.setAutoFooterRefreshDisabled(true);

		initData(cell);
		createTableColumns();
		fillTableColumns();

		// buttons
		btEval = new Button(EVAL_SYM);
		btEval.setTitle(loc.getMenuTooltip("Evaluate"));
		btEval.addClickHandler(this);

		btNumeric = new Button(NUM_SYM);
		btNumeric.setTitle(loc.getMenuTooltip("Numeric"));
		btNumeric.addClickHandler(this);

		btSub = new Button(loc.getPlain(SUB_SYM));
		btSub.setTitle(loc.getMenuTooltip("Substitute"));
		btSub.addClickHandler(this);

		btPanel = new HorizontalPanel();

		tablePane = new ScrollPanel(table);
		tablePane.setWidth(DEFAULT_TABLE_WIDTH + "px");
		tablePane.setHeight(DEFAULT_TABLE_HEIGHT + "px");

		optionPane.add(tablePane);
		optionPane.add(btPanel);

		btPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		btPanel.add(btEval);
		btPanel.setCellWidth(btEval, DEFAULT_BUTTON_WIDTH + "px");
		btPanel.add(btNumeric);
		btPanel.setCellWidth(btNumeric, DEFAULT_BUTTON_WIDTH + "px");
		btPanel.add(btSub);
		btPanel.setCellWidth(btSub, DEFAULT_BUTTON_WIDTH + "px");
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
		Column<SubstituteValue, String> oldVal = new Column<CASSubDialogW.SubstituteValue, String>(
		        new EditTextCell()) {
			@Override
			public String getCellStyleNames(Context context,
			        SubstituteValue object) {
				return "CAS_substitute_editTextCell";
			}

			@Override
			public String getValue(SubstituteValue object) {
				return object.getVariable();
			}
		};
		table.addColumn(oldVal, app.getPlain("OldExpression"));
		table.setColumnWidth(oldVal, 40, Unit.PX);
		oldVal.setFieldUpdater(new FieldUpdater<CASSubDialog.SubstituteValue, String>() {
			public void update(int index, SubstituteValue object, String value) {
				object.setVariable(value);
				if ((index == (getTable().getRowCount() - 1))
				        && object.getValue() != null
				        && object.getVariable() != null
				        && !"".equals(object.getValue())
				        && !"".equals(object.getVariable())) {
					getList().add(new SubstituteValue("", ""));
				}
			}
		});

		Column<SubstituteValue, String> newVal = new Column<CASSubDialogW.SubstituteValue, String>(
		        new EditTextCell()) {
			@Override
			public String getCellStyleNames(Context context,
			        SubstituteValue object) {
				return "CAS_substitute_editTextCell";
			}

			@Override
			public String getValue(SubstituteValue object) {
				return object.getValue();
			}
		};
		table.addColumn(newVal, app.getPlain("NewExpression"));
		table.setColumnWidth(newVal, 40, Unit.PX);
		newVal.setFieldUpdater(new FieldUpdater<CASSubDialog.SubstituteValue, String>() {
			public void update(int index, SubstituteValue object, String value) {
				object.setValue(value);
				if ((index == (getTable().getRowCount() - 1))
				        && object.getValue() != null
				        && object.getVariable() != null
				        && !"".equals(object.getValue())
				        && !"".equals(object.getVariable())) {
					getList().add(new SubstituteValue("", ""));
				}
			}
		});
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

	public void onClick(ClickEvent event) {
		Object src = event.getSource();
		stopEditing();
		if (btEval == src) {
			if (apply(ACTION_EVALUATE))
				dialog.hide(false);
		} else if (btNumeric == src) {
			if (apply(ACTION_NUMERIC))
				dialog.hide(false);
		} else if (btSub == src) {
			if (apply(ACTION_SUBSTITUTE))
				dialog.hide(false);
		}
	}

	private void stopEditing() {
		data.setSize(list.size());
		for (int i = 0; i < list.size(); i++) {
			Vector<String> vec = data.get(i);
			if (vec == null) {
				vec = new Vector<String>();
				vec.setSize(2);
				data.set(i, vec);
			}
			vec.set(0, list.get(i).getVariable());
			vec.set(1, list.get(i).getValue());
		}
	}

	/**
	 * @return list of substitution values
	 */
	public List<SubstituteValue> getList() {
		return list;
	}

	/**
	 * @return CellTable showing the list of substitution values
	 */
	public CellTable<SubstituteValue> getTable() {
		return table;
	}
}
