
package com.ca.apm.swat.epaplugins.asm.monitor;

import com.ca.apm.swat.epaplugins.asm.error.AsmException;
import com.ca.apm.swat.epaplugins.asm.reporting.MetricMap;
import com.ca.apm.swat.epaplugins.utils.AsmMessages;
import com.wily.introscope.epagent.EpaUtils;
import com.wily.util.feedback.Module;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class AssetDownloader implements Handler {

    protected Handler successor = null;  

    public void setSuccessor(Handler successor) {
        this.successor = successor;
    }

    /**
     * Generate metrics from API call result.
     * AssetDownloader replaces a reference to an asset with it's actual content
     * 
     * @param string a string
     * @param metricTree metric tree prefix
     * @return metricMap map containing the metrics
     * @throws AsmException error during metrics generation
     */
    public MetricMap generateMetrics(String string, String metricTree) throws AsmException {
        Module module = new Module(Thread.currentThread().getName());

        // doesn't make sense if nobody handles the result
        if (null != successor) {
            
            BufferedReader in = null;
            
            try {
                in = new BufferedReader(new InputStreamReader(new URL(
                        new JSONObject(string).getJSONObject("jtl").getString("url")
                    ).openStream()));
                StringBuilder sb = new StringBuilder();
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                }
                string = sb.toString();

            } catch (JSONException ex) {
                // assume the string represents an asset, and pass it on unchanged
            } catch (Exception ex) {
                EpaUtils.getFeedback().warn(module, AsmMessages.getMessage(
                        AsmMessages.ASSET_DOWNLOAD_ERROR_714, 
                        this.getClass().getSimpleName(), 
                        ex.getMessage()
                    ));
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception ignored) { }
                }
            }

            return successor.generateMetrics(string, metricTree);

        } else {
            EpaUtils.getFeedback().error(module, AsmMessages.getMessage(
                    AsmMessages.INVALID_HANDLER_CHAIN_910, 
                    this.getClass().getSimpleName()
                ));
        }
        return new MetricMap();
    }
}