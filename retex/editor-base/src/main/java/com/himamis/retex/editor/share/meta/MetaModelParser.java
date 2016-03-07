package com.himamis.retex.editor.share.meta;

import com.himamis.retex.renderer.share.platform.ParserAdapter;
import com.himamis.retex.renderer.share.platform.parser.Element;
import com.himamis.retex.renderer.share.platform.parser.Node;
import com.himamis.retex.renderer.share.platform.parser.NodeList;

import java.util.ArrayList;

/**
 * Created by Balazs on 8/26/2015.
 */
public class MetaModelParser {
    /* Category element. */
    private static final String CHARACTER = "Character";
    private static final String OPERATOR = "Operator";
    private static final String SYMBOL = "Symbol";
    private static final String FUNCTION = "Function";
    private static final String PARAMETER = "Parameter";
    private static final String ARRAY = "Array";
    /* Category attributes. */
    private static final String GROUP = "group";
    private static final String COLUMNS = "columns";
    /* Element attributes. */
    private static final String NAME = "name";
    private static final String DESC = "desc";
    private static final String TYPE = "type";
    private static final String CAS = "cas";
    private static final String TEX = "tex";
    private static final String KEY = "key";
    private static final String UNICODE = "unicode";
    private static final String IMG = "img";
    private static final String UP = "up";
    private static final String DOWN = "down";
    private static final String CODE = "code";
    private static final String FONTID = "fontId";
    private static final String INITIAL = "initial";
    private static final String INSERT = "insert";
    private ParserAdapter parserAdapter = new ParserAdapter();

    private static String getStringAttribute(String attrName, Element element) throws Exception {
        String attrValue = element.getAttribute(attrName);
        if (attrValue == null || attrValue.length() == 0)
            throw new Exception(element.getTagName() + " is null.");
        return attrValue;
    }

    private static int getIntAttribute(String attrName, Element element) throws Exception {
        String attrValue = getStringAttribute(attrName, element);
        int res = 0;
        try {
            res = Integer.parseInt(attrValue);
        } catch (NumberFormatException e) {
            throw new Exception(element.getTagName() + " has invalid value.");
        }
        return res;
    }

    private static char getCharAttribute(String attrName, Element element) throws Exception {
        String attrValue = getStringAttribute(attrName, element);
        char res = 0;
        try {
            res = attrValue.length() > 0 ? attrValue.charAt(0) : 0;
        } catch (NumberFormatException e) {
            throw new Exception(element.getTagName() + " has invalid value.");
        }
        return res;
    }

