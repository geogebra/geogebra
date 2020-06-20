/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geogebraVoiceCommand.modifierstringcommand.object;

import geogebraVoiceCommand.modifierstringcommand.ParseLibraryXML;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Darkyver
 */
public class IdentificationCmd {

    private ParseLibraryXML parse;

    private String actionName = "null";
    private String objectName = "null";
    private String actionContext = "null";
    private double X;
    private double Y;
    boolean XYAvailable = false;
    boolean previousPoint = false;
    //0- случайный, 1 - по точкам, 2 -прямоугольный, 3 - равнобедренный, 4 - равносторонний
    int typeTryangle = 0;
    public IdentificationCmd() {
        parse = new ParseLibraryXML();
    }

    public IdentificationCmd(String inputStr) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        parse = new ParseLibraryXML();
        detect(inputStr);
    }

    public void detect(String inputStr) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        //разбиваем входную строку на отдельные слова и помещаем в массив
        String[] arrInputStr = inputStr.split(" ");
        //определяем действие
        actionName = getActionByTagArr(arrInputStr);
        //Определяем объект
        objectName = getObjectByTagArr(arrInputStr);
        //определяем контекст действия
        actionContext = getContextByAction(arrInputStr, actionName);
        //определяем контекст объекта (пока что координаты точки)
        XYAvailable = false;
        previousPoint = false;
        getContextByObject(arrInputStr, objectName);

    }

    String[] nodeToArray(NodeList list) {
        String[] arrLibraryStr = new String[list.getLength()];
        for (int i = 0; i < list.getLength(); i++) {
            arrLibraryStr[i] = list.item(i).getTextContent();
        }
        return arrLibraryStr;
    }

    //проведём поиск по всем введённым словам, сравнивая с корнями из библиотеки
    //если слово не найдено, вернуть null
    private String posWordByRoot(String[] arrInputStr, String[] arrLibraryStr) {
        String resultStr = "null";
        String inputWord = "";

        for (String i : arrInputStr) {
            inputWord += i + " ";
        }
        inputWord = inputWord.toLowerCase();
        for (int i = 0; i < arrLibraryStr.length; i++) {
            if (inputWord.contains(arrLibraryStr[i])) {
                resultStr = arrLibraryStr[i];
                break;
            }
        }

        return resultStr;
    }

    //метод определяет вид команды (построить/удалить/переместить) по ключевому слову команды
    private String getActionByTagArr(String[] arrInputStr) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        //Получаем все вероятные корни всех видов команд (построить/удалить/изменить)
        NodeList list = parse.evaluateLib("/root/command/items/KeyWord/td");
        //NodeList разбираем на отдельные ноды и помещаем в массив строк
        String[] arrLibraryCommandStr = nodeToArray(list);
        String nameNode = posWordByRoot(arrInputStr, arrLibraryCommandStr);
        //ищем по найденному корню вид действия (построить/удалить/изменить)
        if (!nameNode.equals("null")) {
            String eval = "/root//KeyWord[td='" + nameNode + "']/parent::*/name";
            list = parse.evaluateLib(eval);
            //возвращаем результат в виде строки 
            return (list.item(0).getTextContent());
        } else {
            return null;
        }
    }

    //метод определяет объект по ключевому слову команды
    private String getObjectByTagArr(String[] arrInputStr) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        ParseLibraryXML parse = new ParseLibraryXML();
        String eval = "/root/objects/items/KeyWord/td";
        NodeList list = parse.evaluateLib(eval);
        String[] arrLibraryObjectStr = nodeToArray(list);
        String objectName = posWordByRoot(arrInputStr, arrLibraryObjectStr);
        if (!objectName.equals("null")) {
            eval = "/root/objects/items/KeyWord[td='" + objectName + "']/parent::*/name";
            list = parse.evaluateLib(eval);
            objectName = nodeToArray(list)[0];
        }
        return objectName;
    }

    //метод возвращает контекст действия (сдвинуть вправо/удалить все и т.д.) по ключевому слову команды
    //из всей введённой строки
    private String getContextByAction(String[] arrInputStr, String nameAction) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        //объект парсера, для загрузки нужных нод из таблицы
        ParseLibraryXML parse = new ParseLibraryXML();
        //
        String eval = "/root//items[name='" + nameAction + "']/KeyWordContext//@*";
        //парсим все результаты в NodeList
        NodeList list = parse.evaluateLib(eval);
        //копируем полученные ноды в массив
        String[] arrLibraryContextStr = nodeToArray(list);
        //ищем в массиве входной строки наши ключевые ноды
        String actionContext = posWordByRoot(arrInputStr, arrLibraryContextStr);
        //если результат найден, ОПРЕДЕЛЯЕМ КЛЮЧ ДЕЙСТВИЯ
        if (!actionContext.equals("null")) {
            eval = "//KeyWordContext//td[@actionContext='" + actionContext + "']";
            list = parse.evaluateLib(eval);
            //берём ключ действия
            actionContext = nodeToArray(list)[0];
        }
        //возвращаем результат
        return actionContext;
    }

    private void getContextByObject(String[] arrInputStr, String nameObject) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        int indexX = -1, indexY = -1;
        //объект парсера, для загрузки нужных нод из таблицы
        ParseLibraryXML parse = new ParseLibraryXML();
        //формируем запрос в таблицу
        String eval = "//items[name='" + nameObject + "']/KeyWordContext/*";
        //парсим все результаты в NodeList
        NodeList list = parse.evaluateLib(eval);
        //копируем полученные ноды в массив
        String[] arrLibraryContextStr = nodeToArray(list);
        //ищем в массиве входной строки наши ключевые ноды
        String objectContext = (posWordByRoot(arrInputStr, arrLibraryContextStr));

        switch (nameObject) {
            case ("Point"):
                for (int i = 0; i < arrInputStr.length; i++) {
                    try {   
                        if (indexX == -1) {
                            X = Double.parseDouble(arrInputStr[i]);
                            indexX = i;
                        } else {
                            Y = Double.parseDouble(arrInputStr[i]);
                            indexY = i;
                            XYAvailable = true;
                            break;
                        }
                    } catch (NumberFormatException e) {
//                X = 0;
//                Y = 0;
                    }
                }
                break;
            case ("Segment"):
                /*
                switch (objectContext) {
                    case ("по точк"):
                    case ("по предыдущ"):
                        previousPoint = true;
                        break;
                }
                */
                if (objectContext.equalsIgnoreCase(new String("по точк".getBytes(),"utf-8"))
                        || objectContext.equalsIgnoreCase(new String("по предыдущ".getBytes(),"utf-8"))){
                    previousPoint = true;
                }
                break;
            case ("Triangle"):
                if (objectContext.equalsIgnoreCase(new String("по точк".getBytes(),"utf-8"))
                        || objectContext.equalsIgnoreCase(new String("по предыдущ".getBytes(),"utf-8"))){
                    typeTryangle = 1;
                } else if (objectContext.equalsIgnoreCase(new String("прямоугольн".getBytes(),"utf-8"))){
                    typeTryangle = 2;
                }
                else if (objectContext.equalsIgnoreCase(new String("равнобедр".getBytes(),"utf-8"))){
                    typeTryangle = 3;
                }
                break;
        }

    }

    public String getAction() {
        return actionName;
    }

    public String getObject() {
        return objectName;
    }

    public String getActionContext() {
        return actionContext;
    }

    public String getResultCmd() {
        return (actionName + " " + objectName + " " + actionContext);
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

}
