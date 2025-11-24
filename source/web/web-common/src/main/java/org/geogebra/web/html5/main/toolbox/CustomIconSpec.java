package org.geogebra.web.html5.main.toolbox;

import static org.geogebra.common.awt.GColor.parseHexColor;

import org.geogebra.common.awt.MyImage;
import org.geogebra.web.awt.MyImageW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.gwtproject.dom.client.Element;

import elemental2.dom.HTMLImageElement;
import jsinterop.base.Js;

public class CustomIconSpec implements IconSpec {
    private String url;
    MyImageW image;

    public CustomIconSpec(MyImage image) {
        this.image = (MyImageW) image;
    }

    public CustomIconSpec(String url) {
        this.url = url;
    }

    @Override
    public Element toElement() {
        HTMLImageElement img = Dom.createImage();
        img.src = url;
        image = new MyImageW(img, true);
        return Js.uncheckedCast(image.getImage());
    }

    @Override
    public IconSpec withFill(String color) {
        return new CustomIconSpec(image.tintedSVG(parseHexColor(color), () -> {}));
    }
}
