package com.wily.introscope.epagent;

import java.util.Properties;

import com.ca.apm.swat.epaplugins.utils.AsmProperties;
import com.wily.util.feedback.IModuleFeedbackChannel;
import com.wily.util.feedback.SeverityLevel;
import com.wily.util.feedback.SystemOutFeedbackChannel;


/**
 * EPAgent utilities.
 * 
 * @author Andreas Reiss - CA Wily Professional Service
 *
 */
public class EpaUtils {

    private static Properties properties;
    private static IModuleFeedbackChannel channel = null;
    private static boolean started = false;
    
    public static String PREFERENCES_KEY = EPAConfig.kPrefsKey2;
    
    /**
     * Cannot instantiate.
     */
    private EpaUtils() {
    }
    /**
     * Get the logging component.
     * @return the logging component
     */
    public static IModuleFeedbackChannel getFeedback() {
        if (null == channel) {
            if (EPAgent.GetInstance() == null) {
                channel = new SystemOutFeedbackChannel(
                    AsmProperties.ASM_PRODUCT_NAME_SHORT + " EPA",
                    SeverityLevel.INFO);
            } else {
                channel = EPAgent.GetFeedback();
            }
            if (!started && (null != getProperties())) {
                String version = getProperties().getProperty(AsmProperties.VERSION);
                channel.info("Started field pack " + AsmProperties.ASM_PRODUCT_NAME
                    + ", version " + version);
                started = true;
            }
        }

        return channel;
    }

    /**
     * Replace unsupported characters in metric name.
     * @param rawMetric raw metric name
     * @return cleansed metric name
     */
    public static String fixMetricName(String rawMetric) {
        /*
        StringFilter normalizer = null;
        try {
            normalizer = TextNormalizer.getNormalizationStringFilter();
        } catch (ClassNotFoundException e) {
            EpaUtils.getFeedback().error(e.getMessage());
            throw new InitializationError(
                AsmMessages.getMessage(AsmMessages.NORMALIZER_INFO, e.getMessage()));
        }

        String metricKeyNormalized = normalizer.filter(rawMetric);
        Pattern pattern = Pattern.compile(AsmProperties.JSON_PATTERN);
        String fixedMetric =
                pattern.matcher(metricKeyNormalized).replaceAll(AsmProperties.EMPTY_STRING);
*/
        if (null == rawMetric) {
            return null;
        }

        String fixedMetric = rawMetric;

        // replace all but the last occurence of ':'
        int lastColon = fixedMetric.lastIndexOf(':');
        if (lastColon > -1) {
            while (true) {
                int otherColon = fixedMetric.indexOf(':');
                if ((otherColon == lastColon) || (otherColon == -1)) {
                    break;
                }
                fixedMetric = fixedMetric.replaceFirst(":", "_");
            }
        }
        
        return fixedMetric.replace("\\", "-")
                //.replace("/", "-")
                .replace(",", "_")
                .replace(";", "-")
                .replace("&", "_");
    }

    /**
     * Replace unsupported characters in metric value.
     * @param rawMetric raw metric name
     * @return cleansed metric name
     */
    public static String fixMetricValue(String rawMetric) {
        if (null == rawMetric) {
            return null;
        }
        
        return rawMetric.replace("\\", "-")
                //.replace("/", "-")
                .replace(",", "_")
                .replace(";", "-")
                .replace("&", "_")
                .replace(":", "_")
                .replace("\"", "'");
    }

    /**
     * Get the global properties.
     * @return the properties
     */
    public static Properties getProperties() {
        return properties;
    }

    /**
     * Set the global properties.
     * @param properties the properties
     */
    public static void setProperties(Properties properties) {
        EpaUtils.properties = properties;
    }

    /**
     * Searches for the property with the specified key in the global property list.
     *   The method returns the default value argument if the property is not found.
     * @param key the property key (name)
     * @param defaultValue the default value
     * @return the value in this property list with the specified key value.
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Searches for the property with the specified key in the global property list.
     *   The method returns null if the property is not found.
     * @param key the property key (name)
     * @param defaultValue the default value
     * @return the value in this property list with the specified key value.
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Searches for the property with the specified key in the global property list.
     *   The method returns null if the property is not found.
     * @param key the property key (name)
     * @param defaultValue the default value
     * @return the value in this property list with the specified key value.
     */
    public static boolean getBooleanProperty(String key) {
        return Boolean.getBoolean(properties.getProperty(key));
    }

    /**
     * Searches for the property with the specified key in the global property list.
     *   The method returns the default value argument if the property is not found.
     * @param key the property key (name)
     * @param defaultValue the default value
     * @return the value in this property list with the specified key value.
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        if (properties.containsKey(key)) {
            return Boolean.valueOf(properties.getProperty(key));
        }
        return defaultValue;
    }

    /**
     * Returns the name of the encoding (e.g. UTF_8) to use.
     * @return name of the encoding (default: UTF_8)
     */
    public static String getEncoding() {
        return getProperty(AsmProperties.ENCODING, "UTF-8");
    }
}
