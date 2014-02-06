package geogebra.geogebra3D.web;

import geogebra.geogebra3D.web.gui.app.GeoGebraAppFrame3D;
import geogebra.web.Web;

public class Web3D extends Web {
	
	
		@Override
		public void onModuleLoad() {
		    super.onModuleLoad();
			//Window.alert("I will be threeD :-)");
		}
		
		@Override
        protected void createGeoGebraAppFrame(){
			new GeoGebraAppFrame3D();
		}
}
