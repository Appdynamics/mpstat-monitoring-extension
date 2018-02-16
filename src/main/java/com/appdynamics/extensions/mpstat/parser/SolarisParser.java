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
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by balakrishnav on 19/8/15.
 */
public class SolarisParser extends Parser {
    private static Logger logger = Logger.getLogger(SolarisParser.class);

    @Override
    public Map<String, Map<String, String>> executeMpStatAndPopulateMetrics() throws MpStatMonitorException, IOException {
        String command = Commands.MPSTAT_SOLARIS;
        List<String> lines = executeCommandAndSplitResponseIntoLines(command);

        String[] headerStrings = {"CPU", "minf", "mjf", "xcal", "intr", "ithr", "csw", "icsw", "migr", "smtx", "srw", "syscl", "usr", "sys", "wt", "idl"};

        Map<String, Integer> headerPositionsInfo = processHeaderLine(command, lines.get(0), headerStrings, "\\s+");

        Map<String, Map<String, String>> mpStats = processMpStatData(lines, headerPositionsInfo, 1, lines.size(), "CPU");

        return mpStats;
    }
}
