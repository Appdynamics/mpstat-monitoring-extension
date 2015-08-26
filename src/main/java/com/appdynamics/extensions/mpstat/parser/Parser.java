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

import com.appdynamics.extensions.mpstat.common.MpStatMonitorException;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by balakrishnav on 18/8/15.
 */
public abstract class Parser {
    private static Logger logger = Logger.getLogger(Parser.class);

    public abstract Map<String, Map<String, String>> executeMpStatAndPopulateMetrics() throws MpStatMonitorException, IOException;

    protected List<String> removeEmptyLinesAndSplit(String commandOutput) throws IOException {
        List<String> lines = IOUtils.readLines(new StringReader(commandOutput.replaceAll("[\n\r]+", "\n")));
        return lines;
    }

    protected Map<String, Integer> processHeaderLine(String command, String headerLine, String [] headerStrings, String delimiter) throws MpStatMonitorException {
        List<String> headersList = Arrays.asList(headerLine.trim().split(delimiter));
        Map<String, Integer> headerInfo = Maps.newHashMap();
        for(String headerString : headerStrings) {
            if(headersList.contains(headerString)) {
                headerInfo.put(headerString, headersList.indexOf(headerString));
            } else {
                logger.error("Could not find correct header information for " + headerString + " while executing command " + command);
                throw new MpStatMonitorException("Could not find correct header information for " + headerString + " while executing command " + command);
            }
        }
        return headerInfo;
    }
}
