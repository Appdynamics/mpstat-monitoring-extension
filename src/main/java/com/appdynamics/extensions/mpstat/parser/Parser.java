/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */
package com.appdynamics.extensions.mpstat.parser;

import com.appdynamics.extensions.mpstat.common.CommandExecutor;
import com.appdynamics.extensions.mpstat.common.MpStatMonitorException;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by balakrishnav on 18/8/15.
 */
public abstract class Parser {
    private static Logger logger = LoggerFactory.getLogger(Parser.class);

    public abstract Map<String, Map<String, String>> executeMpStatAndPopulateMetrics() throws MpStatMonitorException, IOException;

    protected List<String> executeCommandAndSplitResponseIntoLines(String command) throws MpStatMonitorException, IOException {
        String mpstatOutput = CommandExecutor.execute(command);
        List<String> lines = removeEmptyLinesAndSplit(mpstatOutput);
        return lines;
    }

    protected List<String> removeEmptyLinesAndSplit(String commandOutput) throws IOException {
        List<String> lines = IOUtils.readLines(new StringReader(commandOutput.replaceAll("[\n\r]+", "\n")));
        return lines;
    }

    protected Map<String, Map<String, String>> processMpStatData(List<String> lines, Map<String, Integer> headerPositionsInfo, int dataBeginLineNumber, int dataEndLineNumber, String processorNameHeaderString) {
        Map<String, Map<String, String>> mpStats = Maps.newHashMap();
        for (int i = dataBeginLineNumber; i < dataEndLineNumber; i++) {
            Map<String, String> processorMetrics = Maps.newHashMap();
            String processorName = null;
            String[] processorData = lines.get(i).trim().split("\\s+");
            boolean processorNameRecorded = false;
            for (Map.Entry<String, Integer> headerStringInfo : headerPositionsInfo.entrySet()) {
                if (!processorNameRecorded && headerStringInfo.getKey().equals(processorNameHeaderString)) {
                    processorName = processorData[headerStringInfo.getValue()];
                    processorNameRecorded = true;
                } else {
                    processorMetrics.put(headerStringInfo.getKey(), processorData[headerStringInfo.getValue()]);
                }
            }
            mpStats.put(processorName, processorMetrics);
        }
        return mpStats;
    }

    protected Map<String, Integer> processHeaderLine(String command, String headerLine, String[] headerStrings, String delimiter) throws MpStatMonitorException {
        Map<String, Integer> headerInfo = Maps.newHashMap();
        if (!Strings.isNullOrEmpty(headerLine)) {
            List<String> headersList = Arrays.asList(headerLine.trim().split(delimiter));
            for (String headerString : headerStrings) {
                if (headersList.contains(headerString)) {
                    headerInfo.put(headerString, headersList.indexOf(headerString));
                } /*else {
                    logger.warn("Could not find correct header information for " + headerString + " while executing command " + command);
                    //throw new MpStatMonitorException("Could not find correct header information for " + headerString + " while executing command " + command);
                }*/
            }
        } else {
            logger.error("Header of command {} output is null or empty", command);
            throw new MpStatMonitorException("Header of command output is null or empty");
        }
        return headerInfo;
    }
}
