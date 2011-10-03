/* $Id: JacomaxPropertiesConfigurator.java 5 2010-03-19 15:40:39Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax;

import geogebra.cas.maxima.jacomax.internal.DummyLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;



/**
 * This class populates a {@link MaximaConfiguration} Object from a Java {@link Properties} file
 * or Object, with convenience constructors for searching for a {@link Properties} file in a number
 * of useful locations.
 * 
 * <h2>Construction</h2>
 * 
 * Use one of the following constructors as appropriate:
 * 
 * <ul>
 *   <li>
 *     The default no argument constructor will look for a File called
 *     {@link #DEFAULT_PROPERTIES_RESOURCE_NAME} by searching (in order):
 *     the current working directory, your home directory, the ClassPath.
 *     If nothing is found, a {@link JacomaxConfigurationException} will be thrown.
 *   </li>
 *   <li>
 *     Use {@link #JacomaxPropertiesConfigurator(String, PropertiesSearchLocation...)}
 *     to search for a {@link Properties} file of the given name in the given
 *     {@link PropertiesSearchLocation}s. The first match wins. 
 *     If nothing is found, a {@link JacomaxConfigurationException} will be thrown.
 *   </li>
 *   <li>
 *     Use the {@link File} or {@link Properties} constructor if you want to explicitly use
 *     the given {@link File} or {@link Properties} Object to supply configuration information.
 *   </li>
 * </ul>
 * 
 * <h2>Properties File Format</h2>
 * 
 * See <tt>jacomax.properties.sample</tt> for an example of the required/supported property
 * names and values.
 * 
 * @see MaximaConfiguration
 * @see JacomaxAutoConfigurator
 * @see JacomaxSimpleConfigurator
 * 
 * @author  David McKain
 * @version $Revision: 5 $
 */
public final class JacomaxPropertiesConfigurator {
    
    private static final DummyLogger logger = new DummyLogger();// LoggerFactory.getLogger(JacomaxPropertiesConfigurator.class);
    
    /** Enumerates the various locations to search in. */ 
    public static enum PropertiesSearchLocation {
        
        CURRENT_DIRECTORY,
        USER_HOME_DIRECTORY,
        CLASSPATH,
        SYSTEM,
        ;
    }
    
    /** Default properties resource name, used if nothing explicit stated */
    public static final String DEFAULT_PROPERTIES_RESOURCE_NAME = "jacomax.properties";
    
    /** Name of property specifying {@link MaximaConfiguration#getMaximaExecutablePath()} */
    public static final String MAXIMA_EXECUTABLE_PATH_PROPERTY_NAME = "jacomax.maxima.path";
    
    /** Base name of properties specifying {@link MaximaConfiguration#getMaximaCommandArguments()} */
    public static final String MAXIMA_COMMAND_ARGUMENTS_PROPERTY_BASE_NAME = "jacomax.maxima.arg";
    
    /** Base name of properties specifying {@link MaximaConfiguration#getMaximaRuntimeEnvironment()} */
    public static final String MAXIMA_ENVIRONMENT_PROPERTY_BASE_NAME = "jacomax.maxima.env";
    
    /** Name of property specifying {@link MaximaConfiguration#getMaximaCharset()} */
    public static final String MAXIMA_CHARSET_PROPERTY_NAME = "jacomax.maxima.charset";
    
    /** Name of property specifying {@link MaximaConfiguration#getDefaultCallTimeout()} */
    public static final String DEFAULT_CALL_TIMEOUT_PROPERTY_NAME = "jacomax.default.call.timeout";

    /** Name of property specifying {@link MaximaConfiguration#getDefaultBatchTimeout()} */
    public static final String DEFAULT_BATCH_TIMEOUT_PROPERTY_NAME = "jacomax.default.batch.timeout";
 
    /** Resolved Properties */
    private final Properties properties;
    
    /** Description of where {@link #properties} was resolved from */
    private final String propertiesSourceDescription;
    
    //----------------------------------------------------------------
    
    /**
     * This constructor looks for a resource called {@link #DEFAULT_PROPERTIES_RESOURCE_NAME}
     * by searching the current directory, your home directory and finally the ClassPath. 
     */
    public JacomaxPropertiesConfigurator() {
        this(DEFAULT_PROPERTIES_RESOURCE_NAME,
                PropertiesSearchLocation.CURRENT_DIRECTORY,
                PropertiesSearchLocation.USER_HOME_DIRECTORY,
                PropertiesSearchLocation.CLASSPATH);
    }
    
