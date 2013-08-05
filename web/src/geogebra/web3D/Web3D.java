package geogebra.web3D;


import geogebra.web.Web;
import geogebra.web3D.euclidian3D.opengl.Test;



/**
 * @author apa
 *
 */
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Web3D extends Web {
	
	
	

	
    @Override
    public void onModuleLoad() {
		//super.onModuleLoad();
    	
		new Test();
	}


}
