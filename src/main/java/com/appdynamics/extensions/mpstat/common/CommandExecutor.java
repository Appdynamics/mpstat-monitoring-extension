/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */
package com.appdynamics.extensions.mpstat.common;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by balakrishnav on 18/8/15.
 */
public class CommandExecutor {
    private static Logger logger = Logger.getLogger(CommandExecutor.class);

    public static String execute(String command) throws MpStatMonitorException {
        Runtime rt = Runtime.getRuntime();
        Process p = null;
        BufferedReader input = null;
        try {
            logger.debug("Executing command " + command);
            p = rt.exec(command);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = input.readLine()) != null) {
                sb.append(line).append(System.getProperty("line.separator"));
            }
            return sb.toString();
        } catch (IOException e) {
            logger.error(e);
            throw new MpStatMonitorException(e);
        } finally {
            closeBufferedReader(input);
            cleanUpProcess(p, command);
        }
    }

    protected static void cleanUpProcess(Process p, String cmd) {
        try {
            if (p != null) {
                int exitValue = p.waitFor();
                if (exitValue != 0) {
                    logger.warn("Unable to terminate the command " + cmd + " normally. ExitValue = " + exitValue);
                }
                p.destroy();
            }
        } catch (InterruptedException e) {
            logger.error("Execution of command " + cmd + " got interrupted ", e);
        }
    }

    protected static void closeBufferedReader(BufferedReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                logger.error("Exception while closing the reader: ", e);
            }
        }
    }
}
