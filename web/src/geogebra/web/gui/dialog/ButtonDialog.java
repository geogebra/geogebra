package geogebra.web.gui.dialog;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.web.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.main.AppW;

import java.util.Iterator;
import java.util.TreeSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ButtonDialog extends PopupPanel implements ClickHandler{

	private AutoCompleteTextFieldW tfCaption; /* tfScript, tfScript2 */; 
	private HorizontalPanel btPanel;
//	//private DefaultListModel listModel;
//	private DefaultComboBoxModel comboModel;
//	
	private GeoElement linkedGeo = null;
	private boolean textField = false;
	
//	private Point location;
	private Button btApply, btCancel;
//	private JRadioButton rbNumber, rbAngle;
//	private InputPanel tfLabel;
	private VerticalPanel optionPane;
//	
	private AppW app;
	
//	private GeoElement geoResult = null;
	private GeoButton button = null;
	
//	InputPanel inputPanel, inputPanel2;
	private int x,y;
	
	public ButtonDialog(AppW app, int x, int y, boolean textfield) {
		super(false,true);
		
		this.app = app;		
		this.textField = textfield;
		this.x=x;
		this.y=y;
		
		createGUI();	
		center();
//		setLocationRelativeTo(app.getMainComponent());	
	}

	private void createGUI() {
		//this.setTitle(textField ? app.getPlain("TextField") : app.getPlain("Button") );
		
		// create caption panel
		Label captionLabel = new Label(app.getMenu("Button.Caption")+":");
		String initString = button == null ? "" : button.getCaption(StringTemplate.defaultTemplate);
		InputPanelW ip = new InputPanelW(initString, app, 1, 25, true);				
		tfCaption = ip.getTextComponent();
		if (tfCaption instanceof AutoCompleteTextFieldW) {
			AutoCompleteTextFieldW atf = (AutoCompleteTextFieldW) tfCaption;
			atf.setAutoComplete(false);
		}
		
		//captionLabel.setLabelFor(tfCaption);
		HorizontalPanel captionPanel = new HorizontalPanel();
		captionPanel.add(captionLabel);
		captionPanel.add(ip);
		captionPanel.addStyleName("captionPanel");
		captionLabel.getElement().getParentElement().addClassName("tdForCaptionLabel");
		captionLabel.getElement().getParentElement().setAttribute("style","vertical-align: middle");
				
		// combo box to link GeoElement to TextField
//		comboModel = new DefaultComboBoxModel();
		TreeSet<GeoElement> sortedSet = app.getKernel().getConstruction().
									getGeoSetNameDescriptionOrder();			
		
//		final JComboBox cbAdd = new JComboBox(comboModel);
		final ListBox cbAdd = new ListBox();
		
		
		if (textField) {
			// lists for combo boxes to select input and output objects
			// fill combobox models
			Iterator<GeoElement> it = sortedSet.iterator();
//			comboModel.addElement(null);
//			FontMetrics fm = getFontMetrics(getFont());
//			int width = (int)cbAdd.getPreferredSize().getWidth();
			while (it.hasNext()) {
				GeoElement geo = it.next();				
				if (!geo.isGeoImage() && !(geo.isGeoButton()) && !(geo.isGeoBoolean())) {
//					comboModel.addElement(geo);
					String str = geo.toString(StringTemplate.defaultTemplate);
					cbAdd.addItem(str);
//					if (width < fm.stringWidth(str))
//						width = fm.stringWidth(str);
				}
			}	
//			
//			// make sure it's not too wide (eg long GeoList)
//			Dimension size = new Dimension(Math.min(Application.getScreenSize().width/2, width), cbAdd.getPreferredSize().height);
//			cbAdd.setMaximumSize(size);
//			cbAdd.setPreferredSize(size);
//
//
//			
			if (cbAdd.getItemCount() > 1) {
				cbAdd.addClickHandler(new ClickHandler(){

					public void onClick(ClickEvent event) {
						GeoElement geo = getGeo(cbAdd.getItemText(cbAdd.getSelectedIndex()));
						if (geo==null) return;
						linkedGeo = geo;
                    }
					
					public GeoElement getGeo(String text) {
						TreeSet<GeoElement> sortedSet = app.getKernel()
						        .getConstruction()
						        .getGeoSetNameDescriptionOrder();
						Iterator<GeoElement> it = sortedSet.iterator();
						while (it.hasNext()) {
							GeoElement geo = it.next();
							if (text.equals(geo
							        .toString(StringTemplate.defaultTemplate)))
								return geo;

						}
						return null;
					}
					
				});
			}
		}
//
//		
//		// create script panel
//		JLabel scriptLabel = new JLabel(app.getPlain("Script")+":");
//		initString = (button == null) ? "" : button.getClickScript();
//		InputPanel ip2 = new InputPanel(initString, app, 10, 40, false);
//		Dimension dim = ((GeoGebraEditorPane) ip2.getTextComponent())
//				.getPreferredSizeFromRowColumn(10, 40);
//		ip2.setPreferredSize(dim);
//		
//		ip2.setShowLineNumbering(true);
//		tfScript = ip2.getTextComponent();
//		// add a small margin
//		tfScript.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
//		
//		if (tfScript instanceof AutoCompleteTextField) {
//			AutoCompleteTextField atf = (AutoCompleteTextField) tfScript;
//			atf.setAutoComplete(false);
//		}
//		
//		scriptLabel.setLabelFor(tfScript);
//		JPanel scriptPanel = new JPanel(new BorderLayout(5,5));
//		scriptPanel.add(scriptLabel, BorderLayout.NORTH);
//		scriptPanel.add(ip2,BorderLayout.CENTER);
//		scriptPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//		
		HorizontalPanel linkedPanel = new HorizontalPanel();
		Label linkedLabel = new Label(app.getPlain("LinkedObject")+":");
		linkedPanel.add(linkedLabel);
		linkedPanel.add(cbAdd);
		
		// buttons
		btApply = new Button(app.getPlain("Apply"));
		btApply.getElement().setAttribute("action","Apply");
		btApply.addClickHandler(this);
		btCancel = new Button(app.getPlain("Cancel"));
		btCancel.getElement().setAttribute("action","Cancel");
		btCancel.addClickHandler(this);
		btPanel = new HorizontalPanel();
		btPanel.add(btApply);
		btPanel.add(btCancel);
		btPanel.addStyleName("buttonPanel");
			
		//Create the JOptionPane.
		optionPane = new VerticalPanel();
		
		// create object list
		optionPane.add(captionPanel);
		if (textField)
			optionPane.add(linkedPanel);	
//		else
//			optionPane.add(scriptPanel, BorderLayout.CENTER);	
		optionPane.add(btPanel);	
//		optionPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
//
		//Make this dialog display it.	
		setWidget(optionPane);
		//this.addStyleName("ButtonDialog");
		this.getElement().getElementsByTagName("table").getItem(0).setAttribute("cellpadding", "5px");
    }

	public void onClick(ClickEvent event) {
//	    AbstractApplication.debug(((Widget) event.getSource()).getElement().getAttribute("action"));
	
		Object source = event.getSource();				
		if (source == btApply) {	
			Construction cons = app.getKernel().getConstruction();
			//button = textField ? app.getKernel().textfield(null, linkedGeo) : new GeoButton(cons);
			button = app.getKernel().getAlgoDispatcher().textfield(null, linkedGeo); //temp - instead of previous row
			button.setEuclidianVisible(true);
			button.setAbsoluteScreenLoc(x, y);
//
//			
//			button.setLabel(null);	
//			button.setClickScript(tfScript.getText(), true);
//			
			// set caption text
			String strCaption = tfCaption.getText().trim();
			if (strCaption.length() > 0) {
				button.setCaption(strCaption);			
			}
			
			button.setEuclidianVisible(true);
//			button.setLabelVisible(true);
			button.updateRepaint();
//
//
//			geoResult = button;		
			hide();
			app.getActiveEuclidianView().requestFocusInWindow();
//			
			app.storeUndoInfo();
		} 
		else if (source == btCancel) {
//			geoResult = null;
			hide();
			app.getActiveEuclidianView().requestFocusInWindow();
		} 
		
		
    }

}
