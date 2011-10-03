package geogebra.euclidian;

import geogebra.gui.color.ColorPopupMenuButton;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.AlgoTableText;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoText;
import geogebra.kernel.PointProperties;
import geogebra.kernel.TextProperties;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;
import geogebra.main.MyError;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
/**
 * Stylebar for the Euclidian Views
 * 
 * @author G. Sturr 
 *
 */
public class EuclidianStyleBar extends JToolBar implements ActionListener {
		
	/***/
	private static final long serialVersionUID = 1L;

	// ggb
	private EuclidianController ec;
	protected EuclidianViewInterface ev;
	protected Application app;
	private Construction cons; 
	
	
	// buttons and lists of buttons
	private ColorPopupMenuButton btnColor,btnBgColor, btnTextColor;
	
	private PopupMenuButton   btnLineStyle, btnPointStyle, btnTextSize, btnMode, 
		btnTableTextJustify, btnTableTextBracket, btnLabelStyle, btnPointCapture;
	
	private MyToggleButton btnCopyVisualStyle, btnPen, btnShowGrid, btnShowAxes,
    		btnBold, btnItalic, btnDelete, btnLabel, btnPenEraser, btnHideShowLabel, 
    		btnTableTextLinesV, btnTableTextLinesH;
	
	private PopupMenuButton[] popupBtnList;
	private MyToggleButton[] toggleBtnList;
	private JButton btnPenDelete, btnDeleteGeo;
	
	
	// fields for setting/unsetting default geos 
	private HashMap<Integer,Integer> defaultGeoMap;
	private ArrayList<GeoElement> defaultGeos;
	private GeoElement oldDefaultGeo;
	
	// flags and constants
	protected int iconHeight = 18;
	private Dimension iconDimension = new Dimension(16, iconHeight);
	public int mode = -1;
	private boolean isIniting;
	private boolean needUndo = false;
	private Integer oldDefaultMode;
	private boolean modeChanged = true;
	
	
	// button-specific fields  
	// TODO: create button classes so these become internal
	private AlgoTableText tableText;
	private Integer[] lineStyleArray, pointStyleArray;
	private HashMap<Integer, Integer> lineStyleMap, pointStyleMap;
	private final String[] bracketArray = { "\u00D8" , "{ }" , "( )", "[ ]", "| |", "|| ||"};
	private final String[] bracketArray2 = { "\u00D8" , "{ }" , "( )", "[ ]", "||", "||||"};

	
	
	
	/*************************************************
	 * Constructs a styleBar
	 * @param ev view
	 */
	public EuclidianStyleBar(EuclidianViewInterface ev) {
		
		isIniting = true;
		
		this.ev = ev;
		ec = ev.getEuclidianController(); 
		app = ev.getApplication();
		cons = app.getKernel().getConstruction();
		
		// init handling of default geos
		createDefaultMap();
		defaultGeos = new ArrayList<GeoElement>();	
		
		// toolbar display settings 
		setFloatable(false);
		Dimension d = getPreferredSize();
		d.height = iconHeight+8;
		setPreferredSize(d);

		// init button-specific fields
		// TODO: put these in button classes
		pointStyleArray = EuclidianView.getPointStyles();
		pointStyleMap = new HashMap<Integer,Integer>();
		for(int i = 0; i < pointStyleArray.length; i++)
			pointStyleMap.put(pointStyleArray[i], i);

		lineStyleArray = EuclidianView.getLineTypes();
		lineStyleMap = new HashMap<Integer,Integer>();
		for(int i = 0; i < lineStyleArray.length; i++)
			lineStyleMap.put(lineStyleArray[i], i);
		
		
		initGUI();
		isIniting = false;
		
		setMode(ev.getMode()); //this will also update the stylebar
	}
	
	
	public int getMode() {
		return mode;
	}

	/**
	 * Handles ggb mode changes.
	 * @param mode new mode
	 */
	public void setMode(int mode) {
	
		if(this.mode == mode){
			modeChanged = false;
			return;
		}else{
			modeChanged = true;
			this.mode = mode;
		}
		
		// MODE_TEXT temporarily switches to  MODE_SELECTION_LISTENER 
		// so we need to ignore this.
		if(mode == EuclidianConstants.MODE_SELECTION_LISTENER){
			modeChanged = false;
			return;
		}
		
		updateStyleBar();
		
	}
	
	
	protected boolean isVisibleInThisView(GeoElement geo){
		return geo.isVisibleInView(ev.getViewID()) ;
	}
	
	
	public void restoreDefaultGeo(){
		if(oldDefaultGeo != null)
		oldDefaultGeo = cons.getConstructionDefaults().getDefaultGeo(oldDefaultMode);
	}
	
	
	/**
	 * Updates the state of the stylebar buttons and the defaultGeo field.
	 */
	public void updateStyleBar(){

		if(mode == EuclidianConstants.MODE_VISUAL_STYLE) return;

		//-----------------------------------------------------
		// Create activeGeoList, a list of geos the stylebar can adjust.
		// These are either the selected geos or the current default geo. 
		// Each button uses this list to update its gui and set visibility
		//-----------------------------------------------------
		ArrayList<GeoElement> activeGeoList = new ArrayList<GeoElement>();

		
		//-----------------------------------------------------
		// MODE_MOVE case: load activeGeoList with all selected geos
		//-----------------------------------------------------
		if(mode == EuclidianConstants.MODE_MOVE){

			boolean hasGeosInThisView = false;
			for(GeoElement geo: ev.getApplication().getSelectedGeos()){
				if(isVisibleInThisView(geo) && geo.isEuclidianVisible() ){
					hasGeosInThisView = true;
					break;
				}
			}
			for(GeoElement geo: ec.getJustCreatedGeos()) {
				if(isVisibleInThisView(geo) && geo.isEuclidianVisible() ){
					hasGeosInThisView = true;
					break;
				}
			}
			if(hasGeosInThisView) { 
				activeGeoList = ev.getApplication().getSelectedGeos();
				
				// we also update stylebars according to just created geos
				activeGeoList.addAll(ec.getJustCreatedGeos());
			}
		}

		//-----------------------------------------------------
		// All other modes: load activeGeoList with current default geo 
		//-----------------------------------------------------
		else if (defaultGeoMap.containsKey(mode)){

			// Save the current default geo state in oldDefaultGeo. 
			// Stylebar buttons can temporarily change a default geo, but this default  
			// geo is always restored to its previous state after a mode change.

			if(oldDefaultGeo != null && modeChanged){
				// add oldDefaultGeo to the default map so that the old default is restored
				cons.getConstructionDefaults().addDefaultGeo(oldDefaultMode, oldDefaultGeo);
				oldDefaultGeo = null;
				oldDefaultMode = null;
			}

			// get the current default geo
			GeoElement geo = cons.getConstructionDefaults().getDefaultGeo(defaultGeoMap.get(mode));
			if (geo != null) activeGeoList.add(geo);

			// update the defaultGeos field (needed elsewhere for adjusting default geo state)
			defaultGeos = activeGeoList;

			// update oldDefaultGeo
			if (modeChanged) {
				if (defaultGeos.size() == 0) {
					oldDefaultGeo = null;
					oldDefaultMode = -1;
				} else {
					oldDefaultGeo = defaultGeos.get(0);  
					oldDefaultMode = defaultGeoMap.get(mode);
				}
			}
		
			// we also update stylebars according to just created geos
			activeGeoList.addAll(ec.getJustCreatedGeos());
		}


		//-----------------------------------------------------
		// update the buttons
		// note: this must always be done, even when activeGeoList is empty
		//-----------------------------------------------------
		updateTableText(activeGeoList.toArray());
		for(int i = 0; i < popupBtnList.length; i++){
			popupBtnList[i].update(activeGeoList.toArray());
		}
		for(int i = 0; i < toggleBtnList.length; i++){
			toggleBtnList[i].update(activeGeoList.toArray());
		}

		// show the pen delete button
		// TODO: handle pen mode in code above
		btnPenDelete.setVisible((mode == EuclidianConstants.MODE_PEN));

	}

	
	
