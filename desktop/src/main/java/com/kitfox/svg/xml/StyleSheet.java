/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.svg.xml;

import com.kitfox.svg.SVGConst;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class StyleSheet
{
    HashMap ruleMap = new HashMap();

    public static StyleSheet parseSheet(String src)
    {
        //Implement CS parser later
        Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
            "CSS parser not implemented yet");
        return null;
    }
    
    public void addStyleRule(StyleSheetRule rule, String value)
    {
        ruleMap.put(rule, value);
    }
    
    public boolean getStyle(StyleAttribute attrib, String tagName, String cssClass)
    {
        StyleSheetRule rule = new StyleSheetRule(attrib.getName(), tagName, cssClass);
        String value = (String)ruleMap.get(rule);
        
        if (value != null)
        {
            attrib.setStringValue(value);
            return true;
        }
        
        //Try again using just class name
        rule = new StyleSheetRule(attrib.getName(), null, cssClass);
        value = (String)ruleMap.get(rule);
        
        if (value != null)
        {
            attrib.setStringValue(value);
            return true;
        }
        
        //Try again using just tag name
        rule = new StyleSheetRule(attrib.getName(), tagName, null);
        value = (String)ruleMap.get(rule);
        
        if (value != null)
        {
            attrib.setStringValue(value);
            return true;
        }
        
        return false;
    }
    
}
