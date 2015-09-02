package com.ca.apm.swat.epaplugins.asm.reporting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ca.apm.swat.epaplugins.utils.AsmMessages;
import com.wily.introscope.epagent.EpaUtils;

/**
 * Overrides the HashMap implementation to make sure no values are overwritten.
 * 
 * @author Guenter Grossberger - CA APM SWAT Team
 *
 */
public class MetricMap extends HashMap<String, String> {

    private static final long serialVersionUID = 2388439240326112312L;

    /**
     * Associates the specified value with the specified key in this map.
     *   If the map previously contained a mapping for the key, the old value is NOT replaced.
     */
    @Override
    public String put(String metricPath, String value) {
        if (this.containsKey(metricPath)) {
            if (EpaUtils.getFeedback().isDebugEnabled()) {
                EpaUtils.getFeedback().debug(AsmMessages.getMessage(
                    AsmMessages.DUPLICATE_METRIC_200,
                    metricPath,
                    value,
                    this.get(metricPath)));
            }
            // don't insert
            return null;
        }
        return super.put(metricPath, value);
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     *   If the map previously contained a mapping for the key, the old value is NOT replaced.
     */
    @Override
    public void putAll(Map<? extends String, ? extends String> map) {
        for (Iterator<? extends String> it = map.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            if (!this.containsKey(key)) {
                super.put(key, map.get(key));
            } else {
                if (EpaUtils.getFeedback().isDebugEnabled()) {
                    EpaUtils.getFeedback().debug(AsmMessages.getMessage(
                        AsmMessages.DUPLICATE_METRIC_200,
                        key,
                        map.get(key),
                        this.get(key)));
                }
            }
        }
    }
}
