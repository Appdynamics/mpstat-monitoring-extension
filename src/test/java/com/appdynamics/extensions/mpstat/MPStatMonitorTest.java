package com.appdynamics.extensions.mpstat;

import com.google.common.collect.Maps;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.junit.Test;

import java.util.Map;

/**
 * Created by balakrishnav on 18/8/15.
 */
public class MPStatMonitorTest {
    public static final String CONFIG_ARG = "config-file";

    @Test
    public void testMPStatMonitor() throws TaskExecutionException {
        Map<String, String> taskArgs = Maps.newHashMap();
        taskArgs.put(CONFIG_ARG,"src/test/resources/conf/config.yml");

        MPStatMonitor monitor = new MPStatMonitor();
        monitor.execute(taskArgs, null);
    }
}
