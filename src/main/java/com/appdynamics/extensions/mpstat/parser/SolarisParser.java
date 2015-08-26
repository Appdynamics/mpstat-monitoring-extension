package com.appdynamics.extensions.mpstat.parser;

import com.appdynamics.extensions.mpstat.common.CommandExecutor;
import com.appdynamics.extensions.mpstat.common.Commands;
import com.appdynamics.extensions.mpstat.common.MpStatMonitorException;
import com.google.common.collect.Maps;
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
        Map<String, Map<String, String>> mpStats = Maps.newHashMap();
        String command = Commands.MPSTAT_SOLARIS;
        String mpstatOutput = CommandExecutor.execute(command);
        List<String> lines = removeEmptyLinesAndSplit(mpstatOutput);
        String [] headerStrings = {"CPU", "minf", "mjf", "xcal", "intr", "ithr", "csw", "icsw", "migr", "smtx", "srw", "syscl", "usr", "sys", "wt", "idl"};

        Map<String, Integer> headerPositionsInfo = processHeaderLine(command,lines.get(0), headerStrings, "\\s+");

        for(int i = 1; i < lines.size(); i++) {
            Map<String, String> processorMetrics = Maps.newHashMap();
            String processorName = null;
            String processorLine = lines.get(i);
            String [] columns = processorLine.trim().split("\\s+");
            for(Map.Entry<String, Integer> column : headerPositionsInfo.entrySet()) {
                if("CPU".equals(column.getKey())) {
                    processorName = columns[column.getValue()];
                } else {
                    String value = columns[column.getValue()];
                    if(!"".equals(value)) {
                        processorMetrics.put(column.getKey(), value);
                    }
                }
            }
            mpStats.put(processorName, processorMetrics);
        }
        return mpStats;
    }
}
