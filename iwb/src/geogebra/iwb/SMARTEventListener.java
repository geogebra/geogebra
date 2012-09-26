package geogebra.iwb;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianPen;
import geogebra.common.io.DocHandler;
import geogebra.common.io.QDParser;
import geogebra.common.main.App;
import geogebra.euclidian.EuclidianControllerD;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.main.AppD;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.io.StringReader;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import com.smarttech.board.sbsdk.SBSDK;
import com.smarttech.board.sbsdk.SBSDKBase;
import com.smarttech.board.sbsdk.SBSDKBase.SBSDK_TOOL_TYPE;
import com.smarttech.board.sbsdk.SBSDKListener;
import com.smarttech.board.sbsdk.SBSDKListener.SBSDKContact;

/**
 * @author GeoGebra
 *
 */
public class SMARTEventListener implements SBSDKListener{


	public enum ToolType {
		NO_TOOL,
		PEN,
		ERASER
	}


	private SBSDK board;
	private AppD app;
	
	/** save selection for pen */
	private int penSelection;
	/** save selection for finger */
	private int fingerSelection;
	
	private boolean moveEvent, noToolEvent, resetPen;
	
	/** which tool was last active */
	private ToolType activeTool;

	public SMARTEventListener(AppD app, SBSDK board) {
		this.app = app;
		this.board = board;
		penSelection=-1;
		moveEvent=false;
	}
	
	
	private GPoint gestureStartLoc;
	public void onGestureDown(int iGestureId, int iType, int x, int y,
			float fRotation, float fScale) {
		//will be sent when the user starts a gesture (SBSDKBase.SBSDK_GESTURE_EVENT_FLAG.SB_GEF_SEND_ALL_THROUGH_SDK has to be set)
		print("onGestureDown","GestureID",iGestureId,"type",iType,"x",x,"y",y,"rotation",fRotation,"scale",fScale);
		if (iType==SBSDK_GESTURE_TYPE.SB_GT_PAN){
			app.getActiveEuclidianView().rememberOrigins();
			gestureStartLoc=new GPoint(x,y);
		}else if (iType==SBSDK_GESTURE_TYPE.SB_GT_SCALE){
			gestureStartLoc=new GPoint(x,y);
		}else if (iType==SBSDK_GESTURE_TYPE.SB_GT_RIGHT_CLICK){
			JPanel euclViewPanel=app.getActiveEuclidianView().getJPanel();
			MouseEvent me=new MouseEvent(euclViewPanel, MouseEvent.MOUSE_RELEASED, 0, 0, x, y, 1, false,MouseEvent.BUTTON3);
			dispatchEvent(me);
		}
	}



	public void onGestureMove(int iGestureId, int iType, int x, int y,
			float fRotation, float fScale) {
		//will be sent when the user moves (or holds) a gesture (SBSDKBase.SBSDK_GESTURE_EVENT_FLAG.SB_GEF_SEND_ALL_THROUGH_SDK has to be set)
		print("onGestureMove","GestureID",iGestureId,"type",iType,"x",x,"y",y,"rotation",fRotation,"scale",fScale);//,"gstrtX",gestureStartLoc.x,"gstrY",gestureStartLoc.y);
		if (iType==SBSDK_GESTURE_TYPE.SB_GT_PAN){
			app.getActiveEuclidianView().setCoordSystemFromMouseMove(x - gestureStartLoc.x,
						y - gestureStartLoc.y, EuclidianController.MOVE_VIEW);
		}else if (iType==SBSDK_GESTURE_TYPE.SB_GT_SCALE){
			EuclidianViewND v=app.getActiveEuclidianView();
			double factor=1.+fScale;
			v.setCoordSystem(x + (v.getXZero() - x) * factor, y + (v.getYZero() - y) * factor, v.getXscale()*factor,v.getYscale()*factor);
		}else if (iType==SBSDK_GESTURE_TYPE.SB_GT_RIGHT_CLICK){
			JPanel euclViewPanel=app.getActiveEuclidianView().getJPanel();
			MouseEvent me=new MouseEvent(euclViewPanel, MouseEvent.MOUSE_DRAGGED, 0, 0, x, y, 1, false,MouseEvent.BUTTON3);
			dispatchEvent(me);
		}
	}