	private void updateTableText(Object[] geos){

		tableText = null;
		if (geos == null || geos.length == 0 || mode == EuclidianConstants.MODE_PEN) return;

		boolean geosOK = true;
		AlgoElement algo;

		for (int i = 0; i < geos.length; i++) {
			algo = ((GeoElement)geos[i]).getParentAlgorithm();
			if(algo == null  || !(algo instanceof AlgoTableText)){
				geosOK = false;
			}			
		}

		if(geosOK && geos[0] != null){
			algo = ((GeoElement)geos[0]).getParentAlgorithm();
			tableText = (AlgoTableText) algo;
		}
	}



	private void createDefaultMap(){
		defaultGeoMap = new HashMap<Integer,Integer>();
		defaultGeoMap.put(EuclidianConstants.MODE_POINT, ConstructionDefaults.DEFAULT_POINT_FREE);
		defaultGeoMap.put(EuclidianConstants.MODE_COMPLEX_NUMBER, ConstructionDefaults.DEFAULT_POINT_FREE);
		defaultGeoMap.put(EuclidianConstants.MODE_POINT_ON_OBJECT, ConstructionDefaults.DEFAULT_POINT_DEPENDENT);
		defaultGeoMap.put(EuclidianConstants.MODE_INTERSECT, ConstructionDefaults.DEFAULT_POINT_DEPENDENT);
		defaultGeoMap.put(EuclidianConstants.MODE_MIDPOINT, ConstructionDefaults.DEFAULT_POINT_DEPENDENT);

		defaultGeoMap.put(EuclidianConstants.MODE_JOIN, ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_SEGMENT, ConstructionDefaults.DEFAULT_SEGMENT);
		defaultGeoMap.put(EuclidianConstants.MODE_SEGMENT_FIXED, ConstructionDefaults.DEFAULT_SEGMENT);
		defaultGeoMap.put(EuclidianConstants.MODE_RAY, ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_VECTOR, ConstructionDefaults.DEFAULT_VECTOR);
		defaultGeoMap.put(EuclidianConstants.MODE_VECTOR_FROM_POINT, ConstructionDefaults.DEFAULT_VECTOR);

		defaultGeoMap.put(EuclidianConstants.MODE_ORTHOGONAL, ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_PARALLEL, ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_LINE_BISECTOR, ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_ANGULAR_BISECTOR, ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_TANGENTS, ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_POLAR_DIAMETER, ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_FITLINE, ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_CREATE_LIST, ConstructionDefaults.DEFAULT_LIST);
		defaultGeoMap.put(EuclidianConstants.MODE_LOCUS, ConstructionDefaults.DEFAULT_LOCUS);

		defaultGeoMap.put(EuclidianConstants.MODE_POLYGON, ConstructionDefaults.DEFAULT_POLYGON);
		defaultGeoMap.put(EuclidianConstants.MODE_REGULAR_POLYGON, ConstructionDefaults.DEFAULT_POLYGON);
		defaultGeoMap.put(EuclidianConstants.MODE_RIGID_POLYGON, ConstructionDefaults.DEFAULT_POLYGON);
		defaultGeoMap.put(EuclidianConstants.MODE_VECTOR_POLYGON, ConstructionDefaults.DEFAULT_POLYGON);
		defaultGeoMap.put(EuclidianConstants.MODE_POLYLINE, ConstructionDefaults.DEFAULT_POLYGON);

		defaultGeoMap.put(EuclidianConstants.MODE_CIRCLE_TWO_POINTS, ConstructionDefaults.DEFAULT_CONIC) ;
		defaultGeoMap.put(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS, ConstructionDefaults.DEFAULT_CONIC) ;
		defaultGeoMap.put(EuclidianConstants.MODE_COMPASSES, ConstructionDefaults.DEFAULT_CONIC) ;
		defaultGeoMap.put(EuclidianConstants.MODE_CIRCLE_THREE_POINTS, ConstructionDefaults.DEFAULT_CONIC) ;
		defaultGeoMap.put(EuclidianConstants.MODE_SEMICIRCLE, ConstructionDefaults.DEFAULT_CONIC) ;
		defaultGeoMap.put(EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS, ConstructionDefaults.DEFAULT_CONIC) ;
		defaultGeoMap.put(EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS, ConstructionDefaults.DEFAULT_CONIC) ;
		defaultGeoMap.put(EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS, ConstructionDefaults.DEFAULT_CONIC_SECTOR) ;
		defaultGeoMap.put(EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS, ConstructionDefaults.DEFAULT_CONIC_SECTOR) ;

		defaultGeoMap.put(EuclidianConstants.MODE_ELLIPSE_THREE_POINTS, ConstructionDefaults.DEFAULT_CONIC) ;
		defaultGeoMap.put(EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS, ConstructionDefaults.DEFAULT_CONIC) ;
		defaultGeoMap.put(EuclidianConstants.MODE_PARABOLA, ConstructionDefaults.DEFAULT_CONIC) ;
		defaultGeoMap.put(EuclidianConstants.MODE_CONIC_FIVE_POINTS, ConstructionDefaults.DEFAULT_CONIC) ;

		defaultGeoMap.put(EuclidianConstants.MODE_ANGLE, ConstructionDefaults.DEFAULT_ANGLE) ;
		defaultGeoMap.put(EuclidianConstants.MODE_ANGLE_FIXED, ConstructionDefaults.DEFAULT_ANGLE) ;

		defaultGeoMap.put(EuclidianConstants.MODE_DISTANCE, ConstructionDefaults.DEFAULT_TEXT) ;
		defaultGeoMap.put(EuclidianConstants.MODE_AREA, ConstructionDefaults.DEFAULT_TEXT) ;
		defaultGeoMap.put(EuclidianConstants.MODE_SLOPE, ConstructionDefaults.DEFAULT_POLYGON) ;

		defaultGeoMap.put(EuclidianConstants.MODE_MIRROR_AT_LINE, ConstructionDefaults.DEFAULT_NONE) ;
		defaultGeoMap.put(EuclidianConstants.MODE_MIRROR_AT_POINT, ConstructionDefaults.DEFAULT_NONE) ;
		defaultGeoMap.put(EuclidianConstants.MODE_MIRROR_AT_CIRCLE, ConstructionDefaults.DEFAULT_NONE) ;
		defaultGeoMap.put(EuclidianConstants.MODE_ROTATE_BY_ANGLE, ConstructionDefaults.DEFAULT_NONE) ;
		defaultGeoMap.put(EuclidianConstants.MODE_TRANSLATE_BY_VECTOR, ConstructionDefaults.DEFAULT_NONE) ;
		defaultGeoMap.put(EuclidianConstants.MODE_DILATE_FROM_POINT, ConstructionDefaults.DEFAULT_NONE) ;

		defaultGeoMap.put(EuclidianConstants.MODE_TEXT, ConstructionDefaults.DEFAULT_TEXT) ;
		defaultGeoMap.put(EuclidianConstants.MODE_SLIDER, ConstructionDefaults.DEFAULT_NUMBER) ;
		defaultGeoMap.put(EuclidianConstants.MODE_IMAGE, ConstructionDefaults.DEFAULT_IMAGE) ;
		
		defaultGeoMap.put(EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX, ConstructionDefaults.DEFAULT_BOOLEAN) ;
		defaultGeoMap.put(EuclidianConstants.MODE_BUTTON_ACTION, ConstructionDefaults.DEFAULT_NONE) ;
		defaultGeoMap.put(EuclidianConstants.MODE_TEXTFIELD_ACTION, ConstructionDefaults.DEFAULT_NONE) ;
	}
	
	
	