    public MetaModel parse(Object file) {
        Element root = parserAdapter.createParserAndParseFile(file, true, true);
        // keyboard input, characters and operators
        try {
            return parseComponents(root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private MetaCharacter parseArrayElement(Element element) throws Exception {

        String name = element.getTagName();
        String cas = getStringAttribute(CAS, element);
        String tex = cas;
        char key = cas.length() > 0 ? cas.charAt(0) : 0;

        try {
            tex = getStringAttribute(TEX, element);
        } catch (Exception e) {
        }

        try {
            key = getCharAttribute(KEY, element);
        } catch (Exception e) {
        }

        MetaCharacter metaCharacter = new MetaCharacter(name, cas, tex, key, key, MetaCharacter.CHARACTER);

        return metaCharacter;
    }

    private MetaArray parseArray(Element element) throws Exception {
        String tagName = element.getTagName();
        String name = getStringAttribute(NAME, element);

        ArrayList<MetaComponent> components = new ArrayList<MetaComponent>();
        NodeList elements = element.getChildNodes();
        for (int i = 0; i < elements.getLength(); i++) {
            Node component = elements.item(i);
            if (component.getNodeType() != Node.ELEMENT_NODE) {
                // exclude non-element nodes
                continue;
            }
            Element childElement = component.castToElement();
            MetaCharacter metaComponent = parseArrayElement(childElement);
            components.add(metaComponent);
        }
        MetaArray metaArray = new MetaArray(tagName, name, components);

        return metaArray;
    }

    private MetaCharacter parseCharacter(Element element) throws Exception {

        String name = getStringAttribute(NAME, element);
        String cas = name;
        String tex = name;
        char key = name.length() > 0 ? name.charAt(0) : 0;
        char unicode = key;

        try {
            cas = getStringAttribute(CAS, element);
        } catch (Exception e) {
        }

        try {
            tex = getStringAttribute(TEX, element);
        } catch (Exception e) {
        }

        try {
            key = getCharAttribute(KEY, element);
        } catch (Exception e) {
        }

        try {
            unicode = getCharAttribute(UNICODE, element);
        } catch (Exception e) {

        }

        MetaCharacter metaCharacter = new MetaCharacter(name, cas, tex, key, unicode, MetaCharacter.CHARACTER);

        return metaCharacter;
    }

    private MetaSymbol parseSymbol(Element element) throws Exception {
        String elementName = element.getTagName();
        int type = elementName.equals(OPERATOR) ? MetaCharacter.OPERATOR : MetaCharacter.SYMBOL;

        String name = getStringAttribute(NAME, element);
        String cas = name;
        String tex = name;
        char key = name.length() == 1 ? name.charAt(0) : 0;
        char unicode = key;

        try {
            cas = getStringAttribute(CAS, element);
        } catch (Exception e) {
        }

        try {
            tex = getStringAttribute(TEX, element);
        } catch (Exception e) {
        }

        try {
            key = getCharAttribute(KEY, element);
        } catch (Exception e) {
        }

        int code = 0;
        try {
            code = getIntAttribute(CODE, element);
        } catch (Exception e) {
        }

        int fontId = 0;
        try {
            fontId = getIntAttribute(FONTID, element);
        } catch (Exception e) {
        }
        try {
            unicode = getCharAttribute(UNICODE, element);
        } catch (Exception e) {
        }

        MetaSymbol metaSymbol = new MetaSymbol(name, cas, tex, key, (char) code, unicode, fontId, type);

        return metaSymbol;
    }

    private MetaFunction parseFunction(Element element) throws Exception {
        String name = getStringAttribute(NAME, element);
        String cas = name;
        String tex = name;
        char key = name.length() == 1 ? name.charAt(0) : 0;

        try {
            cas = getStringAttribute(CAS, element);
        } catch (Exception e) {
        }

        try {
            tex = getStringAttribute(TEX, element);
        } catch (Exception e) {
        }

        try {
            key = getCharAttribute(KEY, element);
        } catch (Exception e) {
        }

        ArrayList<MetaParameter> parameterArray = new ArrayList<MetaParameter>();
        NodeList elements = element.getElementsByTagName(PARAMETER);
        for (int i = 0; i < elements.getLength(); i++) {
            Node parameter = elements.item(i);
            String paramName = getStringAttribute(NAME, parameter.castToElement());
            int order = getIntAttribute("order", parameter.castToElement());

            MetaParameter metaParameter = new MetaParameter(paramName, order);
            parameterArray.add(metaParameter);

            try {
                String desc = getStringAttribute(DESC, parameter.castToElement());
                metaParameter.setDescription(desc);
            } catch (Exception e) {
            }

            try {
                int upIndex = getIntAttribute(UP, parameter.castToElement());
                metaParameter.setUpIndex(upIndex);
            } catch (Exception e) {
            }

            try {
                int downIndex = getIntAttribute(DOWN, parameter.castToElement());
                metaParameter.setDownIndex(downIndex);
            } catch (Exception e) {
            }

        }
        MetaParameter parameters[] = parameterArray.toArray(new MetaParameter[parameterArray.size()]);
        MetaFunction metaFunction = new MetaFunction(name, cas, tex, key, parameters);

        try {
            String img = getStringAttribute(IMG, element);
        } catch (Exception e) {
        }

        try {
            String desc = getStringAttribute(DESC, element);
            metaFunction.setDescription(desc);
        } catch (Exception e) {
        }

        try {
            int initialIndex = getIntAttribute(INITIAL, element);
            metaFunction.setInitialIndex(initialIndex);
        } catch (Exception e) {
        }

        try {
            int insertIndex = getIntAttribute(INSERT, element);
            metaFunction.setInsertIndex(insertIndex);
        } catch (Exception e) {
        }

        return metaFunction;
    }

    private MetaModel parseComponents(Element parent) throws Exception {
        MetaModel metaModel = new MetaModel();
        NodeList rootChildNodes = parent.getChildNodes();
        for (int i = 0; i < rootChildNodes.getLength(); i++) {
            Node rootChildNode = rootChildNodes.item(i);
            if (rootChildNode.getNodeType() != Node.ELEMENT_NODE) {
                // exclude non-element nodes
                continue;
            }
            Element rootChild = rootChildNode.castToElement();
            String groupName = rootChild.getTagName(), group = groupName;
            int columns = 0;
            try {
                groupName = getStringAttribute(NAME, rootChild);
            } catch (Exception e) {
            }

            try {
                group = getStringAttribute(GROUP, rootChild);
            } catch (Exception e) {
            }

            try {
                columns = getIntAttribute(COLUMNS, rootChild);
            } catch (Exception e) {
            }

            ArrayList<MetaComponent> metas = new ArrayList<MetaComponent>();
            NodeList elementsChildNodes = rootChild.getChildNodes();
            for (int j = 0; j < elementsChildNodes.getLength(); j++) {
                Node elementChildNode = elementsChildNodes.item(j);
                if (elementChildNode.getNodeType() != Node.ELEMENT_NODE) {
                    // exclude non-element nodes
                    continue;
                }
                Element elementChild = elementChildNode.castToElement();
                String name = elementChild.getTagName();
                /*if (name.equals(MetaModel.OPEN) || name.equals(MetaModel.CLOSE) ||
                        name.equals(MetaModel.FIELD) || name.equals(MetaModel.ROW)) {
                    MetaComponent metaComponent = parseArrayElement(elementChild);
                    metas.add(metaComponent);*/
                if (name.equals(ARRAY)) {
                    MetaArray metaArray = parseArray(elementChild);
                    metas.add(metaArray);
                } else if (name.equals(OPERATOR)) {
                    MetaSymbol metaOperator = parseSymbol(elementChild);
                    metas.add(metaOperator);

                } else if (name.equals(SYMBOL)) {
                    MetaSymbol metaSymbol = parseSymbol(elementChild);
                    metas.add(metaSymbol);

                } else if (name.equals(FUNCTION)) {
                    MetaFunction metaFunction = parseFunction(elementChild);
                    metas.add(metaFunction);
                } else if (name.equals(CHARACTER)) {
                    MetaCharacter metaCharacter = parseCharacter(elementChild);
                    metas.add(metaCharacter);
                }
            }

            // TODO fix matrix
            /*if (groupName.equals(MetaModel.MATRIX)) {
                metaModel.addGroup(new MetaArray(groupName, group, metas));
            } else {*/
            metaModel.addGroup(new ListMetaGroup(groupName, group, metas, columns));
            //}
        }
        return metaModel;
    }
}
