package geogebra.geogebra3D.web.euclidian3DnoWebGL;

import geogebra.common.awt.GBufferedImage;
import geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.geogebra3D.web.euclidian3D.EuclidianView3DW;
import geogebra.html5.gawt.BufferedImage;

import java.util.HashMap;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;

/**
 * (dummy) 3D view for browsers that don't support webGL
 * @author mathieu
 *
 */
public class EuclidianView3DWnoWebGL extends EuclidianView3DW {

	private GBufferedImage thumb;
	/**
	 * constructor
	 * @param ec controller
	 * @param settings settings
	 */
	public EuclidianView3DWnoWebGL(EuclidianController3D ec,
            EuclidianSettings settings) {
	    super(ec, settings);
    }
	
	
	@Override
    protected Renderer createRenderer() {
	    return new RendererWnoWebGL(this);
    }
	
	@Override
    public void repaintView(){
		// repaint will be done only when resized
	}

	
	@Override
    public void repaint(){
		
		if (thumb != null){
			this.g2p.drawImage(thumb, 0, 0);
		}
		this.g2p.drawString(getApplication().getPlain("NoWebGL"), 10, 10);
	}
	
	@Override
    public void setCurrentFile(HashMap<String, String> file) {
		if(file!= null && file.get("geogebra_thumbnail.png") != null){
			ImageElement img = Document.get().createImageElement();
			img.setSrc(file.get("geogebra_thumbnail.png"));
			thumb = new BufferedImage(img);
		}
		
		repaint();
    }

}