	public void onGestureUp(int iGestureId, int iType, int x, int y,
			float fRotation, float fScale) {
		//will be sent when the user "ends" a gesture (SBSDKBase.SBSDK_GESTURE_EVENT_FLAG.SB_GEF_SEND_ALL_THROUGH_SDK has to be set)
		print("onGestureUp","GestureID",iGestureId,"type",iType,"x",x,"y",y,"rotation",fRotation,"scale",fScale);
		if (iType==SBSDK_GESTURE_TYPE.SB_GT_RIGHT_CLICK){
			JPanel euclViewPanel=app.getActiveEuclidianView().getJPanel();
			MouseEvent me=new MouseEvent(euclViewPanel, MouseEvent.MOUSE_RELEASED, 0, 0, x, y, 1, false,MouseEvent.BUTTON3);
			dispatchEvent(me);
			me=new MouseEvent(euclViewPanel, MouseEvent.MOUSE_CLICKED, 0, 0, x, y, 1, false,MouseEvent.BUTTON3);
			dispatchEvent(me);
		}
	}



	public void onMultiPointContact(SBSDKContact[] list) {
		//needs board.sendMultiPointContacts(true); not used because it interferes with gestures
		String[] s=new String[2*list.length];
		for (int i=0;i<list.length;i++){
			s[2*i]=list[i].getId()+"";
			s[2*i+1]=String.format(";state=%d;h=%f;w=%f;x=%f;y=%f,intsy=%f;vx=%f,vy=%f,pcaa=%f,pcah=%f,pcaw=%f,time=%d,wdw=%d",
					list[i].getState(),
					list[i].getHeight(),list[i].getWidth(),
					list[i].getX(),list[i].getY(),list[i].getIntensity(),
					list[i].getVelocityX(),list[i].getVelocityY(),
					list[i].getPCAAngle(),list[i].getPCAHeight(),list[i].getPCAWidth(),list[i].getTimeStamp(),
					list[i].getWindow());
		}
		print("onMultiPointContact",(Object[])s);
	}



	public void onBoardSettingsChange() {
		//some board settings changed
		print("onBoardSettingsChange");
	}

	public void onCircle(int iPointerID) {
		//never got this
		print("onCircle", "iPointerID", Integer.toString(iPointerID));
	}

	public void onClear(int iPointerID) {
		//never got this
		print("onClear", "iPointerID", Integer.toString(iPointerID));
	}

	public void onDViTAspectDelta(double arg0, double arg1, double arg2,
			double arg3, double arg4) {
		//raw data..., not called in current setting
		print("onDViTAspectDelta", "some args");
	}

	public void onDViTRawWidthHeight(int arg0, int arg1, int arg2) {
		//raw data..., not called in current setting
		print("onDViTRawWidthHeight", "some args");
	}

	public void onDViTTrackerData(int arg0, short arg1, short arg2, short arg3) {
		//raw data..., not called in current setting
		print("onDViTTrackerData", "some args");
	}

	public void onEraser(int iPointerID) {
		//sent when board.sendXMLToolChanges(false); or default, not used in current setting
		print("onEraser", "iPointerID", Integer.toString(iPointerID));
		app.setMode(EuclidianConstants.MODE_DELETE);
	}

	public void onLine(int iPointerID) {
		//never got this
		print("onLine", "iPointerID", Integer.toString(iPointerID));
	}

	public void onNext(int iPointerID) {
		//never got this
		print("onNext", "iPointerID", Integer.toString(iPointerID));
	}

	public void onNoTool(int iPointerID) {
		//sent when board.sendXMLToolChanges(false); or default, not used in current setting
		print("onNoTool", "iPointerID", Integer.toString(iPointerID));
		moveEvent=false;
		if (noToolEvent){ //two noToolEvents means that the pen was put back
			resetPen=true; //pen will be reseted on next onPen event
		}
		noToolEvent=true;
	}

	public void onPen(int iPointerID) {
		//sent when board.sendXMLToolChanges(false); or default, not used in current setting
		print("onPen", "iPointerID", Integer.toString(iPointerID), "color",
				board.getToolColor(iPointerID).toString(),"width",board.getToolWidth(iPointerID),"type",board.getToolType(iPointerID),
				"fillcolor",board.getToolFillColor(iPointerID));
		if (!moveEvent||resetPen){
			Color c = board.getToolColor(iPointerID);
			if (c.equals(Color.BLACK)) {
				penSelection=EuclidianConstants.MODE_PEN;
			}else if (c.equals(Color.RED)){
				penSelection=EuclidianConstants.MODE_FREEHAND_SHAPE;
			}
		}
		app.setMode(penSelection);
		noToolEvent=false;
	}

	public void onPrevious(int iPointerID) {
		//never got this
		print("onPrevious", "iPointerID", Integer.toString(iPointerID));
	}

	public void onPrint(int iPointerID) {
		print("onPrint", "iPointerID", Integer.toString(iPointerID));
	}

	public void onRectangle(int iPointerID) {
		print("onRectangle", "iPointerID", Integer.toString(iPointerID));
	}

