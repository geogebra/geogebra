package geogebra.web.cas.view;

import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.TextProperties;
import geogebra.common.main.App;
import geogebra.common.util.debug.Log;
import geogebra.html5.awt.GColorW;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.awt.GFontW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.util.GeoGebraIcon;
import geogebra.web.gui.util.MyToggleButton2;
import geogebra.web.gui.util.StyleBarW;
import geogebra.web.main.AppW;

import java.util.ArrayList;

import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;


public class CASStylebarW extends StyleBarW implements ClickHandler{
	
	private MyToggleButton2 btnUseAsText;
	private AppW app;
	private int iconHeight = 18;
	private GDimensionW iconDimension = new GDimensionW(16, iconHeight);
	private MyToggleButton2 btnBold;
	private MyToggleButton2 btnItalic;
	private boolean needUndo=false;
	private ArrayList<GeoElement> selectedRows;
	private CASViewW casView;
	private MyToggleButton2[] toggleBtnList;

	public CASStylebarW(CASViewW view, AppW appl){
		casView = view;
		app = appl;
		selectedRows = new ArrayList<GeoElement>();
		initGUI();
	}
	
	private void initGUI(){
		createTextButtons();
		add(btnUseAsText);
//		add(btnTextColor);
		add(btnBold);
//		add(btnItalic);
		toggleBtnList = newToggleBtnList();
		updateStyleBar();
	}

	private void createTextButtons(){
		ImageData useAsTextIcon = GeoGebraIcon.createStringIcon(
				app.getPlain("Text").substring(0, 1), (GFontW) app.getPlainFontCommon(), true,
				false, true, iconDimension , GColorW.black, null); 
		btnUseAsText = new MyToggleButton2(useAsTextIcon, iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
			
				setVisible(true);
				btnUseAsText.setSelected(checkGeoText(geos));

			}
		};
		btnUseAsText.addClickHandler(this);
		
		btnBold = new MyToggleButton2(
				AppResources.INSTANCE.format_text_bold(),
				iconHeight){
			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();
					int style = ((TextProperties) geo).getFontStyle();
					btnBold.setValue(style == GFontW.BOLD
							|| style == (GFontW.BOLD + geogebra.common.awt.GFont.ITALIC));
				}
			}
		};
		btnBold.addClickHandler(this);
	}
	
	/**
	 * @param geos
	 *            list of selected cells
	 * @return whether all given objects are cells in text mode
	 */
	static boolean checkGeoText(Object[] geos) {
		boolean geosOK = (geos.length > 0);
		for (int i = 0; i < geos.length; i++) {
			if (!(((GeoElement) geos[i]).getGeoElementForPropertiesDialog() instanceof GeoCasCell)) {
				geosOK = false;
				break;
			} else if (!((GeoCasCell) geos[i]).isUseAsText()) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}
	
	public int getOffsetHeight() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public void setOpen(boolean showStyleBar) {
	    // TODO Auto-generated method stub
	    
    }
	
	public void onClick(ClickEvent e){
		Object source = e.getSource();

		needUndo  = false;

		processSource(source, selectedRows);

		if (needUndo) {
			app.storeUndoInfo();
			needUndo = false;
		}		
	}
	
	private void processSource(Object source,
            ArrayList<GeoElement> targetGeos) {
		if (source == btnBold){
			applyFontStyle(targetGeos);
		} else if (source == btnUseAsText) {
//			btnUseAsText.onClick(null);
			int i = casView.getConsoleTable().getEditingRow();
//			int pos = (casView.getConsoleTable().getEditor().getCaretPosition();
//			casView.getConsoleTable().stopEditing();
			applyUseAsText(targetGeos);
			casView.getConsoleTable().startEditingRow(i);

//			((CASTableCellEditorW)casView.getConsoleTable().getCellEditor(i,CASTableW.COL_CAS_CELLS)).setCaretPosition(pos);
			
		
		
//			casView.getConsoleTable().stopEditing();
		}
	    
    }
	
	private void applyFontStyle(ArrayList<GeoElement> geos) {
		int fontStyle = 0;
		if (btnBold.isSelected())
			fontStyle += 1;
//		if (btnItalic.isSelected())
//			fontStyle += 2;
		App.printStacktrace("geos-size: " +geos.size()+"");
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			Log.debug(((GeoCasCell) geo).getGeoText());
			if (geo instanceof GeoCasCell
					&& ((GeoCasCell) geo).getGeoText().getFontStyle() != fontStyle) {
				((GeoCasCell) geo).getGeoText().setFontStyle(fontStyle);
				geo.updateRepaint();
				needUndo = true;
			}
		}
	}

	/**
	 * 
	 * @param geo
	 *            selected cell
	 */
	public void setSelectedRow(GeoElement geo) {
		selectedRows.clear();
		selectedRows.add(geo);
		updateStyleBar();
	}
	
	public void updateStyleBar(){
		/*
		for (int i = 0; i < popupBtnList.length; i++) {
			try {
				popupBtnList[i].update(selectedRows.toArray());
			} catch (Exception e) {
				// TODO: find problem
			}
		}
		*/
		for (int i = 0; i < toggleBtnList.length; i++) {
			try {
				toggleBtnList[i].update(selectedRows.toArray());
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: find problem
			}
		}

	}
	
	/**
	 * @return array of toggle buttons
	 */
	protected MyToggleButton2[] newToggleBtnList() {
		return new MyToggleButton2[] { btnBold,/* btnItalic,*/ btnUseAsText };
	}
	
	private void applyUseAsText(ArrayList<GeoElement> geos) {
		casView.getConsoleTable().stopEditing();
		// btnUseAsText
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof GeoCasCell) {
				((GeoCasCell) geo).setUseAsText(btnUseAsText.isSelected());
				geo.updateRepaint();
				needUndo = true;
			}
		}
	}

}
