package geogebra.web.gui.images;

import geogebra.web.gui.util.HasSetIcon;
import geogebra.web.gui.util.SelectionTable;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class AppResourcesConverter {
	
	private static Canvas tmpCanvas = null;
	private static int waitingForConvert = 0;
	static ImageData [] converted = null;
	private static SelectionTable sT;
	
	private static Canvas getTmpCanvas() {
		if (tmpCanvas == null) {
			tmpCanvas = Canvas.createIfSupported();
		}
		return tmpCanvas;
	}

	public static void convertImageResourceToImageData(Object[] data,
            SelectionTable selectionTable) {
		waitingForConvert = data.length;
		converted = new ImageData[waitingForConvert];
		sT = selectionTable;
		for (int i = 0; i < data.length; i++) {
		   convertToImageData(data[i],i);
		}
    }

	private static void convertToImageData(Object object, final int index) {
	   ImageResource is = (ImageResource) object;
	   final Image i = new Image(is.getSafeUri());
	   i.addLoadHandler(new LoadHandler() {
		
		public void onLoad(LoadEvent event) {
			Context2d c = getTmpCanvas().getContext2d();
			int w = i.getWidth();
			int h = i.getHeight();
			getTmpCanvas().setCoordinateSpaceWidth(w);
			getTmpCanvas().setCoordinateSpaceHeight(h);
			c.clearRect(0, 0, w, h);
			c.drawImage(ImageElement.as(i.getElement()), 0, 0);
			//for some reason the size is 0 in dev mode sometimes (win8 + IE10)
			if(w * h>0){
				converted[index] = c.getImageData(0, 0, w, h);
				
				waitingForConvert--;
				checkIfCanCallCallback();
			}else{
				System.out.println(i.getUrl());
			
			}
		}
	   });
	   i.setVisible(false);
	   RootPanel.get().add(i);
    }

	private static void checkIfCanCallCallback() {
		if (waitingForConvert == 0) {
			sT.populateModelCallback(converted);
		}
	}

	public static void setIcon(final ImageResource ir, final HasSetIcon button) {
	    Canvas c = getTmpCanvas(ir.getWidth(),ir.getHeight());
	    final Context2d ctx = c.getContext2d();
	    final Image img = new Image(ir.getSafeUri());
	    img.addLoadHandler(new LoadHandler() {
			
			public void onLoad(LoadEvent event) {
				ctx.drawImage(ImageElement.as(img.getElement()), 0, 0, ir.getWidth(), ir.getHeight());
				button.setIcon(ctx.getImageData(0, 0, ir.getWidth(), ir.getHeight()));
			}
		});
	   img.setVisible(false);
	   RootPanel.get().add(img);
    }

	private static Canvas getTmpCanvas(int width, int height) {
		if (tmpCanvas == null) {
		    	tmpCanvas = Canvas.createIfSupported();
		}
		
		Context2d ctx = tmpCanvas.getContext2d();
	    tmpCanvas.setCoordinateSpaceWidth(width);
	    tmpCanvas.setCoordinateSpaceHeight(height);
	    ctx.setTransform(1, 0, 0, 1, 0, 0);
	    
	    ctx.clearRect(0, 0, tmpCanvas.getCoordinateSpaceWidth(), tmpCanvas.getCoordinateSpaceHeight());
		
	    return tmpCanvas;
	    
    }

}
