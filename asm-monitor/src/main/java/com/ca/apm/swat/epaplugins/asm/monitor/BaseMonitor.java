package com.ca.apm.swat.epaplugins.asm.monitor;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ca.apm.swat.epaplugins.asm.AsmReader;
import com.ca.apm.swat.epaplugins.asm.AsmRequestHelper;
import com.ca.apm.swat.epaplugins.utils.AsmProperties;
import com.ca.apm.swat.epaplugins.utils.AsmPropertiesImpl;
import com.wily.introscope.epagent.EpaUtils;

/**
 * Base class for implementations of the {@link Monitor} interface.
 * @author Guenter Grossberger - CA APM SWAT Team
 *
 */
public class BaseMonitor implements Monitor, AsmProperties {

    private String name = null;
    private String folder = null;
    private String[] tags = null;
    private String type = null;

    protected Handler successor = null;

    /**
     * Monitor base class.
     * @param name name of the monitor
     * @param folder folder of the monitor
     * @param tags tags of the monitor
     */
    protected BaseMonitor(String name, String type, String folder, String[] tags) {
        this.name = name;
        this.folder = folder;
        this.tags = tags;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getFolder() {
        return folder;
    }

    public String[] getTags() {
        return tags;
    }

    public String getType() {
        return type;
    }

    /**
     * Compare the name of the monitor with a string.
     * Needed to include/exclude monitors by name.
     * @param anotherName string to compare monitor name with
     * @return true if the monitor name equals anotherName
     */
    public boolean equals(String anotherName) {
        EpaUtils.getFeedback().debug("equals(String s) called for Monitor " + name
            + " with s = anotherName"); 
        return this.name.equals(anotherName);
    }

    /**
     * Compare the names of the monitors.
     * @param anotherMonitor monitor to compare with
     * @return true if the monitor names are equal
     */
    public boolean equals(Monitor anotherMonitor) {
        return this.name.equals(anotherMonitor.getName());
    }


    /**
     * Recursively generate metrics from API call result. 
     * @param jsonString API call result.
     * @param metricTree metric tree prefix
     * @return metricMap map containing the metrics
     */
    public HashMap<String, String> generateMetrics(
        String jsonString,
        String metricTree) {

        HashMap<String, String> metricMap = new HashMap<String, String>();

        JSONObject jsonObject = new JSONObject(jsonString);

        // if we find a name append it to metric tree
        if (jsonObject.optString(NAME_TAG, null) != null) {
            metricTree = metricTree + METRIC_PATH_SEPARATOR + jsonObject.getString(NAME_TAG);
        }

        // append monitoring station to metric tree
        if (TRUE.equals(AsmReader.getProperties().getProperty(DISPLAY_STATIONS, TRUE))) {
            if (jsonObject.optString(LOCATION_TAG, null) != null) {
                metricTree = metricTree + METRIC_PATH_SEPARATOR
                        + AsmRequestHelper.getMonitoringStationMap().get(
                            jsonObject.getString(LOCATION_TAG));
            }
        }

        // iterate over JSON object
        Iterator jsonObjectKeys = jsonObject.keys();
        while (jsonObjectKeys.hasNext()) {
            String thisKey = jsonObjectKeys.next().toString();

            // if this is another object do recursion
            if (jsonObject.optJSONObject(thisKey) != null) {
                JSONObject innerJsonObject = jsonObject.getJSONObject(thisKey);
                metricMap.putAll(generateMetrics(innerJsonObject.toString(), metricTree));
            } else if (jsonObject.optJSONArray(thisKey) != null) {
                // iterate over array
                JSONArray innerJsonArray = jsonObject.optJSONArray(thisKey);
                for (int i = 0; i < innerJsonArray.length(); i++) {
                    // recursively generate metrics for these tags
                    if ((thisKey.equals(RESULT_TAG))
                            || (thisKey.equals(MONITORS_TAG))
                            || (thisKey.equals(STATS_TAG))) {
                        metricMap.putAll(generateMetrics(
                            innerJsonArray.getJSONObject(i).toString(), metricTree));
                    } else {
                        metricMap.putAll(generateMetrics(
                            innerJsonArray.getJSONObject(i).toString(),
                            metricTree + METRIC_PATH_SEPARATOR + thisKey));
                    }
                }
            } else {
                // ignore these tags
                if ((thisKey.equals(CODE_TAG)) || (thisKey.equals(ELAPSED_TAG))
                        || (thisKey.equals(INFO_TAG)) || (thisKey.equals(VERSION_TAG))
                        || (jsonObject.optString(thisKey, EMPTY_STRING).length() == 0)) {
                    continue;
                }
                String thisValue = jsonObject.getString(thisKey);

                // store description as error
                if (thisKey.equals(DESCR_TAG)) {
                    String rawErrorMetric = metricTree + METRIC_NAME_SEPARATOR
                            + (String) AsmPropertiesImpl.ASM_METRICS.get(ERRORS_TAG);
                    metricMap.put(EpaUtils.fixMetric(rawErrorMetric), ONE);
                }

                // convert color to status value
                if (thisKey.equals(COLOR_TAG)) {
                    String rawErrorMetric = metricTree + METRIC_NAME_SEPARATOR
                            + (String) AsmPropertiesImpl.ASM_METRICS.get(COLORS_TAG);
                    if (AsmPropertiesImpl.ASM_COLORS.containsKey(thisValue)) {
                        metricMap.put(
                            EpaUtils.fixMetric(rawErrorMetric),
                            (String) AsmPropertiesImpl.ASM_COLORS.get(thisValue));
                    } else {
                        metricMap.put(EpaUtils.fixMetric(rawErrorMetric), ZERO);
                    }

                }

                // map location
                if (thisKey.equals(LOCATION_TAG)) {
                    thisValue = AsmRequestHelper.getMonitoringStationMap().get(thisValue);
                }

                // map metric key
                if (AsmPropertiesImpl.ASM_METRICS.containsKey(thisKey)) {
                    thisKey = ((String) AsmPropertiesImpl.ASM_METRICS.get(thisKey)).toString();
                }

                if (thisKey.equalsIgnoreCase(OUTPUT_TAG)) {

                    //Handled different
                    continue;
                }

                // put metric into map
                String rawMetric = metricTree + METRIC_NAME_SEPARATOR + thisKey;
                metricMap.put(EpaUtils.fixMetric(rawMetric),
                    EpaUtils.fixMetric(thisValue));
            }
        }

        if (EpaUtils.getFeedback().isVerboseEnabled()) {
            EpaUtils.getFeedback().verbose("BaseMonitor returning " + metricMap.size()
                + " metrics for monitor " + getName() + " in metric tree " + metricTree);
        }
        
        return metricMap;
    }

    public void setSuccessor(Handler successor) {
        this.successor = successor;
    }
    
    protected Handler getSuccessor() {
        return this.successor;
    }
}