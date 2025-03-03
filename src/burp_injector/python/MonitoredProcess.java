package burp_injector.python;

/**
 * A process that is monitored by the process watchdog
 */
public class MonitoredProcess {
    private Process process;
    private long startTime = 0;
    private long pid = -1;

    public MonitoredProcess ( Process process ) {
        this.process = process;
        this.pid = process.pid();
        startTime = System.currentTimeMillis();
    }

    public Process getProcess() {
        return process;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getPid() {
        return pid;
    }
}
