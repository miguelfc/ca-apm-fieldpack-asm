package com.ca.apm.swat.epaplugins.asm.monitor;

import com.ca.apm.swat.epaplugins.asm.error.AsmException;
import com.ca.apm.swat.epaplugins.asm.reporting.MetricMap;

/**
 * Handler interface for generating metrics from a JSON string.
 * Implements the Chain of Responsibility design pattern.
 * 
 * @author Guenter Grossberger - CA APM SWAT Team
 *
 */
public interface Handler {
    
    /**
     * Add a Handler to the chain of responsibility.
     * @param successor next Handler in chain
     */
    public void setSuccessor(Handler successor);
    
    /**
     * Generate metrics from API call result. 
     * @param jsonString API call result.
     * @param metricTree metric tree prefix
     * @return map containing the metrics
     * @throws AsmException error during metrics generation
     */
    public MetricMap generateMetrics(String jsonString, String metricTree) throws AsmException;
}
