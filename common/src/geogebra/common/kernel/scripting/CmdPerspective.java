package geogebra.common.kernel.scripting;

import geogebra.common.factories.AwtFactory;
import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.io.layout.DockSplitPaneData;
import geogebra.common.io.layout.Perspective;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Variable;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.parser.ParseException;
import geogebra.common.main.App;
import geogebra.common.plugin.Operation;
import geogebra.common.util.debug.Log;

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
		viewCodes.put("P", new DockPanelData(App.VIEW_PROPERTIES, null, false, false, true, AwtFactory.prototype.newRectangle(100, 100, 700, 550), "1,1", 400));
		
		viewCodes.put("L", new DockPanelData(App.VIEW_CONSTRUCTION_PROTOCOL, null, false, false, true, AwtFactory.prototype.newRectangle(100, 100, 700, 550), "1,1", 400));
		viewCodes.put("D", new DockPanelData(App.VIEW_EUCLIDIAN2, null, false, false, true, AwtFactory.prototype.newRectangle(100, 100, 700, 550), "1,1", 400));
		viewCodes.put("T", new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false, false, true, AwtFactory.prototype.newRectangle(100, 100, 700, 550), "1,1", 400));
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
			String longCode = "";
			for(int i = 0;i<code.length();i++){
				longCode += code.charAt(i)+" ";
			}
			ExpressionValue expr;
			try {
				expr = kernelA.getParser().parseGeoGebraExpression(longCode).wrap();
			} catch (ParseException e) {				
				e.printStackTrace();
				return;
			}
			ArrayList<DockPanelData> panelList = new ArrayList<DockPanelData>();
			ArrayList<DockSplitPaneData> splitList = new ArrayList<DockSplitPaneData>();
			buildPerspective(expr.unwrap(),"","",panelList,splitList,1,0.8);
			
			DockSplitPaneData[] spData = new DockSplitPaneData[splitList.size()];
			splitList.toArray(spData);
			
			
			DockPanelData[] dpData = new DockPanelData[panelList.size()];
			panelList.toArray(dpData);
		
			
			Perspective ps =  new Perspective("Custom", spData, dpData, defToolbar, true, false, true, true, true, false);
			try{
			app.getGuiManager().getLayout().applyPerspective(ps);
			}catch(Exception e){
				e.printStackTrace();
			}
			return;
		}
		throw this.argErr(app, c.getName(),args[0]);
		
		
		
	}

	private void buildPerspective(ExpressionValue expr, String panelPath,
			String splitPath,
			ArrayList<DockPanelData> panelList,
			ArrayList<DockSplitPaneData> splitList, double totalWidth, double totalHeight) {
		
		if(expr instanceof Variable){
			String code = ((Variable)expr).getName(StringTemplate.defaultTemplate);
			if(viewCodes.get(code)!=null){
				viewCodes.get(code).setVisible(true);
				viewCodes.get(code).setLocation(panelPath.length()>0?panelPath.substring(1):"");
				panelList.add(viewCodes.get(code));
			}
		}
		else
		if(expr instanceof ExpressionNode){
			Log.debug(expr);
			App.debug("HEIGHT"+totalHeight);
			ExpressionNode en = (ExpressionNode)expr;
			boolean horizontal = ((ExpressionNode) expr).getOperation() == Operation.MULTIPLY;

			double ratio = size(en.getLeft(),horizontal)/size(en,horizontal);
			App.debug("RATIO"+ratio);
			double height1 = totalHeight;
			double width1 = totalWidth;
			double height2 = totalHeight;
			double width2 = totalWidth;
			if(horizontal){
				width1 = width1 * ratio;
				width2 = width2 * (1-ratio);
			}else{
				height1 = height1 * ratio;
				height2 = height2 * (1-ratio);
			}
			App.debug("HEIGHT1"+height1);

			splitList.add(new DockSplitPaneData(splitPath.length()>0?splitPath.substring(1):"", horizontal ? width1 : height1, horizontal ? 1 : 0));
			this.buildPerspective(en.getRight().unwrap(), panelPath+(horizontal ? ",1" : ",2"), splitPath+",1", panelList, splitList, width2, height2);
			this.buildPerspective(en.getLeft().unwrap(), panelPath+(horizontal ? ",3" : ",0"), splitPath+",0", panelList, splitList, width1, height1);
		}else{
			App.error("Wrong type"+expr.getClass().getName());
		}
	}

	private double size(ExpressionValue expr, boolean horizontal) {
		if(expr instanceof Variable && horizontal){
			String name = ((Variable)expr).getName(StringTemplate.defaultTemplate);
			if("A".equals(name) || "C".equals(name)){
				return 0.5;
			}
		}
		if(!(expr instanceof ExpressionNode)){
			return 1;
		}
		ExpressionNode en = (ExpressionNode)expr;
		if(en.getOperation()!=Operation.NO_OPERATION && en.getRight()!=null){
			return size(en.getLeft(), horizontal)+size(en.getRight(), horizontal);
		}
		return size(en.getLeft(),horizontal);
		
	}

}
