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

import com.appdynamics.extensions.mpstat.common.Commands;
import com.appdynamics.extensions.mpstat.common.MpStatMonitorException;
import com.google.common.base.Strings;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by balakrishnav on 18/8/15.
 */
public class LinuxParser extends Parser {
    private static Logger logger = Logger.getLogger(LinuxParser.class);

    public Map<String, Map<String, String>> executeMpStatAndPopulateMetrics() throws MpStatMonitorException, IOException {
        String command = Commands.MPSTAT_LINUX;
        List<String> lines = executeCommandAndSplitResponseIntoLines(command);

        String[] headerStrings = {"CPU", "%usr", "%nice", "%sys", "%iowait", "%irq", "%soft", "%steal", "%guest", "%gnice", "%idle"};

        Map<String, Integer> headerPositionsInfo = processHeaderLine(command, lines.get(1), headerStrings, "\\s+");
        Map<String, Map<String, String>> mpStats = processMpStatData(lines, headerPositionsInfo, 2, lines.size(), "CPU");
        return mpStats;
    }

    private String multiplyWithFactor(String value) {
        if (!Strings.isNullOrEmpty(value)) {
            try {
                return new BigDecimal(value).scaleByPowerOfTen(2).toPlainString();
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return "";
    }
}
