package com.ca.apm.swat.epaplugins.asm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.ca.apm.swat.epaplugins.asm.rules.Rule;
import com.ca.apm.swat.epaplugins.asm.rules.RuleFactory;

/**
 * Test class for testing the acct_credits API.
 * 
 * @author Guenter Grossberger - CA APM SWAT Team
 *
 */
public class StatsTest extends FileTest {

    @Override
    public void setup() {
        super.setup();

        // we need to load the the checkpoint map
        try {
            requestHelper.getCheckpoints();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("error getting checkpoints: " + e.getMessage());
        }
    }


    /**
     * Test getLog() for a real browser monitor (RBM).
     */
//        @Test
    public void getStatsFirefox() {

        try {
            // set properties
            AsmReader.getProperties().setProperty(METRICS_LOGS, TRUE);

            String folder = "Caterpillar";
            Rule rule = RuleFactory.getRule("SFDC transaction", RBM_RULE, folder,
                EMPTY_STRING_ARRAY);
            int numRules = 5;
            String metricPrefix = MONITOR_METRIC_PREFIX + folder;

            // load file
            accessor.loadFile(STATS_CMD, "target/test-classes/rule_stats_firefox.json");

            // call API
            HashMap<String, String> metricMap =
                    requestHelper.getLogs(folder, rule, numRules, metricPrefix);

            // metricMap should contain those entries
            String[] expectedMetrics = {
                                        "Monitors|Caterpillar:Agent GMT Offset"
            };

            TreeSet<String> sortedSet = new TreeSet<String>(metricMap.keySet());
            for (Iterator<String> it = sortedSet.iterator(); it.hasNext(); ) {
                String key = it.next();
                System.out.println(key + " = " + metricMap.get(key));
            }

            // check
            checkMetrics(expectedMetrics, metricMap);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}