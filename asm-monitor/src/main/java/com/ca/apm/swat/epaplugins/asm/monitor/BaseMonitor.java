package com.ca.apm.swat.epaplugins.asm.monitor;

import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ca.apm.swat.epaplugins.asm.AsmRequestHelper;
import com.ca.apm.swat.epaplugins.asm.format.Formatter;
import com.ca.apm.swat.epaplugins.asm.reporting.MetricMap;
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
    private boolean active = false;

    protected Handler successor = null;

    protected static Formatter format = Formatter.getInstance();
    
    /**
     * Monitor base class.
     * @param name name of the monitor
     * @param folder folder of the monitor
     * @param tags tags of the monitor
     */
    protected BaseMonitor(String name, String type, String folder, String[] tags, boolean active) {
        this.name = name;
        this.folder = folder;
        this.tags = tags;
        this.type = type;
        this.active = active;
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

    public boolean isActive() {
        return active;
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
    @SuppressWarnings("rawtypes")
    public MetricMap generateMetrics(
        String jsonString,
        String metricTree) {

        MetricMap metricMap = new MetricMap();
        JSONObject jsonObject = new JSONObject(jsonString);
        String name = jsonObject.optString(NAME_TAG, null);
        Monitor monitor = null;
        String origMetricTree = null;
        
        if (EpaUtils.getFeedback().isDebugEnabled()) {
            EpaUtils.getFeedback().debug("generateMetrics(" + metricTree + ", " 
                + jsonString + ")");
        }

        // return if this monitor is inactive
        if (jsonObject.optString(ACTIVE_TAG, YES) == NO) {
            return metricMap;
        }
        
        // if we find a name append it to metric tree
        if (name != null) {
            metricTree = metricTree + METRIC_PATH_SEPARATOR + name;

            // find the monitor
            monitor = MonitorFactory.findMonitor(name);

            // return if not active
            if ((null != monitor) && (!monitor.isActive())) {
                if (EpaUtils.getFeedback().isVerboseEnabled()) {
                    EpaUtils.getFeedback().verbose("skipping metrics for inactive monitor "
                            + name);
                }
                return metricMap;
            }
        }

        // append monitoring station to metric tree
        if (jsonObject.optString(LOCATION_TAG, null) != null) {
            if (EpaUtils.getBooleanProperty(DISPLAY_STATIONS, true)) {
                String location = AsmRequestHelper.getMonitoringStationMap().get(
                    jsonObject.getString(LOCATION_TAG));
                if (null == location) {
                    location = OPMS + METRIC_PATH_SEPARATOR + OPMS + METRIC_PATH_SEPARATOR
                            + jsonObject.getString(LOCATION_TAG);
                }
                metricTree = metricTree + METRIC_PATH_SEPARATOR + location;
            }
        }

        // add a step node if STEP_FORMAT_ALWAYS is true
        if (EpaUtils.getBooleanProperty(STEP_FORMAT_ALWAYS, false)) {
            if (null != name) {  
                // add step node if not a script monitor
                if ((null != monitor) && (!SCRIPT_MONITOR.equals(monitor.getType()))) {
                    origMetricTree = metricTree;
                    metricTree = metricTree + METRIC_PATH_SEPARATOR
                            + format.formatStep(1, EMPTY_STRING);
                    if (EpaUtils.getFeedback().isVerboseEnabled()) {
                        EpaUtils.getFeedback().verbose("adding a step for monitor " + name
                            + " of type " + monitor.getType());
                    }
                }
            }
        }
        
        MetricMap outputMap = null;
        int result = 0;
        
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
                        || (thisKey.equals(ACTIVE_TAG))
                        || (jsonObject.optString(thisKey, EMPTY_STRING).length() == 0)
                        || format.ignoreTagForMonitor(thisKey)) {
                    continue;
                } else if (thisKey.equalsIgnoreCase(OUTPUT_TAG)) {
                    if (null != successor) {
                        try {
                            // let successors do the work
                            String thisValue = jsonObject.getString(thisKey);
                            if (null != thisValue) {
                                outputMap = successor.generateMetrics(thisValue, metricTree);
                            }
                        } catch (Exception e) {
                            //Don't throw. Some formats are not yet supported
                            EpaUtils.getFeedback().warn(e.getMessage() + "\n monitor " + getName()
                                + ", metric tree  =" + metricTree + "\njsonString = " + jsonString);
                        }
                    }
                    // anyway don't write metric
                    continue;
                }

                String thisValue = jsonObject.getString(thisKey);

                // store description as error
                if (thisKey.equals(DESCR_TAG)) {
                    String rawErrorMetric = metricTree + METRIC_NAME_SEPARATOR
                            + (String) AsmPropertiesImpl.ASM_METRICS.get(ERRORS_TAG);
                    metricMap.put(EpaUtils.fixMetric(rawErrorMetric), ONE);
                
                // convert color to status value
                } else if (thisKey.equals(COLOR_TAG)) {
                    String rawErrorMetric = metricTree + METRIC_NAME_SEPARATOR
                            + (String) AsmPropertiesImpl.ASM_METRICS.get(COLORS_TAG);
                    if (AsmPropertiesImpl.ASM_COLORS.containsKey(thisValue)) {
                        metricMap.put(
                            EpaUtils.fixMetric(rawErrorMetric),
                            (String) AsmPropertiesImpl.ASM_COLORS.get(thisValue));
                    } else {
                        metricMap.put(EpaUtils.fixMetric(rawErrorMetric), ZERO);
                    }

                // map location
                } else if (thisKey.equals(LOCATION_TAG)) {
                    // use mapped value if existing
                    String location = AsmRequestHelper.getMonitoringStationMap().get(thisValue);
                    if (null != location) {
                        thisValue = location;
                    }
                // map result code
                } else if (thisKey.equals(RESULT_TAG)) {
                    try {
                        result = Integer.parseInt(thisValue);
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                    metricMap.put(EpaUtils.fixMetric(metricTree + METRIC_NAME_SEPARATOR
                        + STATUS_MESSAGE_VALUE), 
                        format.mapResponseToStatusCode(thisValue));
                }

                // map metric key
                if (AsmPropertiesImpl.ASM_METRICS.containsKey(thisKey)) {
                    thisKey = AsmPropertiesImpl.ASM_METRICS.get(thisKey);
                }
             
                // put metric into map
                String rawMetric = metricTree + METRIC_NAME_SEPARATOR + thisKey;
                if ((null == rawMetric) || (null == thisValue)) {
                    EpaUtils.getFeedback().warn("null value in " + rawMetric + " = " + thisValue);
                } else {
                    metricMap.put(EpaUtils.fixMetric(rawMetric), thisValue);
                }
            }
        }

        // if monitor result is timeout set step status message value to timeout, too
        if (null != outputMap) {
            if (EpaUtils.getBooleanProperty(TIMEOUT_REPORT_ALWAYS, true) && isTimeout(result)) {
                for (Iterator<String> it = outputMap.keySet().iterator(); it.hasNext(); ) {
                    String key = it.next();
                    if (key.endsWith(STATUS_MESSAGE_VALUE)) {
                        metricMap.put(key,
                            format.mapResponseToStatusCode(Integer.toString(result)));
                    } else {
                        metricMap.put(key, outputMap.get(key));
                    }
                }
            } else {
                metricMap.putAll(outputMap);
            }
        }
        
        if (EpaUtils.getFeedback().isVerboseEnabled()) {
            EpaUtils.getFeedback().verbose("BaseMonitor returning " + metricMap.size()
                + " metrics for monitor " + getName() + " in metric tree " + metricTree);
        }

        // if we added a step node then copy all metrics up to monitor node
        if (EpaUtils.getBooleanProperty(STEP_FORMAT_ALWAYS, false)
                && (null != origMetricTree)
                && (!metricMap.isEmpty())) {
            // we need to copy to a new map because we cannot iterate over
            // and modify map at the same time
            outputMap = new MetricMap();
            
            Set<String> keySet = metricMap.keySet();
            for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
                String key = it.next();
                outputMap.put(key, metricMap.get(key));
                int index = key.lastIndexOf(METRIC_NAME_SEPARATOR);
                if (-1 < index) {
                    outputMap.put(origMetricTree + key.substring(index, key.length()),
                        metricMap.get(key));
                }
            }

            metricMap = outputMap;
        }
        
        return metricMap;
    }

    /**
     * Check if the supplied result code is a timeout. 
     * @param result the result code to check
     * @return true if it is a timeout, e.g. 7011 or 1042.
     */
    public boolean isTimeout(int result) {
        switch (result) {
          case RESULT_CONNECT_TIMEOUT:
          case RESULT_EXECUTION_TIMEOUT:
          case RESULT_PAGE_LOAD_TIMEOUT:
          case RESULT_OPERATION_TIMEOUT:
              return true;
          default:
              return false;
        }
    }

    public void setSuccessor(Handler successor) {
        this.successor = successor;
    }
    
    protected Handler getSuccessor() {
        return this.successor;
    }
}
