/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geogebraVoiceCommand;

import org.geogebra.desktop.plugin.GgbAPID;

import geogebraVoiceCommand.modifierstringcommand.object.Point;
import geogebraVoiceCommand.modifierstringcommand.object.Segment;
import geogebraVoiceCommand.modifierstringcommand.object.Triangle;

/**
 *
 * @author Darkyver
 */
public class FieldViewer {
    public static int xmin,xmax,ymin,ymax;

    
    final public static int DELTA_FIELD = 1;
    
    final static int X_MIN_DEFAULT = -20;
    final static int X_MAX_DEFAULT = 20;
    final static int Y_MIN_DEFAULT = -10;
    final static int Y_MAX_DEFAULT = 10;

    private GgbAPID geoGebraAPI;

    public FieldViewer(GgbAPID geoGebraAPI){
        this.geoGebraAPI = geoGebraAPI;
        xmin = X_MIN_DEFAULT;
        xmax = X_MAX_DEFAULT;
        ymin = Y_MIN_DEFAULT;
        ymax = Y_MAX_DEFAULT;
        refresh();
    }

    public FieldViewer(int xmin, int xmax, int ymin, int ymax) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
        refresh();
    }
    public void setDefault(){
        xmin = X_MIN_DEFAULT;
        xmax = X_MAX_DEFAULT;
        ymin = Y_MIN_DEFAULT;
        ymax = Y_MAX_DEFAULT;
        geoGebraAPI.setCoordSystem(xmin, xmax, ymin, ymax);
    }
    public void refresh(){
        geoGebraAPI.setCoordSystem(xmin, xmax, ymin, ymax);
    }
    

    public void refresh(Point p){
        xmin = (int) Math.floor(xmin>(p.getX()-2)?p.getX()-DELTA_FIELD:xmin);
        xmax = (int) Math.ceil(xmax<(p.getX()+2)?p.getX()+DELTA_FIELD:xmax);
        ymin = (int) Math.floor(ymin>(p.getY()-2)?p.getY()-DELTA_FIELD:ymin);
        ymax = (int) Math.ceil(ymax<(p.getY()+2)?p.getY()+DELTA_FIELD:ymax);
        refresh();
    }
    
    public void setView(Point leftBottom, Point rightTop){
        xmin = (int) leftBottom.getX();
        xmax = (int) rightTop.getX();
        ymin = (int) leftBottom.getY();
        ymax = (int) rightTop.getY();
        refresh();
    }
    public void zoomOut(int value){
        xmin-=value;
        xmax+=value;
        ymin-=value/2;
        ymax+=value/2;
    }
    public void zoomIn(int value){
        xmin+=value;
        xmax-=value;
        ymin+=value/2;
        ymax-=value/2;
    }
    public void refresh(Segment seg){
        refresh(seg.getA());
        refresh(seg.getB());
    }
    
    public void refresh(Triangle tri) {
        refresh(tri.getA());
        refresh(tri.getB());
        refresh(tri.getC());
    }
    
    public int getXmax() {
        return xmax;
    }

    public int getXmin() {
        return xmin;
    }

    public int getYmax() {
        return ymax;
    }

    public int getYmin() {
        return ymin;
    }


    public GgbAPID getGeoGebraAPI() {
        return geoGebraAPI;
    }

    public void sendCommand(String cmd) {
        if (!cmd.contains("null")) {
            getGeoGebraAPI().evalCommand(cmd);
        }
    }

    @Override
    public String toString() {
        return (xmin + " " + xmax + " " + ymin + " " + ymax); //To change body of generated methods, choose Tools | Templates.
    }

  
    
    
    
}