    /**
     * This constructor looks for a resource called propertiesName, searching in the locations
     * specified in the order specified.
     * <p>
     * Note that {@link PropertiesSearchLocation#SYSTEM} will always "win" over anything appearing
     * after it.
     */
    public JacomaxPropertiesConfigurator(String propertiesName, PropertiesSearchLocation... propertiesSearchPath) {
        Properties theProperties = null;
        String thePropertiesSourceDescription = null;
        File tryFile;
        SEARCH: for (PropertiesSearchLocation location : propertiesSearchPath) {
            switch (location) {
                case CURRENT_DIRECTORY:
                    tryFile = new File(System.getProperty("user.dir"), propertiesName);
                    theProperties = tryPropertiesFile(tryFile);
                    if (theProperties!=null) {
                        logger.info("Creating Maxima configuration from properties file {} found in current directory", tryFile.getPath());
                        thePropertiesSourceDescription = "File " + tryFile.getPath() + " (found in current directory)";
                        break SEARCH;
                    }
                    continue SEARCH;
                    
                case USER_HOME_DIRECTORY:
                    tryFile = new File(System.getProperty("user.home"), propertiesName);
                    theProperties = tryPropertiesFile(tryFile);
                    if (theProperties!=null) {
                        logger.info("Creating Maxima configuration from properties file {} found in user home directory", tryFile.getPath());
                        thePropertiesSourceDescription = "File " + tryFile.getPath() + " (found in user home directory)";
                        break SEARCH;
                    }
                    continue SEARCH;
                    
                case CLASSPATH:
                    InputStream propertiesStream = JacomaxPropertiesConfigurator.class.getClassLoader().getResourceAsStream(propertiesName);
                    if (propertiesStream!=null) {
                        theProperties = readProperties(propertiesStream, "ClassPath resource " + propertiesName);
                        logger.info("Creating Maxima configuration using properties file {} found in ClassPath", propertiesStream);
                        thePropertiesSourceDescription = "ClassPath resource " + propertiesName;
                        break SEARCH;
                    }
                    continue SEARCH;
                    
                case SYSTEM:
                    theProperties = System.getProperties();
                    logger.info("Creating Maxima configuration from System properties");
                    thePropertiesSourceDescription = "System properties";
                    break SEARCH;
                    
                default:
                    throw new JacomaxLogicException("Unexpected switch fall-through on " + location);
            }
        }
        if (theProperties==null) {
            throw new JacomaxConfigurationException("Could not load properties file/resource " + propertiesName
                    + " using search path "
                    + Arrays.toString(propertiesSearchPath));
        }
        this.properties = theProperties;
        this.propertiesSourceDescription = thePropertiesSourceDescription;
    }
    
    /**
     * This constructor uses the provided {@link Properties} Object as a source of
     * configuration information.
     */
    public JacomaxPropertiesConfigurator(Properties maximaProperties) {
        this.properties = maximaProperties;
        this.propertiesSourceDescription = "Properties Object " + maximaProperties;
    }
    
    /**
     * This constructor uses the provided {@link File} as a source of configuration information.
     * configuration information.
     */
    public JacomaxPropertiesConfigurator(File propertiesFile) throws FileNotFoundException {
        this.properties = readProperties(new FileInputStream(propertiesFile), "File " + propertiesFile.getPath());
        this.propertiesSourceDescription = "Explicitly specified File " + propertiesFile.getPath();
    }
    
    /**
     * This constructor uses the provided {@link File} as a source of configuration information.
     * configuration information.
     */
    public JacomaxPropertiesConfigurator(InputStream inputStream) {
        this.properties = readProperties(inputStream, "Stream " + inputStream.toString());
        this.propertiesSourceDescription = "Explicitly specified InputStream " + inputStream.toString();
    }
    
    private Properties tryPropertiesFile(File file) {
        InputStream propertiesStream;
        logger.debug("Checking for existence of Jacomax properties file at {}", file.getPath());
        try {
            propertiesStream = new FileInputStream(file);
            logger.debug("Found {}", file.getPath());
            return readProperties(propertiesStream, "File " + file.getPath());
        }
        catch (FileNotFoundException e) {
            logger.debug("Did not find {}", file.getPath());
            return null;
        }
    }
    
    private Properties readProperties(InputStream inputStream, String inputDescription) {
        Properties result = new Properties();
        try {
            result.load(inputStream);
        }
        catch (IOException e) {
            throw new JacomaxConfigurationException("IOException occurred when reading Maxima properties from "
                    + inputDescription, e);
        }
        return result;
    }
    
    //----------------------------------------------------------------
    
    public Properties getProperties() {
        return properties;
    }
    
    public String getPropertiesSourceDescription() {
        return propertiesSourceDescription;
    }
    
    //----------------------------------------------------------------
    
    public MaximaConfiguration configure() {
        MaximaConfiguration result = new MaximaConfiguration();
        configure(result);
        return result;
    }
    
    public void configure(MaximaConfiguration config) {
        config.setMaximaExecutablePath(getRequiredProperty(MAXIMA_EXECUTABLE_PATH_PROPERTY_NAME));
        config.setMaximaCommandArguments(getIndexedProperty(MAXIMA_COMMAND_ARGUMENTS_PROPERTY_BASE_NAME));
        config.setMaximaRuntimeEnvironment(getIndexedProperty(MAXIMA_ENVIRONMENT_PROPERTY_BASE_NAME));
        config.setMaximaCharset(getProperty(MAXIMA_CHARSET_PROPERTY_NAME));
        config.setDefaultCallTimeout(getIntegerProperty(DEFAULT_CALL_TIMEOUT_PROPERTY_NAME));
        config.setDefaultBatchTimeout(getIntegerProperty(DEFAULT_BATCH_TIMEOUT_PROPERTY_NAME));
    }
    
    //----------------------------------------------------------------

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    public String getRequiredProperty(String propertyName) {
        String result = getProperty(propertyName);
        if (result==null) {
            throw new JacomaxConfigurationException("Required property " + propertyName
                    + " not specified in " + propertiesSourceDescription);
        }
        return result;
    }
    
    public String[] getIndexedProperty(String propertyNameBase) {
        List<String> resultList = new ArrayList<String>();
        String indexedValue;
        for (int i=0; ;i++) { /* (Keep reading until we get a null or empty property) */
            indexedValue = getProperty(propertyNameBase + i);
            if (indexedValue==null || indexedValue.trim().equals("") ) {
                break;
            }
            resultList.add(indexedValue);
        }
        return resultList.toArray(new String[resultList.size()]);
    }
    
    public int getIntegerProperty(String propertyName) {
        Integer result;
        String valueString = getProperty(propertyName);
        if (valueString!=null) {
            try {
                result = Integer.valueOf(valueString);
            }
            catch (NumberFormatException e) {
                throw new JacomaxConfigurationException("Default timeout " + valueString + " must be an integer");
            }
        }
        else {
            result = null;
        }
        return result!=null ? result.intValue() : 0;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + propertiesSourceDescription + "]";
    }
}