	public void onXMLAnnot(String szXMLAnnot) {
		print("onXMLAnnot", "szXMLAnnot", szXMLAnnot);
	}

	public void onXMLToolChange(int iBoardNumber, String szXMLTool) {
		// sent when board.sendXMLToolChanges(true); and a tool is changed
		// (either taken from the board or just before use)
		// the XML-string is not documented but seems human readable
		// (see inner class ToolXMLHandler)
		print("onXMLTool", "iBoardNumber", Integer.toString(iBoardNumber),
				"szXMLTool", szXMLTool);
		QDParser parser=new QDParser();
		ToolXMLHandler d=new ToolXMLHandler();
		try {
			parser.parse(d, new StringReader("<root>"+szXMLTool+"</root>"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		App.debug(d.getToolEventType().toString());
		if (activeTool==ToolType.NO_TOOL){
			fingerSelection=app.getMode();
		}else if (activeTool==ToolType.PEN){
			penSelection=app.getMode();
		}
		switch (d.getToolEventType()){
		case TAKE_PEN:
			Color c = d.getColor();
			if (c.equals(Color.RED)){
				penSelection=EuclidianConstants.MODE_FREEHAND_SHAPE;
			}
			else{ //atm we want all colors but red for the pen.
//			else if (c.equals(Color.BLACK)) {
				penSelection=EuclidianConstants.MODE_PEN;
			}
			app.setMode(penSelection);
			EuclidianPen ep=app.getActiveEuclidianView().getEuclidianController().getPen();
			ep.setPenSize(2*d.getStrokeSize());
			ep.setPenColor(geogebra.common.factories.AwtFactory.prototype.newColor(c.getRGB()));
			activeTool=ToolType.PEN;
			break;
		case USE_PEN:
			app.setMode(penSelection);
			activeTool=ToolType.PEN;
			break;
		case USE_FINGER:
			app.setMode(fingerSelection);
			activeTool=ToolType.NO_TOOL;
			break;
		case PUT_AWAY_ALL:
			app.setMode(fingerSelection);
			activeTool=ToolType.NO_TOOL;
			break;
		case TAKE_ERASER:
		case USE_ERASER:
			activeTool=ToolType.ERASER;
			app.setMode(EuclidianConstants.MODE_DELETE);
			app.getActiveEuclidianView().getEuclidianController()
					.setDeleteToolSize(d.getStrokeSize()*2);
			break;
		}
	}

	private int lastMoveX=-1,lastMoveY=-1;
	public void onXYDown(int x, int y, int z, int iPointerID) {
		//touch with current tool
		print("onXYDown", "x", Integer.toString(x), "y", Integer.toString(y),
				"z", Integer.toString(z), "iPointerID",
				Integer.toString(iPointerID));
		lastMoveX=-1;
		lastMoveY=-1;
		JPanel euclViewPanel=app.getActiveEuclidianView().getJPanel();
		MouseEvent me=new MouseEvent(euclViewPanel, MouseEvent.MOUSE_PRESSED, 0, 0, x, y, 1, false,MouseEvent.BUTTON1);
		dispatchEvent(me);
	}

	public void onXYMove(int x, int y, int z, int iPointerID) {
		//move/hold with current tool
		print("onXYMove", "x", Integer.toString(x), "y", Integer.toString(y),
				"z", Integer.toString(z), "iPointerID",
				Integer.toString(iPointerID));
		moveEvent=true;
		if (x!=lastMoveX||y!=lastMoveY){
				lastMoveX=x;
				lastMoveY=y;
				JPanel euclViewPanel=app.getActiveEuclidianView().getJPanel();
				MouseEvent me=new MouseEvent(euclViewPanel, MouseEvent.MOUSE_DRAGGED, 0, 0, x, y, 1, false,MouseEvent.BUTTON1);
				dispatchEvent(me);
		}
	}
	
	public void onXYUp(int x, int y, int z, int iPointerID) {
		//ending touch with current tool
		print("onXYUp", "x", Integer.toString(x), "y", Integer.toString(y),
				"z", Integer.toString(z), "iPointerID",
				Integer.toString(iPointerID));
		JPanel euclViewPanel=app.getActiveEuclidianView().getJPanel();
		MouseEvent me=new MouseEvent(euclViewPanel, MouseEvent.MOUSE_RELEASED, 0, 0, x, y, 1, false,MouseEvent.BUTTON1);
		dispatchEvent(me);
		me=new MouseEvent(euclViewPanel, MouseEvent.MOUSE_CLICKED, 0, 0, x, y, 1, false,MouseEvent.BUTTON1);
		dispatchEvent(me);
	}

	public void onXYNonProjectedDown(int x, int y, int z, int iPointerID) {
		// board can supposedly be in NonProjected Mode, (don't now how to do this),
		// the coordinates will than be inside a 4000x6000 rectangle => better resolution.
		print("onXYNonProjectedDown", "x", Integer.toString(x), "y",
				Integer.toString(y), "z", Integer.toString(z), "iPointerID",
				Integer.toString(iPointerID));
	}

	public void onXYNonProjectedMove(int x, int y, int z, int iPointerID) {
		// board can supposedly be in NonProjected Mode, (don't now how to do this),
		// the coordinates will than be inside a 4000x6000 rectangle => better resolution.
		print("onXYNonProjectedMove", "x", Integer.toString(x), "y",
				Integer.toString(y), "z", Integer.toString(z), "iPointerID",
				Integer.toString(iPointerID));
	}

	public void onXYNonProjectedUp(int x, int y, int z, int iPointerID) {
		// board can supposedly be in NonProjected Mode, (don't now how to do this),
		// the coordinates will than be inside a 4000x6000 rectangle => better resolution.
		print("onXYNonProjectedUp", "x", Integer.toString(x), "y",
				Integer.toString(y), "z", Integer.toString(z), "iPointerID",
				Integer.toString(iPointerID));
	}


	public void print(String name, Object... args) {
		//easy debug output
		StringBuilder sb=new StringBuilder(Thread.currentThread().getId()+":");
		sb.append(name);
		sb.append('(');
		for (int i = 0; i < args.length; i++) {
			if (i > 0) {
				if ((i & 1) == 0) {
					sb.append(',');
				} else {
					sb.append('=');
				}
			}
			sb.append(args[i]);
		}
		sb.append(")");
		App.debug(sb);
	}
	
	private enum ToolEventType{ TAKE_PEN, TAKE_ERASER, USE_PEN,USE_FINGER,USE_ERASER, PUT_AWAY_ALL};
	
	/**
	 * dispatch this MouseEvent to System Queue
	 * @param me
	 */
	private void dispatchEvent(MouseEvent me){
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(me);
	}
	
	/**
	 * this DocHandler handles the XML String from onXMLToolChange
	 * @author GeoGebra
	 *
	 */
	private class ToolXMLHandler implements DocHandler{

		private boolean isPen;
		private boolean isEraser;
		private boolean touch;
		private Color color;
		private int strokeSize;
		
		public ToolXMLHandler() {
			
		}

		public Color getColor() {
			return color;
		}

		public int getStrokeSize() {
			return strokeSize;
		}


		public void startElement(String tag, LinkedHashMap<String, String> h)
				throws Exception {
			if (tag.equals("polyline")){
				isPen=true;
				String colorText=h.get("stroke");
				if (colorText!=null){
					try {
						color=new Color(Integer.parseInt(colorText.substring(1), 16));
					} catch (NumberFormatException e) {
						color=Color.black;
					}
				}
			}else if (tag.equals("no_tool")){
				isPen=false;
			}else if (tag.equals("tool_meta_data")){
				String sourceText=h.get("source");
				if (sourceText!=null){
					if (sourceText.equals("tool contact")){
						touch=true;
					}
				}
			}else if (tag.equals("eraser_tool")){
				isEraser=true;
			}else if (tag.equals("root")) {
				//do nothing
			}else{
				App.debug("Couldn't understand element <"+tag+">.");
			}
			String sizeText=h.get("stroke-width");
			if (sizeText!=null){
				try {
					strokeSize=Integer.parseInt(sizeText);
				} catch (NumberFormatException e) {
					strokeSize=3;
				}
			}
		}

		public void endElement(String tag) throws Exception {
			//
		}

		public void startDocument() throws Exception {
			isPen=false;
			touch=false;
			color=Color.black;
		}

		public void endDocument() throws Exception {
			//
		}

		public void text(String str) throws Exception {
			//
		}

		public int getConsStep() {
			return 0;
		}
		
		public ToolEventType getToolEventType(){
			ToolEventType ret;
			if (isPen){
				if (touch){
					ret=ToolEventType.USE_PEN;
				}else{
					ret=ToolEventType.TAKE_PEN;
				}
			}else if (isEraser){
				if (touch){
					ret=ToolEventType.USE_ERASER;
				}else{
					ret=ToolEventType.TAKE_ERASER;
				}
			}else{
				if (touch){
					ret=ToolEventType.USE_FINGER;
				}else{
					ret=ToolEventType.PUT_AWAY_ALL;
				}
			}
			return ret;
		}
		
	}

}
