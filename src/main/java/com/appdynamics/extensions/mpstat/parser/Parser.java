/**
 * Copyright 2015 AppDynamics
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
