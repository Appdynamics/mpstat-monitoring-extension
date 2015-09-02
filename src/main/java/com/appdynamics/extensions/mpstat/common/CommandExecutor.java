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
