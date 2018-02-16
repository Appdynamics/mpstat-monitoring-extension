/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
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
