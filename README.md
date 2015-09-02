# Network Monitoring Extension  

##Use Case

Use for monitoring performance statistics for all logical processors in the system.

It uses the mpstat command to retrieve the metrics and is currently supported for Linux and Solaris machines.
This extension only works with standalone machine agent and requires it to be deployed on the machine you want to monitor.

**Note : By default, the Machine agent and AppServer agent can only send a fixed number of metrics to the controller. To change this limit, please follow the instructions mentioned [here](http://docs.appdynamics.com/display/PRO14S/Metrics+Limits).**

##Installation
1. To build from source, clone this repository and run 'mvn clean install'. This will produce a MPStatMonitor-VERSION.zip in the target directory. Alternatively, download the latest release archive from [Github](https://github.com/Appdynamics/mpstat-monitoring-extension/releases).
2. Copy and unzip MPStatMonitor.zip from 'target' directory into `<machine_agent_dir>/monitors/`
3. Restart the Machine Agent.

##Metrics
Metric path is typically: **Application Infrastructure Performance|\<Tier\>|Custom Metrics|MPStat|** followed by the individual metrics. The metric path can be changed by specifying in config.yml

###Linux

| Metric | Description |
| ----- | ----- |
| \<CPU\>&#124;%usr | CPU utilization that occurred while executing at the user level (application). |
| \<CPU\>&#124;%nice | CPU utilization that occurred while  executing at the user level with nice priority. |
| \<CPU\>&#124;%sys | CPU utilization that occurred while executing at the system level (kernel) |
| \<CPU\>&#124;%iowait | Time that the CPU or CPUs were idle during which the system had an outstanding disk I/O request. |
| \<CPU\>&#124;%irq | Time spent by the CPU or CPUs to  service interrupts. |
| \<CPU\>&#124;%soft | Time spent by the CPU or CPUs to service softirqs. |
| \<CPU\>&#124;%idle | Time that the CPU or CPUs were  idle  and the system did not have an outstanding disk I/O request. |

###Solaris

| Metric | Description |
| ----- | ----- |
| \<CPU\>&#124;minf | minor faults |
| \<CPU\>&#124;mjf | major faults. |
| \<CPU\>&#124;xcal | inter-processor cross-calls |
| \<CPU\>&#124;intr | interrupts. |
| \<CPU\>&#124;ithr | interrupts as threads (not counting clock interrupt). |
| \<CPU\>&#124;csw | context switches. |
| \<CPU\>&#124;icsw | involuntary context switches. |
| \<CPU\>&#124;migr | thread migrations (to another processor) |
| \<CPU\>&#124;smtx | spins on mutexes (lock not acquired on first try). |
| \<CPU\>&#124;srw | spins on readers/writer locks (lock not acquired on first try) |
| \<CPU\>&#124;syscl | system calls. |
| \<CPU\>&#124;usr | percent user time. |
| \<CPU\>&#124;sys | percent system time |
| \<CPU\>&#124;wt | the I/O wait time is no longer calculated as a percentage of CPU time, and this statistic will always return zero. |
| \<CPU\>&#124;idl | percent idle time. |

##Platform Tested

| Platform | Version |
| ----- | ----- |
| Ubuntu | 14.04 LTS |
| SunOS | 5.10 |

##Agent Compatibility

| Version |
| ----- |
| 4.1.2+ |

##Contributing

Always feel free to fork and contribute any changes directly here on GitHub

##Community

Find out more in the [AppSphere](http://www.appdynamics.com/community/exchange/extension/mpstat-monitoring-extension/) community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:help@appdynamics.com).

