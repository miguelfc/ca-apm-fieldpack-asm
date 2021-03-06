
package com.ca.apm.swat.epaplugins.asm.monitor;

import com.ca.apm.swat.epaplugins.asm.error.AsmException;
import com.ca.apm.swat.epaplugins.utils.AsmMessages;
import com.wily.introscope.epagent.EpaUtils;
import com.wily.util.feedback.Module;
import java.util.Map;

public class XmlFixer implements Handler {

    protected Handler successor = null;  

    public void setSuccessor(Handler successor) {
        this.successor = successor;
    }

    /**
     * Generate metrics from API call result.
     * XmlFixer replaces all '&' characters in xml string with "&amp;"
     * 
     * @param map map to insert metrics into
     * @param xmlString xml string
     * @param metricTree metric tree prefix
     * @return metricMap map containing the metrics
     * @throws AsmException error during metrics generation
     */
    public Map<String, String> generateMetrics(Map<String, String> map,
                                               String xmlString,
                                               String metricTree) throws AsmException {
        Module module = new Module(Thread.currentThread().getName());

        // doesn't make sense if nobody handles the result
        if (null != successor) {
            if (EpaUtils.getFeedback().isVerboseEnabled(module)) {
                EpaUtils.getFeedback().verbose(module, AsmMessages.getMessage(
                    AsmMessages.METHOD_FOR_FOLDER_306,
                    this.getClass().getSimpleName(),
                    metricTree));
            }
            // replace all occu
            return successor.generateMetrics(map, xmlString.replaceAll("&", "&amp;"), metricTree);

        } else {
            EpaUtils.getFeedback().error(module, AsmMessages.getMessage(
                AsmMessages.INVALID_HANDLER_CHAIN_910,
                this.getClass().getSimpleName()));
        }
        return map;
    }
}
