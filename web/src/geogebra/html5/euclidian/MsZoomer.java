package geogebra.html5.euclidian;

import geogebra.common.euclidian.EuclidianController;

import com.google.gwt.user.client.Element;

public class MsZoomer {
	
	private final EuclidianController tc;
	public MsZoomer(EuclidianController tc){
		this.tc = tc;
	}
	
	public void pointersUp(){
		this.tc.setExternalHandling(false);
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
	
	public native void attachTo(Element element) /*-{
	$wnd.first = {id:-1};
	$wnd.second = {id:-1};
	
	
	element.addEventListener("MSPointerMove",function(e) {
		if($wnd.first.id >=0 && $wnd.second.id>=0){
			if($wnd.second.id === e.pointerId){    	
				$wnd.second.x = e.x;	
				$wnd.second.y = e.y;
				this.@geogebra.html5.euclidian.MsZoomer::twoPointersMove(DDDD)($wnd.first.x,$wnd.first.y,
			$wnd.second.x,$wnd.second.y);
			}else{
				$wnd.first.x = e.x;	
				$wnd.first.y = e.y;
			}
			
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
			this.@geogebra.html5.euclidian.MsZoomer::twoPointersDown(DDDD)($wnd.first.x,$wnd.first.y,
			$wnd.second.x,$wnd.second.y);
		}

	});
	
	element.addEventListener("MSPointerUp",function(e) {
		if($wnd.first.id == e.pointerId){
			$wnd.first.id = -1;
		}else{
			$wnd.second.id = -1;
		}
		zoomer.@geogebra.html5.euclidian.MsZoomer::pointersUp()();
	});
}-*/;

}