	//=====================================================
	//                  Init  GUI
	//=====================================================
	
	private void initGUI() {
		
		removeAll();
		
		createButtons();
		createColorButton();
		createBgColorButton();
		createTextButtons();
		
		// add the buttons --- order matters here
		add(btnShowAxes);
		add(btnShowGrid);
		addBtnPointCapture();
		
		add(btnColor);
		add(btnBgColor);
		add(btnTextColor);
		add(btnLineStyle);
		add(btnPointStyle);
		add(btnBold);
		add(btnItalic);
		add(btnTextSize);
	
		createTableTextButtons();
		add(btnTableTextJustify);
		add(btnTableTextLinesV);
		add(btnTableTextLinesH);
		add(btnTableTextBracket);
		
	//	add(btnPenEraser);
		//add(btnHideShowLabel);
		add(btnLabelStyle);
		//add(btnPointCapture);
		addBtnRotateView();
	//	add(btnPenDelete);
			
		popupBtnList = newPopupBtnList();
		toggleBtnList = newToggleBtnList();
		
		for(int i=0; i< popupBtnList.length; i++){
			//popupBtnList[i].setStandardButton(true);
		}
		
		
	}
	
	protected PopupMenuButton[] newPopupBtnList(){
		return new PopupMenuButton[]{
				btnColor, btnBgColor, btnTextColor, btnLineStyle, btnPointStyle, btnTextSize, 
				btnTableTextJustify, btnTableTextBracket, btnLabelStyle, btnPointCapture};
	}
	
	protected MyToggleButton[] newToggleBtnList(){
		return new MyToggleButton[]{
				btnCopyVisualStyle, btnPen, btnShowGrid, btnShowAxes,
	            btnBold, btnItalic, btnDelete, btnLabel, btnPenEraser, btnHideShowLabel, btnTableTextLinesV, btnTableTextLinesH};
	}
	
	
	protected void addBtnPointCapture(){
		add(btnPointCapture);
	}
	
	protected void addBtnRotateView(){

	}
	
	//=====================================================
	//                 Create Buttons
	//=====================================================
	
	protected void createButtons() {
		
		//========================================
		// mode button
		
		ImageIcon[] modeArray = new ImageIcon[]{
				app.getImageIcon("cursor_arrow.png"),
				app.getImageIcon("applications-graphics.png"),
				app.getImageIcon("delete_small.gif"),
				app.getImageIcon("mode_point_16.gif"),
				app.getImageIcon("mode_copyvisualstyle_16.png")
		};
		btnMode = new PopupMenuButton(ev.getApplication(), modeArray, -1,1,
				new Dimension(20,iconHeight), SelectionTable.MODE_ICON);
		btnMode.addActionListener(this);
		btnMode.setKeepVisible(false);
		//add(btnMode);
		
		
		//========================================
		// pen button
		btnPen = new MyToggleButton(ev.getApplication().getImageIcon("applications-graphics.png")){
		      @Override
			public void update(Object[] geos) {
					this.setVisible((geos.length == 0 && mode == EuclidianConstants.MODE_MOVE) || mode == EuclidianConstants.MODE_PEN);	  
			      }
			};
		btnPen.addActionListener(this);
		//add(btnPen);

		
		//========================================
		// delete button
		btnDelete = new MyToggleButton(ev.getApplication().getImageIcon("delete_small.gif")){
		      @Override
			public void update(Object[] geos) {
					this.setVisible((geos.length == 0 && mode == EuclidianConstants.MODE_MOVE)  || mode == EuclidianConstants.MODE_DELETE);	  
			      }
			};
		btnDelete.addActionListener(this);
		//add(btnDelete);
		
		
		
		//========================================
		// hide/show labels button
		btnLabel = new MyToggleButton(ev.getApplication().getImageIcon("mode_copyvisualstyle_16.png")){
		      @Override
			public void update(Object[] geos) {
					this.setVisible((geos.length == 0 && mode == EuclidianConstants.MODE_MOVE) || mode == EuclidianConstants.MODE_SHOW_HIDE_LABEL);	  
			      }
			};
		btnLabel.addActionListener(this);
		//add(btnLabel);
		
		
		//========================================
		// visual style button
		
		btnCopyVisualStyle = new MyToggleButton(ev.getApplication().getImageIcon("mode_copyvisualstyle_16.png")){
		      @Override
			public void update(Object[] geos) {
				this.setVisible( (geos.length > 0 && mode == EuclidianConstants.MODE_MOVE) || mode == EuclidianConstants.MODE_VISUAL_STYLE);	  
		      }
		};
		btnCopyVisualStyle.addActionListener(this);
		//add(this.btnCopyVisualStyle);

		
		//========================================
		// show axes button	
		btnShowAxes = new MyToggleButton(app.getImageIcon("axes.gif")){
		      @Override
			public void update(Object[] geos) {
		    	// always show this button unless in pen mode
				this.setVisible(mode != EuclidianConstants.MODE_PEN);	  
		      }
		};
		
		//btnShowAxes.setPreferredSize(new Dimension(16,16));
		btnShowAxes.addActionListener(this);
		
		
		
		//========================================
		// show grid button
		btnShowGrid = new MyToggleButton(app.getImageIcon("grid.gif")){
		      @Override
			public void update(Object[] geos) {
		    	// always show this button unless in pen mode
					this.setVisible(mode != EuclidianConstants.MODE_PEN);	  
			      }
			};			
		//btnShowGrid.setPreferredSize(new Dimension(16,16));
		btnShowGrid.addActionListener(this);
		
	
		
	
		//========================================
		// line style button
		
		// create line style icon array
		final Dimension lineStyleIconSize = new Dimension(80,iconHeight);
		ImageIcon[] lineStyleIcons = new ImageIcon[lineStyleArray.length];
		for(int i=0; i < lineStyleArray.length; i++)
			lineStyleIcons[i] = GeoGebraIcon.createLineStyleIcon( lineStyleArray[i],  2, lineStyleIconSize,  Color.BLACK,  null);
		
		// create button
		btnLineStyle = new PopupMenuButton(app, lineStyleIcons, -1,1,
				lineStyleIconSize, SelectionTable.MODE_ICON){

			@Override
			public void update(Object[] geos) {

				if( mode == EuclidianConstants.MODE_PEN){
					this.setVisible(true);
					setFgColor(ec.getPen().getPenColor());
					setSliderValue(ec.getPen().getPenSize());
					setSelectedIndex(lineStyleMap.get(ec.getPen().getPenLineStyle()));
				}else{
					boolean geosOK = (geos.length > 0 );
					for (int i = 0; i < geos.length; i++) {
						GeoElement geo = ((GeoElement) geos[i]).getGeoElementForPropertiesDialog();
						if (!(geo.isPath()
								|| (geo.isGeoList() ? ((GeoList)geo).showLineProperties() : false )
								|| (geo.isGeoNumeric() ? ( ((GeoNumeric) geo).isDrawable() || ((GeoNumeric) geo).isSliderFixed() ) : false)
								|| geo.isGeoAngle())) {
							geosOK = false;
							break;
						}
					}

					this.setVisible(geosOK);

					if(geosOK){	
						//setFgColor(((GeoElement)geos[0]).getObjectColor());

						setFgColor(Color.black);
						setSliderValue( ((GeoElement)geos[0]).getLineThickness());

						setSelectedIndex(lineStyleMap.get(((GeoElement)geos[0]).getLineType()));

						this.setKeepVisible(mode == EuclidianConstants.MODE_MOVE);
					}							
				}
			}

			public ImageIcon getButtonIcon(){
				if(getSelectedIndex() > -1)
					return GeoGebraIcon.createLineStyleIcon( lineStyleArray[this.getSelectedIndex()],  
							this.getSliderValue(),  lineStyleIconSize,  Color.BLACK,  null);
				else 
					return GeoGebraIcon.createEmptyIcon(lineStyleIconSize.width, lineStyleIconSize.height);
			}
			
		};
		
		btnLineStyle.getMySlider().setMinimum(1);
		btnLineStyle.getMySlider().setMaximum(13);
		btnLineStyle.getMySlider().setMajorTickSpacing(2);
		btnLineStyle.getMySlider().setMinorTickSpacing(1);
		btnLineStyle.getMySlider().setPaintTicks(true);	
		btnLineStyle.addActionListener(this);
				
		
		//========================================
		// point style button

		// create line style icon array
		final Dimension pointStyleIconSize = new Dimension(20,iconHeight);
		ImageIcon[] pointStyleIcons = new ImageIcon[pointStyleArray.length];
		for(int i=0; i < pointStyleArray.length; i++)
			pointStyleIcons[i] = GeoGebraIcon.createPointStyleIcon( pointStyleArray[i],  4, pointStyleIconSize,  Color.BLACK,  null);
		
		// create button
		btnPointStyle = new PopupMenuButton(app, pointStyleIcons, 2, -1, 
				pointStyleIconSize, SelectionTable.MODE_ICON){

			@Override
			public void update(Object[] geos) {
				GeoElement geo;
				boolean geosOK = (geos.length > 0 );
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement)geos[i];
					if (!(geo.getGeoElementForPropertiesDialog().isGeoPoint())
							&& (!(geo.isGeoList() && ((GeoList)geo).showPointProperties()))) {
						geosOK = false;
						break;
					}
				}
				this.setVisible(geosOK);

				if(geosOK){					
					//setFgColor(((GeoElement)geos[0]).getObjectColor());
					setFgColor(Color.black);
					
					// if geo is a matrix, this will return a GeoNumeric...
					geo = ((GeoElement)geos[0]).getGeoElementForPropertiesDialog();
					
					// ... so need to check
					if (geo instanceof PointProperties) {
						setSliderValue( ((PointProperties)geo).getPointSize());
						int pointStyle = ((PointProperties)geo).getPointStyle();
						if(pointStyle == -1) // global default point style
				    		pointStyle = ev.getPointStyle();
						setSelectedIndex(pointStyleMap.get(pointStyle));
						this.setKeepVisible(mode == EuclidianConstants.MODE_MOVE);
					}
				}
			}
					
