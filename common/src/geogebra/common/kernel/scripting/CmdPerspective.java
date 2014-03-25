package geogebra.common.kernel.scripting;

import geogebra.common.factories.AwtFactory;
import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.io.layout.DockSplitPaneData;
import geogebra.common.io.layout.Perspective;
import geogebra.common.javax.swing.GSplitPane;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CmdPerspective extends CmdScripting {
	static Map<String, DockPanelData> viewCodes = new HashMap<String,DockPanelData>();
	static{
		viewCodes.put("G", new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1", 500));
		viewCodes.put("A", new DockPanelData(App.VIEW_ALGEBRA, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 250, 400), "3,3", 200));
		viewCodes.put("S", new DockPanelData(App.VIEW_SPREADSHEET, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "3", 300));
		viewCodes.put("C", new DockPanelData(App.VIEW_CAS, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "3,1", 300));
		viewCodes.put("P", new DockPanelData(App.VIEW_PROPERTIES, null, false, true, true, AwtFactory.prototype.newRectangle(100, 100, 700, 550), "1,1", 400));
	}
	public CmdPerspective(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void perform(Command c) {
		GeoElement[] args = resArgs(c);
		if(args.length != 1){
			throw this.argNumErr(app, c.getName(),args.length);
		}
		if(args[0] instanceof GeoText){
			String defToolbar = ToolBar.getAllToolsNoMacros(true, app.isHTML5Applet());
			
			String code  = ((GeoText)args[0]).getTextString();
			int views = 0;
			ArrayList<DockPanelData> panelList = new ArrayList<DockPanelData>();
			String[] paths = new String[]{"3","1,3","1,1"};
			for(int i=0;i<code.length();i++){
				if(viewCodes.get(code.charAt(i)+"")!=null){
					viewCodes.get(code.charAt(i)+"").setVisible(true);
					viewCodes.get(code.charAt(i)+"").setLocation(paths[i]);
					panelList.add(viewCodes.get(code.charAt(i)+""));
					views++;
				}
			}
			DockSplitPaneData[] spData = new DockSplitPaneData[views];
			for(int i=0;i<views;i++){
				spData[i] = new DockSplitPaneData("1,1,1,1,1,1,1".substring(0,2*i),0.3,GSplitPane.HORIZONTAL_SPLIT);
			}
			
			DockPanelData[] dpData = new DockPanelData[panelList.size()];
			panelList.toArray(dpData);
		
			
			Perspective ps =  new Perspective("TableAndGraphics", spData, dpData, defToolbar, true, false, true, true, true, false);
			app.getGuiManager().getLayout().applyPerspective(ps);
			return;
		}
		throw this.argErr(app, c.getName(),args[0]);
		
		
		
	}

}
