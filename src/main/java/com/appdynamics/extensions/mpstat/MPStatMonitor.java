/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */
package com.appdynamics.extensions.mpstat;

import com.appdynamics.extensions.PathResolver;
import com.appdynamics.extensions.mpstat.common.MpStatMonitorException;
import com.appdynamics.extensions.mpstat.config.Configuration;
import com.appdynamics.extensions.mpstat.parser.LinuxParser;
import com.appdynamics.extensions.mpstat.parser.Parser;
import com.appdynamics.extensions.mpstat.parser.SolarisParser;
import com.appdynamics.extensions.yml.YmlReader;
import com.google.common.base.Strings;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * Created by balakrishnav on 20/3/15.
 */
public class MPStatMonitor extends AManagedMonitor {

    public static final String METRIC_SEPARATOR = "|";
    public static final String CONFIG_ARG = "config-file";
    private static Logger logger = LoggerFactory.getLogger(MPStatMonitor.class);
    private Parser parser;

    public MPStatMonitor() {
        System.out.println(logVersion());
    }

    public TaskOutput execute(Map<String, String> taskArgs, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
        if (taskArgs != null) {
            logger.info(logVersion());
            try {
                String configFilename = getConfigFilename(taskArgs.get(CONFIG_ARG));
                Configuration config = YmlReader.readFromFile(configFilename, Configuration.class);
                determineOS();
                Map<String, Map<String, String>> metrics = parser.executeMpStatAndPopulateMetrics();
                printMetrics(config.getMetricPrefix(), metrics);
                logger.info("mpstat monitoring task completed successfully");
                return new TaskOutput("mpstat monitoring task completed successfully");
            } catch (Exception e) {
                logger.error("Exception while running mpstat monitor task ", e);
            }
        }
        throw new TaskExecutionException("mpstat monitoring task completed with failures.");
    }

    private void determineOS() throws MpStatMonitorException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("linux")) {
            parser = new LinuxParser();
            logger.debug("OS System detected: Linux");
        } else if (os.contains("sunos")) {
            parser = new SolarisParser();
            logger.debug("OS System detected: Solaris");
        /*} else if (os.contains("aix")) {
            parser = new AIXParser();
            logger.debug("OS System detected: IBM AIX");
        } else if (os.contains("hp-ux")) {
            parser = new HPUXParser(config);
            logger.debug("OS System detected: HP-UX");*/
        } else {
            logger.error("Your OS (" + os + ") is not supported. Quitting Process Monitor");
            throw new MpStatMonitorException("Your OS (" + os + ") is not supported. Quitting MpStat Monitor");
        }

    }

    private void printMetrics(String metricPrefix, Map<String, Map<String, String>> metrics) {
        for (Map.Entry<String, Map<String, String>> processors : metrics.entrySet()) {
            String processorName = processors.getKey();
            Map<String, String> processorStats = processors.getValue();
            for (Map.Entry<String, String> stat : processorStats.entrySet()) {
                String metricName = stat.getKey();
                String value = stat.getValue();
                printMetric(metricPrefix + processorName + METRIC_SEPARATOR + metricName, value);
            }
        }
    }

    private void printMetric(String metricName, String value) {
        String metricValue = toWholeNumberString(metricName, value);
        if (!Strings.isNullOrEmpty(metricValue)) {
            MetricWriter metricWriter = getMetricWriter(metricName, MetricWriter.METRIC_AGGREGATION_TYPE_AVERAGE,
                    MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE, MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_INDIVIDUAL);
            try {
                metricWriter.printMetric(metricValue);
                if (logger.isDebugEnabled()) {
                    logger.debug(metricName + " = " + metricValue);
                }
            } catch (Exception e) {
                logger.error("Error while reporting metric {}:{} ", metricName, value, e);
            }
        }
    }

    private String toWholeNumberString(String metricName, String attribute) {
        try {
            Double f1 = Double.valueOf(attribute);
            return f1.doubleValue() > 0.0D && f1.doubleValue() < 1.0D ? "1" : String.valueOf(Math.round(f1.doubleValue()));
        } catch (NumberFormatException e) {
            logger.error("Invalid Metric:: {}:{} ", metricName, attribute, e);
        }
        return null;
    }

    private String getConfigFilename(String filename) {
        if (filename == null) {
            return "";
        }
        // for absolute paths
        if (new File(filename).exists()) {
            return filename;
        }
        // for relative paths
        File jarPath = PathResolver.resolveDirectory(AManagedMonitor.class);
        String configFileName = "";
        if (!Strings.isNullOrEmpty(filename)) {
            configFileName = jarPath + File.separator + filename;
        }
        return configFileName;
    }

    private String getImplementationVersion() {
        return MPStatMonitor.class.getPackage().getImplementationTitle();
    }

    private String logVersion() {
        String msg = String.format("Using Monitor Version [ %s ]", getImplementationVersion());
        return msg;
    }
}
