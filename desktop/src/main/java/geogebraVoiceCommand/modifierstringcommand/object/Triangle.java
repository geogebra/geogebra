/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geogebraVoiceCommand.modifierstringcommand.object;

import geogebraVoiceCommand.FieldViewer;
import static geogebraVoiceCommand.modifierstringcommand.object.CmdBuilder.fView;
import static geogebraVoiceCommand.modifierstringcommand.object.CmdBuilder.identificationCmd;
import static geogebraVoiceCommand.modifierstringcommand.object.CmdBuilder.stackTriangle;

/**
 *
 * @author Darkyver
 */
public class Triangle {

    Point A, B, C;
    double area = 0;
    int index;

    private void ini() {
        A = new Point();
        B = new Point();
        C = new Point();
    }

    public Triangle() {
        ini();
    }

    public Triangle(Point A, Point B, Point C) {
        ini();
        this.A = A;
        this.B = B;
        this.C = C;
    }

    public Triangle(Segment AB, Segment BC, Segment CA) {
        ini();
        this.A = AB.getA();
        this.B = BC.getA();
        this.C = CA.getA();
    }

    @Override
    public String toString() {
        return "Polygon(" + A.toString() + ","
                + B.toString() + "," + C.toString() + ")";
    }

    public void setA(Point p) {
        A = p;
    }

    public void setB(Point p) {
        B = p;
    }

    public void setC(Point p) {
        C = p;
    }

    public Point getA() {
        return A;
    }

    public Point getB() {
        return B;
    }

    public Point getC() {
        return C;
    }

    void buildTriangle() {
        //создаём экземпляры точек вершин треугольника
        //изначально, координаты точки устанавливается случайно
//0- случайный, 1 - по точкам, 2 -прямоугольный, 3 - равнобедренный, 4 - равносторонний
        //проверяем есть ли команда "по точкам" или "по предыдущим точкам"
        //проверяем есть ли в "стеке" две точки
        //если эти два условия выполнились, то берём из стека эти три точки и копируем их параметры в наши точки
        if (identificationCmd.typeTryangle == 1 && CmdBuilder.stackPoint.getTop() >= 2) {
            //1 - по точкам
            //первая точка
            setA(CmdBuilder.stackPoint.pop());
            //вторая точка
            setB(CmdBuilder.stackPoint.pop());
            //третья точка
            setC(CmdBuilder.stackPoint.pop());
            //если мы создаём треугольник по точкам, то сами точки уже есть и отображать их не нужно
        } else if (identificationCmd.typeTryangle == 2) {
            //2 -прямоугольный
            B.setPoint(A.getX(), B.getY());
            C.setPoint(C.getX(), A.getY());
            A.executeCMD();
            B.executeCMD();
            C.executeCMD();
        } else if (identificationCmd.typeTryangle == 3) {
            A.setPoint(A.getX(), C.getY());
            double Bx = A.getX() > C.getX() ? (Math.abs(A.getX() - C.getX())) / 2 + C.getX()
                    : (Math.abs(C.getX() - A.getX())) / 2 + A.getX();
            double By = (B.getY() <= A.getY()) ? B.getY() * -1 : B.getY();
            B.setPoint(Bx, B.getY());
            A.executeCMD();
            B.executeCMD();
            C.executeCMD();
        } else {
            //иначе, если нет либо команды, либо точек, то мы должны точки отобразить (построить в ГГБ)
            A.executeCMD();
            B.executeCMD();
            C.executeCMD();
        }
        //теперь строим сам треугольник по точкам
        executeCMD();
        index = fView.getGeoGebraAPI().getObjectNumber();
        //проверяем всё ли помещается в текущем масштабе, если нужно, сдвигаем область
        fView.refresh(this);
    }

    public void executeCMD() {
        fView.sendCommand(toString());
        area = getArea();
        stackTriangle.push(this);
    }

    public double getArea() {
        //чтобы найти площадь треугольника по координатам его вершин, нам нужно посчитать 
        //определитель и поделить его абсолютное значение на 2
        double a11 = A.getX() - C.getX();
        double a12 = A.getY() - C.getY();
        double a21 = B.getX() - C.getX();
        double a22 = B.getY() - C.getY();
        return Math.abs(a11 * a22 - a21 * a12) / 2;
    }
    
    public void showArea(){
        fView.getGeoGebraAPI().alert(Double.toString(area));
    }
}
