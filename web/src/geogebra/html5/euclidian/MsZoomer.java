package geogebra.html5.euclidian;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.main.App;
import geogebra.html5.event.HasOffsets;
import geogebra.html5.event.PointerEvent;
import geogebra.web.euclidian.EuclidianControllerW;
import geogebra.web.euclidian.event.ZeroOffset;

import com.google.gwt.user.client.Element;

public class MsZoomer {
	
	private final EuclidianController tc;
	private HasOffsets off;
	
	private class MsOffset extends ZeroOffset{

		private EuclidianControllerW ec;

		public MsOffset(EuclidianControllerW ec){
			this.ec = ec;
		}

		@Override
        public int mouseEventX(int clientX) {
			EnvironmentStyleW style = ec.getEnvironmentStyle();
			App.debug(style+"");
	        return Math.round(clientX - (1/style.getWidthScale()-1)*style.getxOffset());
        }

		@Override
        public int mouseEventY(int clientY) {
			EnvironmentStyleW style = ec.getEnvironmentStyle();
			return Math.round(clientY - (1/style.getHeightScale()-1)*style.getyOffset());
        }

		@Override
        public int touchEventX(int clientX) {
	        return mouseEventX(clientX);
        }

		@Override
        public int touchEventY(int clientY) {
			return mouseEventY(clientY);
        }

		@Override
        public int getEvID() {
	        return ec.getViewID();
        }
		
	}
	
	public MsZoomer(EuclidianController tc){
		this.tc = tc;
		//this.off = (HasOffsets)this.tc;
		this.off = new MsOffset((EuclidianControllerW) tc);
	}
	
	public void pointersUp(){
		this.tc.setExternalHandling(false);
	}
	
	public void pointerDown(double x, double y){
		((EuclidianControllerW) this.tc).onMouseDown(new PointerEvent(x,y,PointerEventType.TOUCH,off));
	}
	
	public void pointerMove(double x, double y){
		((EuclidianControllerW) this.tc).wrapMouseMoveOrDrag(new PointerEvent(x,y,PointerEventType.TOUCH,off));
	}
	
	public void pointerUp(double x, double y){
		((EuclidianControllerW) this.tc).onMouseUp(new PointerEvent(x,y,PointerEventType.TOUCH,off));
	}

	public void twoPointersDown(double x1,double y1, double x2, double y2){
		this.tc.setExternalHandling(true);
		this.tc.twoTouchStart(x1,y1,x2,y2);
	}

	public void twoPointersMove(double x1,double y1, double x2, double y2){
		this.tc.twoTouchMove(x1,y1,x2,y2);
	}
	
	public native void reset()/*-{
		$wnd.first = {id:-1};
		$wnd.second = {id:-1};
	}-*/;
	
	public static native void attachTo(Element element, MsZoomer zoomer) /*-{
	$wnd.first = {id:-1};
	$wnd.second = {id:-1};
	
	
	element.addEventListener("MSPointerMove",function(e) {
		if($wnd.first.id >=0 && $wnd.second.id>=0){
			if($wnd.second.id === e.pointerId){    	
				$wnd.second.x = e.x;	
				$wnd.second.y = e.y;
				zoomer.@geogebra.html5.euclidian.MsZoomer::twoPointersMove(DDDD)($wnd.first.x,$wnd.first.y,
			$wnd.second.x,$wnd.second.y);
			}else{
				$wnd.first.x = e.x;	
				$wnd.first.y = e.y;
			}
			
		}else{
			zoomer.@geogebra.html5.euclidian.MsZoomer::pointerMove(DD)(e.x,e.y);
		}
	});
	
	element.addEventListener("MSPointerDown",function(e) {
		if($wnd.first.id >=0 && $wnd.second.id>=0){
			return;
		}
		if($wnd.first.id >= 0){
			$wnd.second.id = e.pointerId;
			$wnd.second.x = e.x;	
			$wnd.second.y = e.y;
		}else{
			$wnd.first.id = e.pointerId;
			$wnd.first.x = e.x;	
			$wnd.first.y = e.y;
		}
		if($wnd.first.id >=0 && $wnd.second.id>=0){
			zoomer.@geogebra.html5.euclidian.MsZoomer::twoPointersDown(DDDD)($wnd.first.x,$wnd.first.y,
			$wnd.second.x,$wnd.second.y);
		}else{
			zoomer.@geogebra.html5.euclidian.MsZoomer::pointerDown(DD)(e.x,e.y);
		}

	});
	
	element.addEventListener("MSPointerUp",function(e) {
		if($wnd.first.id == e.pointerId){
			$wnd.first.id = -1;
		}else{
			$wnd.second.id = -1;
		}
		zoomer.@geogebra.html5.euclidian.MsZoomer::pointersUp()();
		if($wnd.first.id <0 && $wnd.second.id<0){
			zoomer.@geogebra.html5.euclidian.MsZoomer::pointerUp(DD)(e.x,e.y);
		}
	});
}-*/;

}
