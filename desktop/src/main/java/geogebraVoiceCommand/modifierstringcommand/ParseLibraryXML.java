package geogebraVoiceCommand.modifierstringcommand;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Darkyver
 */
public class ParseLibraryXML {
	private Document document;
	private XPath xpath;
	private String pathLib = "desktop/src/main/resources/org/geogebra/desktop/geogebraVoiceCommand/mainTable.xml";


	public ParseLibraryXML() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			File f = new File(String.valueOf(getClass().getResource(pathLib)));
			document = builder.parse(new File(pathLib));
			XPathFactory xPathFactory = XPathFactory.newInstance();
			xpath = xPathFactory.newXPath();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	//Метод принимает строку параметров, по которым парсит библиотеку корней.
    //Возвращает NodeList результата
    public NodeList evaluateLib(String eval) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException  {
        if(xpath != null && document != null) {
			XPathExpression xPathExpression = xpath.compile(eval);
			NodeList list = (NodeList) xPathExpression.evaluate(document, XPathConstants.NODESET);
			return list;
		}
        return null;
    }

    //метод парсит библиотеку переданными ему параметрами и печатает результаты
    public void printEvaLib(String eval) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
        printNodeList(evaluateLib(eval));
    }

    //метод печатает переданный ему NodeList. Если лист пуст, то печатает null
    public void printNodeList(NodeList list){
        if (list!=null){
            for (int i=0;i<list.getLength();i++){
                System.out.println(list.item(i).getTextContent());
            }
        } else System.out.println("null");
    }

}
