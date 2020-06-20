/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geogebraVoiceCommand.modifierstringcommand.object;

import static geogebraVoiceCommand.modifierstringcommand.object.CmdBuilder.fView;
import static geogebraVoiceCommand.modifierstringcommand.object.CmdBuilder.identificationCmd;

/**
 *
 * @author Darkyver
 */
public class Segment {

    Point A, B;

    public Segment() {
        A = new Point();
        B = new Point();
        this.setRandom();
        //добавляем точку в стек объектов
    }

    public Segment(Point A, Point B) {
        this.A = A;
        this.B = B;
    }

    public Segment(Segment seg) {
        A = new Point();
        B = new Point();
        this.setSegment(seg);
    }

    public void setRandom() {
        A.randomSet();
        B.randomSet();
    }

    public void setA(Point A) {
        this.A = A;
    }

    public void setA(double x, double y) {
        this.A = new Point(x, y);
    }

    public void setB(Point B) {
        this.B = B;
    }

    public void setB(double x, double y) {
        this.B = new Point(x, y);
    }

    public void setSegment(Point A, Point B) {
        this.setA(A);
        this.setB(B);
    }

    public void setSegment(double x1, double y1, double x2, double y2) {
        this.setA(x1, y1);
        this.setB(x2, y2);
    }

    public void setSegment(Segment seg) {
        this.setSegment(seg.getA(), seg.getB());
    }

    public Point getA() {
        return A;
    }

    public Point getB() {
        return B;
    }

    public boolean equals(Segment segm) {
        return (this.getA().equals(segm.getA())
                && this.getB().equals(segm.getB()));
    }

    @Override
    public String toString() {
        return ("Segment(" + A.toString() + "," + B.toString() + ")"); //To change body of generated methods, choose Tools | Templates.
    }

    void buildSegment() {
        //создаём экземпляры точек концов отрезка
        //изначально, координаты точки устанавливается случайно
        A = new Point();
        B = new Point();
        //проверяем есть ли команда "по точкам" или "по предыдущим точкам"
        boolean id = identificationCmd.previousPoint;
        //проверяем есть ли в "стеке" две точки
        boolean top = CmdBuilder.stackPoint.getTop() >= 1;
        //если эти два условия выполнились, то берём из стека эти две точки и копируем их параметры в наши точки
        if (id && top) {
            //последняя точка
            setA(CmdBuilder.stackPoint.pop());
            //предпоследняя точка
            setB(CmdBuilder.stackPoint.pop());
            //если мы создаём отрезок по точкам, то сами точки уже есть и отображать их не нужно
        } else {
            //иначе, если нет либо команды, либо точек, то мы должны точки отобразить (построить в ГГБ)
            A.executeCMD();
            B.executeCMD();
        }
        //теперь строим сам отрезок между точками
        executeCMD();
        //проверяем всё ли помещается в текущем масштабе, если нужно сдвигаем область
        fView.refresh(this);
    }

    public void executeCMD() {
        fView.sendCommand(toString());
    }

}
