/* $Id: JacomaxSimpleConfigurator.java 5 2010-03-19 15:40:39Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax;

import geogebra.cas.maxima.jacomax.internal.DummyLogger;

import java.util.Arrays;


/**
 * This configurator uses a mix of {@link JacomaxAutoConfigurator} and 
 * {@link JacomaxPropertiesConfigurator} and is probably the simplest way of getting a useful
 * {@link MaximaConfiguration} without requiring much effort.
 *
 * @author  David McKain
 * @version $Revision: 5 $
 */
public final class JacomaxSimpleConfigurator {
    
    private static final DummyLogger logger = new DummyLogger(); //LoggerFactory.getLogger(JacomaxSimpleConfigurator.class);
    
    /** Enumerate the methods we'll use to obtain a MaximaConfiguration here */
    public static enum ConfigMethod {
        
        /** Use {@link JacomaxAutoConfigurator} */
        AUTO,
        
        /** Use {@link JacomaxPropertiesConfigurator} to search for properties in the default locations */
        PROPERTIES_SEARCH,
        ;
    }
    
    /**
     * Tries to obtain a {@link MaximaConfiguration} by first using {@link JacomaxPropertiesConfigurator}
     * to look for an appropriate Properties file, then using {@link JacomaxAutoConfigurator} if
     * that doesn't work.
     * 
     * @return resulting {@link MaximaConfiguration}
     * 
     * @throws JacomaxConfigurationException if this process didn't yield anything useful.
     */
    public static MaximaConfiguration configure() {
        return configure(ConfigMethod.PROPERTIES_SEARCH, ConfigMethod.AUTO);
    }
    
    /**
     * Tries to obtain a {@link MaximaConfiguration} using the given methods in order.
     * 
     * @param configMethods methods to try in order to obtain a {@link MaximaConfiguration} 
     * 
     * @return resulting {@link MaximaConfiguration}
     * 
     * @throws JacomaxConfigurationException if this process didn't yield anything useful.
     */
    public static MaximaConfiguration configure(ConfigMethod... configMethods) {
        MaximaConfiguration result = null;
        for (ConfigMethod method : configMethods) {
            switch (method) {
                case AUTO:
                    logger.debug("Trying automatic configuration");
                    result = JacomaxAutoConfigurator.guessMaximaConfiguration();
                    if (result==null) {
                        logger.debug("Automatic configuration attempt did not succeed");
                    }
                    break;
                    
                case PROPERTIES_SEARCH:
                    logger.debug("Trying configuration via properties search in default locations");
                    JacomaxPropertiesConfigurator propertiesConfigurator;
                    try {
                        propertiesConfigurator = new JacomaxPropertiesConfigurator();
                    }
                    catch (JacomaxConfigurationException e) {
                        logger.debug("Properties search did not succeed");
                        break;
                    }
                    result = propertiesConfigurator.configure();
                    break;
                    
                default:
                    throw new JacomaxLogicException("Unexpected switch case " + method);
            }
            if (result!=null) {
                break;
            }
        }
        if (result==null) {
            logger.warn("Configuration did not yield anything");
            throw new JacomaxConfigurationException("Could not obtain a MaximaConfiguration after attempting configuration methods "
                    + Arrays.toString(configMethods));
        }
        return result;
    }
 
}
