// Copyright 2000, SLAC, Stanford, California, U.S.A.
package org.freehep.xml.util;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Utility class for traversing XML DOM trees. 
 * This is an abstract class that has methods which are
 * called as different elements are located in a DOM tree.
 * It is designed to be extended for each nore in a specific
 * DOM tree, with its methods overriden to handle specific
 * elements and attributes of the tree.
 * 
 * @deprecated The design of this class is misguided, it is better to use SAX, and SAXTraverser. Will be removed from future FreeHEP releases
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: XMLNodeTraverser.java,v 1.4 2009-06-22 02:18:23 hohenwarter Exp $
 */
public abstract class XMLNodeTraverser
{
	/**
	 * Traverse a node
	 * @param node The node to traverse
	 */
	public void traverse(Node node) throws BadXMLException
	{
		if (node instanceof Element)
		{
			attrs = ((Element) node).getAttributes();
			handleElementAttributes(attrs);
		}
		for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling())
		{
			handleSubNode(n,n.getNodeName());
		}
		attrs = null; // for efficiency, dont hold on to attributes
	}
	/**
	 * Called to handle a subnode of the node being traversed.
	 * The default implementation calls handleElement, handleTextNode 
	 * or handleOtherNode as appropriate.
	 * @param node The subnode being handled
	 * @param name The name of the subnode
	 */
	protected void handleSubNode(Node node, String name) throws BadXMLException
	{
        int type = node.getNodeType();
        switch (type) 
		{
            case Node.ELEMENT_NODE: 
				handleElement((Element) node,name);
				break;
			case Node.TEXT_NODE:
				handleTextNode((Text) node,name);
                break;
			default:
				handleOtherNode(node,name);
        }
	}
	/**
	 * Called to handle the attributes associated with the
	 * element being traversed. The default implementation calls
	 * handleAttributeNode for each attribute
	 * @param nnm The attrinbutes being handled
	 */
	protected void handleElementAttributes(NamedNodeMap nnm) throws BadXMLException
	{
		for (int i=0; i<nnm.getLength(); i++) 
		{
			Attr attr = (Attr) nnm.item(i);
			handleAttributeNode(attr,attr.getName(),attr.getValue());
		}
	}
	/**
	 * Handle an Element subnode.
	 * The default implementation throws an exception.
	 * @param node The element being handled
	 * @param name The name of the element
	 */
	protected void handleElement(Element node, String name) throws BadXMLException
	{
		throw new BadXMLException("Unhandled Element node "+node);
	}
	/**
	 * Handle a text node.
	 * The default implementation does nothing
	 */
	protected void handleTextNode(Text node, String name) throws BadXMLException
	{
		// Just ignore unhandled text
	}
	/**
	 * Handle an element attribute.
	 * The default implementation throws an exception
	 * @param node The attribute being handled
	 * @param name The name of the attribute
	 * @param value The value of the attribute
	 */
	protected void handleAttributeNode(Attr node, String name, String value) throws BadXMLException
	{
		throw new BadXMLException("Unhandled Attribute node "+node);
	}
	/**
	 * Handle a node other than an Element or TextNode.
	 * The default implementation throws an exception
	 * @param node The node being handled
	 * @param name The name of the node
	 */
	protected void handleOtherNode(Node node, String name) throws BadXMLException
	{
		throw new BadXMLException("Unhandled Other node "+node+" type="+node.getNodeType());
	}
	/**
	 * Utility routine to convert a String to an int
	 */
	public int toInt(String value) throws BadXMLException
	{
		try
		{
			return Integer.parseInt(value);
		}
		catch (Throwable x)
		{
			throw new BadXMLException("Bad integer value "+value);
		}
	}
	/**
	 * Convenience method to get the value of some attribute.
	 * Note, this will always return null if called
	 * while the traverse method is not active. Returns null
	 * if the attribute does not exist.
	 */
	public String getAttributeValue(String name)
	{
		if (attrs == null) return null;
		Node attr = attrs.getNamedItem(name);
		if (attr == null) return null;
		return attr.getNodeValue();
	}
	/**
	 * Utility routine to convert a String to a boolean
	 */
	public boolean toBoolean(String value) throws BadXMLException
	{
			if      (value.equalsIgnoreCase("true")) return true;
			else if (value.equalsIgnoreCase("false")) return false;
			else throw new BadXMLException("Bad boolean value "+value);
	}
	/**
	 * Utility routine to convert a String to a double
	 */
	public double toDouble(String value) throws BadXMLException
	{
		try
		{
			return new Double(value).doubleValue();
		}
		catch (Throwable x)
		{
			throw new BadXMLException("Bad double value "+value);
		}
	}
	/**
	 * Exception to throw for any kind of problem during 
	 * the node traversal
	 */
	public static class BadXMLException extends Exception 
	{  
		/**
		 * 
		 */
        private static final long serialVersionUID = 68032324630130555L;
		public BadXMLException() { super(); }
		public BadXMLException(String s) { super(s); }
	}
	private NamedNodeMap attrs;
}
