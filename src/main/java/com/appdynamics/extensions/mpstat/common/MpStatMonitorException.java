/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */
package com.appdynamics.extensions.mpstat.common;

/**
 * Created by balakrishnav on 18/8/15.
 */
public class MpStatMonitorException extends Exception {
    public MpStatMonitorException() {
    }

    public MpStatMonitorException(String message) {
        super(message);
    }

    public MpStatMonitorException(String message, Throwable cause) {
        super(message, cause);
    }

    public MpStatMonitorException(Throwable cause) {
        super(cause);
    }
}