			public ImageIcon getButtonIcon(){
				if(getSelectedIndex() > -1)
					return GeoGebraIcon.createPointStyleIcon( pointStyleArray[this.getSelectedIndex()],  
							this.getSliderValue(),  pointStyleIconSize,  Color.BLACK,  null);
				else 
					return GeoGebraIcon.createEmptyIcon(pointStyleIconSize.width, pointStyleIconSize.height);
			}
			
			
			
		};
		btnPointStyle.getMySlider().setMinimum(1);
		btnPointStyle.getMySlider().setMaximum(9);
		btnPointStyle.getMySlider().setMajorTickSpacing(2);
		btnPointStyle.getMySlider().setMinorTickSpacing(1);
		btnPointStyle.getMySlider().setPaintTicks(true);		
		btnPointStyle.addActionListener(this);
		
	
		//========================================
		// eraser button
		btnPenEraser = new MyToggleButton(app.getImageIcon("delete_small.gif")){
			@Override
			public void update(Object[] geos) {
				this.setVisible(mode == EuclidianConstants.MODE_PEN);
			}	
		};
		btnPenEraser.addActionListener(this);
		
		
		//========================================
		// delete geo button
		btnDeleteGeo = new JButton(app.getImageIcon("delete_small.gif"));
		btnDeleteGeo.addActionListener(this);
		//add(btnDeleteGeo);
		
		
		
		//========================================
		// hide/show label button
		btnHideShowLabel = new MyToggleButton(app.getImageIcon("mode_showhidelabel_16.gif")){
			@Override
			public void update(Object[] geos) {
				// only show this button when handling selection, do not use it for defaults
				if(mode != EuclidianConstants.MODE_MOVE){
					this.setVisible(false);
					return;
				}
				boolean geosOK = (geos.length > 0);
				for (int i = 0; i < geos.length; i++) {
					if ((((GeoElement)geos[i]).getGeoElementForPropertiesDialog().isGeoText())) {
						geosOK = false;
						break;
					}
				}
				this.setVisible(geosOK);
				if(geosOK){	
					btnHideShowLabel.setSelected(((GeoElement)geos[0]).isLabelVisible());
				}
			}	
			
		};
		btnHideShowLabel.addActionListener(this);
		
		
		
		
		//========================================
		// caption style button

		String[] captionArray = new String[] {
				app.getPlain("stylebar.Hidden"), // index 4
				app.getPlain("Name"), // index 0
				app.getPlain("NameAndValue"), // index 1
				app.getPlain("Value"), // index 2
				app.getPlain("Caption") // index 3
				
		};


		btnLabelStyle = new PopupMenuButton(app, captionArray, -1, 1, 
				new Dimension(0, iconHeight), SelectionTable.MODE_TEXT){

			@Override
			public void update(Object[] geos) {
				boolean geosOK = false;
				GeoElement geo = null;
				if (mode == EuclidianConstants.MODE_MOVE) {
					for (int i = 0; i < geos.length; i++) {
						if (((GeoElement)geos[i]).isLabelShowable() ||
								((GeoElement)geos[i]).isGeoAngle() ||
								(((GeoElement)geos[i]).isGeoNumeric() ? ((GeoNumeric)geos[i]).isSliderFixed() : false)) {
							geo = (GeoElement)geos[i];
							geosOK = true;
							break;
						}
					}
				} else if (app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_ALWAYS_OFF) {
					this.setVisible(false);
					return;
				} else if (app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY) {
					for (int i = 0; i < geos.length; i++) {
					if (((GeoElement)geos[i]).isLabelShowable() &&
							((GeoElement)geos[i]).isGeoPoint()) {
							geo = (GeoElement)geos[i];
							geosOK = true;
							break;
						}
					}
				} else {
					for (int i = 0; i < geos.length; i++) {
						if (((GeoElement)geos[i]).isLabelShowable() ||
							((GeoElement)geos[i]).isGeoAngle() ||
							(((GeoElement)geos[i]).isGeoNumeric() ? ((GeoNumeric)geos[i]).isSliderFixed() : false)) {
							geo = (GeoElement)geos[i];
							geosOK = true;
							break;
						}
					}
				}
				this.setVisible(geosOK);
				
				if(geosOK){
					if(!geo.isLabelVisible())
						setSelectedIndex(0);
					else
						setSelectedIndex(geo.getLabelMode()+1);
					
				}
			}	
			
			public ImageIcon getButtonIcon(){
				return (ImageIcon) this.getIcon();
			}

			
		};	
		ImageIcon ic = app.getImageIcon("mode_showhidelabel_16.gif");
		btnLabelStyle.setIconSize(new Dimension(ic.getIconWidth(),iconHeight));
		btnLabelStyle.setIcon(ic);
		btnLabelStyle.addActionListener(this);
		btnLabelStyle.setKeepVisible(false);
		
		
		

		//========================================
		// point capture button
		
		String[] strPointCapturing = { 
				app.getMenu("Labeling.automatic"), 
				app.getMenu("SnapToGrid"),
				app.getMenu("FixedToGrid"), 
				app.getMenu("off") };

		btnPointCapture = new PopupMenuButton(app, strPointCapturing, -1, 1, 
				new Dimension(0, iconHeight), SelectionTable.MODE_TEXT){
			
			@Override
			public void update(Object[] geos) {
				// always show this button unless in pen mode
				this.setVisible(mode != EuclidianConstants.MODE_PEN);
				
			}	
					
			public ImageIcon getButtonIcon(){
				return (ImageIcon) this.getIcon();
			}

		};	
		ImageIcon ptCaptureIcon = app.getImageIcon("magnet.gif");
		btnPointCapture.setIconSize(new Dimension(ptCaptureIcon.getIconWidth(),iconHeight));
		btnPointCapture.setIcon(ptCaptureIcon);
		btnPointCapture.addActionListener(this);
		btnPointCapture.setKeepVisible(false);
		
		
		
		
		
		
		
		
		
		
		//========================================
		// pen delete button
		btnPenDelete = new JButton("\u2718");
		Dimension d = new Dimension(iconHeight,iconHeight);
		btnPenDelete.setPreferredSize(d);
		btnPenDelete.setMaximumSize(d);
		btnPenDelete.addActionListener(this);

	}

	
	
	
	
	
	
	//========================================
	// object color button  (color for everything except text)

	private void createColorButton(){
		
		final Dimension colorIconSize = new Dimension(20,iconHeight);
		btnColor = new ColorPopupMenuButton(app, colorIconSize, ColorPopupMenuButton.COLORSET_DEFAULT, true) {

			@Override
			public void update(Object[] geos) {

				if( mode == EuclidianConstants.MODE_PEN){
					this.setVisible(true);
					
					setSelectedIndex(getColorIndex(ec.getPen().getPenColor()));
					
					setSliderValue(100);
					getMySlider().setVisible(false);

				}else{
					boolean geosOK = (geos.length > 0 || mode == EuclidianConstants.MODE_PEN);
					for (int i = 0; i < geos.length; i++) {
						GeoElement geo = ((GeoElement)geos[i]).getGeoElementForPropertiesDialog();
						if (geo instanceof GeoImage || geo instanceof GeoText){
							geosOK = false;
							break;
						}
					}

					setVisible(geosOK);

					if(geosOK){
						// get color from first geo
						Color geoColor;
						geoColor = ((GeoElement) geos[0]).getObjectColor();
						
						// check if selection contains a fillable geo
						// if true, then set slider to first fillable's alpha value
						float alpha = 1.0f;
						boolean hasFillable = false;
						for (int i = 0; i < geos.length; i++) {
							if (((GeoElement) geos[i]).isFillable()) {
								hasFillable = true;
								alpha = ((GeoElement) geos[i]).getAlphaValue();
								break;
							}
						}
						
						if(hasFillable)
							setToolTipText(app.getPlain("stylebar.ColorTransparency"));
						else
							setToolTipText(app.getPlain("stylebar.Color"));
						
						getMySlider().setVisible(hasFillable);	
						setSliderValue(Math.round(alpha * 100));

						updateColorTable();
						
						// find the geoColor in the table and select it 
						int index = this.getColorIndex(geoColor);
						setSelectedIndex(index);
						setDefaultColor(alpha, geoColor);

						this.setKeepVisible(mode == EuclidianConstants.MODE_MOVE);
					}
				}
			}
					
		};

		btnColor.addActionListener(this);
	}




	private void createBgColorButton(){
		
		final Dimension bgColorIconSize = new Dimension(20,iconHeight);
		
		btnBgColor = new ColorPopupMenuButton(app, bgColorIconSize, ColorPopupMenuButton.COLORSET_BGCOLOR, false) {
			
			@Override
			public void update(Object[] geos) {

				boolean geosOK = (geos.length > 0);
				for (int i = 0; i < geos.length; i++) {
					GeoElement geo = ((GeoElement)geos[i]).getGeoElementForPropertiesDialog();
					if (!(geo instanceof GeoText)){
						geosOK = false;
						break;
					}
				}

				setVisible(geosOK);

				if(geosOK){
					// get color from first geo
					Color geoColor;
					geoColor = ((GeoElement) geos[0]).getBackgroundColor();
					
					/*
					// check if selection contains a fillable geo
					// if true, then set slider to first fillable's alpha value
					float alpha = 1.0f;
					boolean hasFillable = false;
					for (int i = 0; i < geos.length; i++) {
						if (((GeoElement) geos[i]).isFillable()) {
							hasFillable = true;
							alpha = ((GeoElement) geos[i]).getAlphaValue();
							break;
						}
					}
					getMySlider().setVisible(hasFillable);	
					setSliderValue(Math.round(alpha * 100));
					
					*/
					float alpha = 1.0f;
					updateColorTable();
					
					
					// find the geoColor in the table and select it 
					int index = getColorIndex(geoColor);
					setSelectedIndex(index);
					setDefaultColor(alpha, geoColor);
					
					// if nothing was selected, set the icon to show the non-standard color 
					if(index == -1){
						this.setIcon(GeoGebraIcon.createColorSwatchIcon( alpha, bgColorIconSize, geoColor, null));
					}	
				}
			}
		};
		btnBgColor.setKeepVisible(true);
		btnBgColor.addActionListener(this);
	}


	

	//=====================================================
	//           Text Format Buttons
	//=====================================================
	
	private boolean checkGeoText(Object[] geos){
		boolean geosOK = (geos.length > 0);
		for (int i = 0; i < geos.length; i++) {
			if (!(((GeoElement)geos[i]).getGeoElementForPropertiesDialog() instanceof TextProperties) 
					 ) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}
	
	
	
	private void createTextButtons() {	

		
		//========================
		// text color  button
		final Dimension textColorIconSize = new Dimension(20,iconHeight);

		btnTextColor = new ColorPopupMenuButton(app, textColorIconSize, ColorPopupMenuButton.COLORSET_DEFAULT, false) {

			private Color geoColor;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);

				if(geosOK){
					GeoElement geo = ((GeoElement)geos[0]).getGeoElementForPropertiesDialog(); 
					geoColor = geo.getObjectColor();
					updateColorTable();
					
					// find the geoColor in the table and select it 
					int index = this.getColorIndex(geoColor);
					setSelectedIndex(index);

					// if nothing was selected, set the icon to show the non-standard color 
					if(index == -1){
						this.setIcon(getButtonIcon());
					}

					
					setFgColor(geoColor);
					setFontStyle(((TextProperties)geo).getFontStyle());
				}
			}


			public ImageIcon getButtonIcon(){				
				return GeoGebraIcon.createTextSymbolIcon("A", app.getPlainFont(), textColorIconSize,  getSelectedColor(),  null);
			}

		};

		btnTextColor.addActionListener(this);

			
		//========================================
		// bold text button
		ImageIcon boldIcon = GeoGebraIcon.createStringIcon(app.getPlain("Bold").substring(0,1),
				app.getPlainFont(), true, false, true, iconDimension, Color.black, null);
		btnBold = new MyToggleButton(boldIcon){
			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				if(geosOK){
					GeoElement geo = ((GeoElement)geos[0]).getGeoElementForPropertiesDialog();
					int style = ((TextProperties)geo).getFontStyle();
					btnBold.setSelected(style == Font.BOLD || style == (Font.BOLD + Font.ITALIC));		
				}
			}		  
		};
		btnBold.addActionListener(this);



		//========================================
		// italic text button
		ImageIcon italicIcon = GeoGebraIcon.createStringIcon(app.getPlain("Italic").substring(0,1),
				app.getPlainFont(), false, true, true, iconDimension, Color.black, null);
		btnItalic = new MyToggleButton(italicIcon){
			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				this.setVisible(geosOK);
				if(geosOK){	
					GeoElement geo = ((GeoElement)geos[0]).getGeoElementForPropertiesDialog();
					int style = ((TextProperties)geo).getFontStyle();
					btnItalic.setSelected(style == Font.ITALIC || style == (Font.BOLD + Font.ITALIC));
				}
			}	

		};
		btnItalic.addActionListener(this);


		//========================================
		// text size button

		String[] textSizeArray = app.getFontSizeStrings();
		
		btnTextSize = new PopupMenuButton(app, textSizeArray, -1, 1, 
				new Dimension(-1, iconHeight), SelectionTable.MODE_TEXT){

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);

				if(geosOK){
					GeoElement geo = ((GeoElement)geos[0]).getGeoElementForPropertiesDialog();
					setSelectedIndex(GeoText.getFontSizeIndex(((TextProperties)geo).getFontSize())); // font size ranges from -4 to 4, transform this to 0,1,..,4
				}
			}		  
		};	
		btnTextSize.addActionListener(this);
		btnTextSize.setKeepVisible(false);
		
		

	}

	
	
	
	//================================================
	//      Create TableText buttons
	//================================================
	
	
	private void createTableTextButtons(){
		Dimension iconDimension = new Dimension(16, iconHeight);
		
		//==============================
		// justification popup
		ImageIcon[] justifyIcons = new ImageIcon[]{
				app.getImageIcon("format-justify-left.png"),
				app.getImageIcon("format-justify-center.png"),
				app.getImageIcon("format-justify-right.png")
		};
		btnTableTextJustify = new PopupMenuButton(ev.getApplication(), justifyIcons, 1,-1,
				new Dimension(20,iconHeight), SelectionTable.MODE_ICON){
			@Override
			public void update(Object[] geos) {
				if(tableText != null){					
					this.setVisible(true);
					String justification = tableText.getJustification(); 
					if(justification.equals("c")) btnTableTextJustify.setSelectedIndex(1);
					else if (justification.equals("r")) btnTableTextJustify.setSelectedIndex(2);
					else btnTableTextJustify.setSelectedIndex(0); //left align
					
				}else{
					this.setVisible(false);
				}
			}
		};
		
		btnTableTextJustify.addActionListener(this);
		btnTableTextJustify.setKeepVisible(false);	
	
		
		//==============================
		// bracket style popup

		
	
		ImageIcon[] bracketIcons = new ImageIcon[bracketArray.length];
		for(int i = 0; i<bracketIcons.length; i++){
				bracketIcons[i] = GeoGebraIcon.createStringIcon(bracketArray[i], app.getPlainFont(), true, false, true, new Dimension(30,iconHeight) , Color.BLACK, null);
		}
		
		btnTableTextBracket = new PopupMenuButton(ev.getApplication(), bracketIcons, 2,-1,
				new Dimension(30,iconHeight), SelectionTable.MODE_ICON){
			@Override
			public void update(Object[] geos) {
				if(tableText != null){					
					this.setVisible(true);
					String s = tableText.getOpenSymbol() + " " + tableText.getCloseSymbol(); 
					int index = 0;
					for(int i = 0; i < bracketArray.length; i++){
						if(s.equals(bracketArray[i])) {
							index = i;
							break;
						}
					}
					//System.out.println("index" + index);
					btnTableTextBracket.setSelectedIndex(index); 
					
				}else{
					this.setVisible(false);
				}
			}
		};
		
		btnTableTextBracket.addActionListener(this);
		btnTableTextBracket.setKeepVisible(false);	
		
		
		
		
		//====================================
		// vertical grid lines toggle button
		btnTableTextLinesV = new MyToggleButton(GeoGebraIcon.createVGridIcon(iconDimension)){
			@Override
			public void update(Object[] geos) {
				if(tableText != null){
					setVisible(true);
					setSelected(tableText.isVerticalLines());
				}else{
					setVisible(false);
				}
			}	
		};
		btnTableTextLinesV.addActionListener(this);
		
		//====================================
		// horizontal grid lines toggle button
		btnTableTextLinesH = new MyToggleButton(GeoGebraIcon.createHGridIcon(iconDimension)){
			@Override
			public void update(Object[] geos) {
				if(tableText != null){
					setVisible(true);
					setSelected(tableText.isHorizontalLines());
				}else{
					setVisible(false);
				}
			}
		};
		btnTableTextLinesH.addActionListener(this);
		
		
		
		
		
		
	}
		
		

	
	//=====================================================
	//                 Event Handlers
	//=====================================================
	
	
	protected void updateGUI(){

		if(isIniting) return;

		btnMode.removeActionListener(this);
		switch (mode){
		case EuclidianConstants.MODE_MOVE:
			btnMode.setSelectedIndex(0);
			break;
		case EuclidianConstants.MODE_PEN:
			btnMode.setSelectedIndex(1);
			break;
		case EuclidianConstants.MODE_DELETE:
			btnMode.setSelectedIndex(2);
			break;
		case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
			btnMode.setSelectedIndex(3);
			break;
		case EuclidianConstants.MODE_VISUAL_STYLE:
			btnMode.setSelectedIndex(4);
			break;
		}
		btnMode.addActionListener(this);	
		
		btnPen.removeActionListener(this);
		btnPen.setSelected(mode == EuclidianConstants.MODE_PEN);
		btnPen.addActionListener(this);
		
		btnDelete.removeActionListener(this);
		btnDelete.setSelected(mode == EuclidianConstants.MODE_DELETE);
		btnDelete.addActionListener(this);
		
		btnLabel.removeActionListener(this);
		btnLabel.setSelected(mode == EuclidianConstants.MODE_SHOW_HIDE_LABEL);
		btnLabel.addActionListener(this);
		
		btnShowAxes.removeActionListener(this);
		btnShowAxes.setSelected(ev.getShowXaxis());
		btnShowAxes.addActionListener(this);

		btnShowGrid.removeActionListener(this);
		btnShowGrid.setSelected(ev.getShowGrid());
		btnShowGrid.addActionListener(this);

	}

	
	
	

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
	
		// mode changing buttons, removed for now?
		/* 
		if (source.equals(btnMode)) {
			switch (btnMode.getSelectedIndex()){
			case 0:
				ev.getApplication().setMoveMode();
				break;
			case 1:
				ev.getApplication().setMode(EuclidianConstants.MODE_PEN);
				break;
			case 2:
				ev.getApplication().setMode(EuclidianConstants.MODE_DELETE);	
				break;
			case 3:
				ev.getApplication().setMode(EuclidianConstants.MODE_SHOW_HIDE_LABEL);
				break;
			case 4:
				ev.getApplication().setMode(EuclidianConstants.MODE_VISUAL_STYLE);
			}
		}

		if (source.equals(btnCopyVisualStyle)) {		
			if(btnCopyVisualStyle.isSelected())
				ev.getApplication().setMode(EuclidianConstants.MODE_VISUAL_STYLE);
			else
				ev.getApplication().setMoveMode();
		}


		if (source.equals(btnPen)) {		
			if(btnPen.isSelected())
				ev.getApplication().setMode(EuclidianConstants.MODE_PEN);
			else
				ev.getApplication().setMoveMode();
		}

		if (source.equals(btnDelete)) {		
			if(btnDelete.isSelected())
				ev.getApplication().setMode(EuclidianConstants.MODE_DELETE);
			else
				ev.getApplication().setMoveMode();
		}

		if (source.equals(btnLabel)) {		
			if(btnLabel.isSelected())
				ev.getApplication().setMode(EuclidianConstants.MODE_SHOW_HIDE_LABEL);
			else
				ev.getApplication().setMoveMode();
		}

	*/
		
		needUndo = false;


		ArrayList<GeoElement> targetGeos = new ArrayList<GeoElement>();
		targetGeos.addAll(ec.getJustCreatedGeos());
		if(mode != EuclidianConstants.MODE_MOVE)
			targetGeos.addAll(defaultGeos);
		else
			targetGeos.addAll(app.getSelectedGeos());

		
		
		processSource(source,targetGeos);
		
		

		if(needUndo){
			app.storeUndoInfo();
			needUndo = false;
		}
		
		updateGUI();
		
	}

	/**
	 * process the action performed
	 * @param source
	 * @param targetGeos
	 */
	protected void processSource(Object source, ArrayList<GeoElement> targetGeos){

		
		if (source.equals(btnShowAxes)) {		
			if (app.getEuclidianView() == ev)
				app.getSettings().getEuclidian(1).setShowAxes(!ev.getShowXaxis(), !ev.getShowXaxis());
			else if (!app.hasEuclidianView2EitherShowingOrNot())
				ev.setShowAxes(!ev.getShowXaxis(), true);
			else if (app.getEuclidianView2() == ev)
				app.getSettings().getEuclidian(2).setShowAxes(!ev.getShowXaxis(), !ev.getShowXaxis());
			else
				ev.setShowAxes(!ev.getShowXaxis(), true);
			ev.repaint();
		}

		else if (source.equals(btnShowGrid)) {
			if (app.getEuclidianView() == ev)
				app.getSettings().getEuclidian(1).showGrid(!ev.getShowGrid());
			else if (!app.hasEuclidianView2EitherShowingOrNot())
				ev.showGrid(!ev.getShowGrid());
			else if (app.getEuclidianView2() == ev)
				app.getSettings().getEuclidian(2).showGrid(!ev.getShowGrid());
			else
				ev.showGrid(!ev.getShowGrid());
			ev.repaint();
		}
		
		else if (source == btnPointCapture) {
			int mode = btnPointCapture.getSelectedIndex();
			if (mode == 3 || mode ==0) mode = 3 - mode; // swap 0 and 3
			ev.setPointCapturing(mode);	
		}
		
		else if (source == btnColor) {
			if(mode == EuclidianConstants.MODE_PEN){
				ec.getPen().setPenColor((Color) btnColor.getSelectedColor());
				//btnLineStyle.setFgColor((Color)btnColor.getSelectedValue());
			} else {
				applyColor(targetGeos);
				//btnLineStyle.setFgColor((Color)btnColor.getSelectedValue());
				//btnPointStyle.setFgColor((Color)btnColor.getSelectedValue());
			}
		}
		
		else if (source == btnBgColor) {
			if(btnBgColor.getSelectedIndex() >=0){
				applyBgColor(targetGeos);
			}
		}
		
		else if (source == btnTextColor) {
			if(btnTextColor.getSelectedIndex() >=0){
				applyTextColor(targetGeos);
				//btnTextColor.setFgColor((Color)btnTextColor.getSelectedValue());
				//btnItalic.setForeground((Color)btnTextColor.getSelectedValue());
				//btnBold.setForeground((Color)btnTextColor.getSelectedValue());
			}
		}
		else if (source == btnLineStyle) {
			if(btnLineStyle.getSelectedValue() != null){
				if(mode == EuclidianConstants.MODE_PEN){
					ec.getPen().setPenLineStyle(lineStyleArray[btnLineStyle.getSelectedIndex()]);
					ec.getPen().setPenSize(btnLineStyle.getSliderValue());
				} else {
					applyLineStyle(targetGeos);
				}

			}
		}
		else if (source == btnPointStyle) {
			if(btnPointStyle.getSelectedValue() != null){
				applyPointStyle(targetGeos);				
			}
		}
		else if (source == btnBold) {
			btnBold.toggle();
			applyFontStyle(targetGeos);			
		}
		else if (source == btnItalic) {
			btnItalic.toggle();
			applyFontStyle(targetGeos);			
		}
		else if (source == btnTextSize) {
			applyTextSize(targetGeos);			
		}
		else if (source == btnHideShowLabel) {
			btnHideShowLabel.toggle();
			
			applyHideShowLabel(targetGeos);	
			updateStyleBar();
		}
		else if (source == btnLabelStyle) {
			applyCaptionStyle(targetGeos);			
		}
		
		else if (source == btnTableTextJustify ) {
			applyTableTextFormat(targetGeos);			
		}
		else if (source == btnTableTextLinesH ) {
			btnTableTextLinesH.toggle();
			applyTableTextFormat(targetGeos);			
		}
		else if (source ==  btnTableTextLinesV) {
			btnTableTextLinesV.toggle();
			applyTableTextFormat(targetGeos);			
		}
		else if (source == btnTableTextBracket ) {
			applyTableTextFormat(targetGeos);			
		}
		
		else if (source == btnPenDelete) {
			
			//add code here to delete pen image
			
		}
		else if (source == btnPenEraser) {
			btnPenEraser.toggle();
			
			// add code here to toggle between pen and eraser mode;			
			
		}
	}



	//==============================================
	//           Apply Styles
	//==============================================

	private void applyLineStyle(ArrayList<GeoElement> geos) {
		int lineStyle = lineStyleArray[btnLineStyle.getSelectedIndex()];
		int lineSize = btnLineStyle.getSliderValue();

		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);
			if(geo.getLineType() != lineStyle || geo.getLineThickness() != lineSize){
				geo.setLineType(lineStyle);
				geo.setLineThickness(lineSize);
				geo.updateRepaint();
				needUndo = true;
			}
		}
	}

	private void applyPointStyle(ArrayList<GeoElement> geos) {
		int pointStyle = pointStyleArray[btnPointStyle.getSelectedIndex()];
		int pointSize = btnPointStyle.getSliderValue();
		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof PointProperties) {
				if(((PointProperties)geo).getPointSize()  != pointSize || (((PointProperties)geo).getPointStyle()  != pointStyle)){
					((PointProperties)geo).setPointSize(pointSize);
					((PointProperties)geo).setPointStyle(pointStyle);
					geo.updateRepaint();
					needUndo = true;
				}
			}
		}
	}


	private void applyColor(ArrayList<GeoElement> geos) {

		Color color = btnColor.getSelectedColor();
		float alpha = btnColor.getSliderValue() / 100.0f;

		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);
			// apply object color to all other geos except images or text
			if(!(geo.getGeoElementForPropertiesDialog() instanceof GeoImage || geo.getGeoElementForPropertiesDialog() instanceof GeoText))
				if((geo.getObjectColor() != color || geo.getAlphaValue() != alpha) ){
					geo.setObjColor(color);
					//if we change alpha for functions, hit won't work properly
					if(geo.isFillable())
						geo.setAlphaValue(alpha);
					geo.updateVisualStyle();
					needUndo = true;
			}
		}
	}


	private void applyBgColor(ArrayList<GeoElement> geos) {

		
		Color color = btnBgColor.getSelectedColor();
		float alpha = btnBgColor.getSliderValue() / 100.0f;

		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);

			// if text geo, then apply background color 
			if(geo instanceof TextProperties)
				if(geo.getBackgroundColor() != color || geo.getAlphaValue() != alpha ){
					geo.setBackgroundColor(color);
					// TODO apply background alpha 
					// --------
					geo.updateRepaint();
					needUndo = true;
				}
		}
	}



	private void applyTextColor(ArrayList<GeoElement> geos) {

		Color color = btnTextColor.getSelectedColor();
		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);
			if( ((GeoElement)geo.getGeoElementForPropertiesDialog()).isGeoText() && geo.getObjectColor() != color){
				geo.setObjColor(color);
				geo.updateRepaint();
				needUndo = true;
			}
		}
	}

	private void applyFontStyle(ArrayList<GeoElement> geos) {

		int fontStyle = 0;
		if (btnBold.isSelected()) fontStyle += 1;
		if (btnItalic.isSelected()) fontStyle += 2;
		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);
			if(geo instanceof TextProperties && ((TextProperties)geo).getFontStyle() != fontStyle){
				((TextProperties)geo).setFontStyle(fontStyle);
				geo.updateRepaint();
				needUndo = true;
			}
		}
	}

	

	private void applyTextSize(ArrayList<GeoElement> geos) {

		int fontSize = GeoText.getRelativeFontSize(btnTextSize.getSelectedIndex()); // transform indices to the range -4, .. , 4

		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);
			if(geo instanceof TextProperties && ((TextProperties)geo).getFontSize() != fontSize){
				((TextProperties)geo).setFontSize(fontSize);
				geo.updateRepaint();
				needUndo = true;
			}		
		}
	}
	
	
	private void applyHideShowLabel(ArrayList<GeoElement> geos) {
		boolean visible = btnHideShowLabel.isSelected();
		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);
			if(geo.isLabelVisible() != visible){
				geo.setLabelVisible(visible);
				geo.updateRepaint();
				needUndo = true;
			}
		}		
	}
	
	private void applyCaptionStyle(ArrayList<GeoElement> geos) {
		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);
			if ((mode == EuclidianConstants.MODE_MOVE && (geo.isLabelShowable() || geo.isGeoAngle() || (geo.isGeoNumeric() ? ((GeoNumeric)geo).isSliderFixed() : false)	)) ||
				(app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY && geo.isLabelShowable() && geo.isGeoPoint()) ||
				(app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_ALWAYS_ON && geo.isLabelShowable() || geo.isGeoAngle() || (geo.isGeoNumeric() ? ((GeoNumeric)geo).isSliderFixed() : false)) ||
				(app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC && geo.isLabelShowable() || geo.isGeoAngle()  || (geo.isGeoNumeric() ? ((GeoNumeric)geo).isSliderFixed() : false)) ) {
				if(btnLabelStyle.getSelectedIndex() == 0){
					if (mode == EuclidianConstants.MODE_MOVE || app.getLabelingStyle() != ConstructionDefaults.LABEL_VISIBLE_ALWAYS_ON) {
						geo.setLabelVisible(false);
					}
				}else{
					geo.setLabelVisible(true);
					geo.setLabelMode(btnLabelStyle.getSelectedIndex()-1);
				}
			}
			geo.updateRepaint();
			needUndo = true;
		}		
	}
	
	
	
	private void applyTableTextFormat(ArrayList<GeoElement> geos) {

		AlgoElement algo = null;
		GeoElement[] input;
		GeoElement geo;
		String arg = null;

		String[] justifyArray = { "l", "c", "r"};
		arg = justifyArray[btnTableTextJustify.getSelectedIndex()];
		if(this.btnTableTextLinesH.isSelected()) arg += "_";
		if(this.btnTableTextLinesV.isSelected()) arg += "|";
		if(btnTableTextBracket.getSelectedIndex() >0)
			arg += this.bracketArray2[btnTableTextBracket.getSelectedIndex()];
		ArrayList<GeoElement> newGeos = new ArrayList<GeoElement>();
		
		StringBuilder cmdText = new StringBuilder();

		for (int i = 0; i < geos.size(); i++) {

			// get the  TableText algo for this geo and its input
			geo = geos.get(i);
			algo = geo.getParentAlgorithm();
			input = algo.getInput();

			// create a new TableText cmd
			cmdText.setLength(0);
			cmdText.append("TableText[");
			cmdText.append(((GeoList) input[0]).getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, false));
			cmdText.append(",\"");
			cmdText.append(arg);
			cmdText.append("\"]");

			// use the new cmd to redefine the geo and save it to a list.
			// (the list is needed to reselect the geo)
			newGeos.add(redefineGeo(geo, cmdText.toString()));
		}

		// reset the selection 
		app.setSelectedGeos(newGeos);
	}

	
	public void applyVisualStyle(ArrayList<GeoElement> geos) {
		
		if(geos == null || geos.size() < 1) return;
		needUndo = false;
		
		if(btnColor.isVisible()) applyColor(geos);
		if(btnBgColor.isVisible()) applyBgColor(geos);
		if(btnLineStyle.isVisible()) applyLineStyle(geos);
		if(btnPointStyle.isVisible()) applyPointStyle(geos);
		if(btnBold.isVisible()) applyFontStyle(geos);
		if(btnItalic.isVisible()) applyFontStyle(geos);
		if(btnTextColor.isVisible()) applyTextColor(geos);
		if(btnTextSize.isVisible()) applyTextSize(geos);
		if(btnHideShowLabel.isVisible()) applyHideShowLabel(geos);
		
		if(needUndo){
			app.storeUndoInfo();
			needUndo = false;
		}
		
		
		// TODO update prop panel
		// see code in PropertiesDialog.applyDefaults
		//propPanel.updateSelection(selectionList.toArray());
		
	}

	
	
	
	
	public GeoElement redefineGeo(GeoElement geo, String cmdtext) {		
		GeoElement newGeo = null;
		
		if (cmdtext == null)
			return newGeo;
		
		try {
			newGeo = app.getKernel().getAlgebraProcessor().changeGeoElement(
					geo, cmdtext, true, true);
			app.doAfterRedefine(newGeo); 
			newGeo.updateRepaint();
			return newGeo;
			
		} catch (Exception e) {
			app.showError("ReplaceFailed");			
		} catch (MyError err) {
			app.showError(err);			
		} 
		return newGeo;
	}
	
	
	public class MyToggleButton extends JButton {
		public MyToggleButton(ImageIcon icon){
			super(icon);
			Dimension d = new Dimension(icon.getIconWidth(), iconHeight);
			setIcon(GeoGebraIcon.ensureIconSize(icon, d));
			this.setRolloverEnabled(true);
		}
		
		public void update(Object[] geos) {	 
		}
		public void toggle(){
			this.setSelected(!this.isSelected());
		}
		
	}

	/**
	 * Set labels with localized strings.
	 */
	public void setLabels(){

		initGUI();
		updateStyleBar();
		
		btnShowGrid.setToolTipText(app.getPlainTooltip("stylebar.Grid"));
		btnShowAxes.setToolTipText(app.getPlainTooltip("stylebar.Axes"));
		btnPointCapture.setToolTipText(app.getPlainTooltip("stylebar.Capture"));
		
		btnLabelStyle.setToolTipText(app.getPlainTooltip("stylebar.Label"));
		
		btnColor.setToolTipText(app.getPlainTooltip("stylebar.Color"));
		btnBgColor.setToolTipText(app.getPlainTooltip("stylebar.BgColor"));
		
		btnLineStyle.setToolTipText(app.getPlainTooltip("stylebar.LineStyle"));
		btnPointStyle.setToolTipText(app.getPlainTooltip("stylebar.PointStyle"));
		
		btnTextColor.setToolTipText(app.getPlainTooltip("stylebar.TextColor"));
		btnTextSize.setToolTipText(app.getPlainTooltip("stylebar.TextSize"));
		btnBold.setToolTipText(app.getPlainTooltip("stylebar.Bold")); 
		btnItalic.setToolTipText(app.getPlainTooltip("stylebar.Italic")); 
		btnTableTextJustify.setToolTipText(app.getPlainTooltip("stylebar.Align"));
		btnTableTextBracket.setToolTipText(app.getPlainTooltip("stylebar.Bracket"));
		btnTableTextLinesV.setToolTipText(app.getPlainTooltip("stylebar.HorizontalLine"));
		btnTableTextLinesH.setToolTipText(app.getPlainTooltip("stylebar.VerticalLine"));

		btnPen.setToolTipText(app.getPlainTooltip("stylebar.Pen"));
		btnPenEraser.setToolTipText(app.getPlainTooltip("stylebar.Eraser"));
		
		btnCopyVisualStyle.setToolTipText(app.getPlainTooltip("stylebar.CopyVisualStyle"));		
		
	}	

}
