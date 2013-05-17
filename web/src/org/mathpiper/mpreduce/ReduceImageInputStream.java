package org.mathpiper.mpreduce;

import geogebra.html5.css.GuiResources;

import java.io.IOException;

import org.mathpiper.mpreduce.base64.Base64;
import org.mathpiper.mpreduce.io.streams.InputStream;

import com.google.gwt.core.client.JsArrayString;

public class ReduceImageInputStream extends InputStream {

    private int stringSelector = 0;

    private int byteIndex = -1;

    private byte[] bytes = null;

    private boolean emptyFlag = false;

    public ReduceImageInputStream()
    {
        bytes = Base64.decode(reduceImage[stringSelector]);
    }

    public int read() throws IOException {

        if(emptyFlag == true)
        {
            return -1;
        }

        byteIndex++;

        if(byteIndex == bytes.length)
        {
            if(stringSelector != reduceImage.length - 2)
            {
                byteIndex = 0;

                stringSelector++;

                bytes = Base64.decodeFast(reduceImage[stringSelector]);
            }
            else
            {
                emptyFlag = true;

                return -1;
            }
        }//end if.

        int character = 0;

        character = bytes[byteIndex] & 0xff;

        return character;

    }//end read.

    public void close()
    {
        reduceImage = null;
    }


    public String[] reduceImage = splitInto2280Character(GuiResources.INSTANCE.getDefaultImgBase64Web().getText());

    private native JsArrayString nativeSplitIntoGivenLenghtCharacter(String imgText) /*-{
		return imgText.match(/.{1,2280}/g);;
	}-*/;

	private String[] splitInto2280Character(String imgText) {
	    JsArrayString nsp  = nativeSplitIntoGivenLenghtCharacter(imgText);
	    String[] ret = new String[nsp.length()];
	    for (int i = 0; i < nsp.length(); i++) {
	    	ret[i] = nsp.get(i);
	    }
	    return ret;
    }

}