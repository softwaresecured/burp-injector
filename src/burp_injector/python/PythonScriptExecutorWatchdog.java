package burp_injector.python;

import burp_injector.util.Logger;

import java.io.IOException;
import java.util.HashMap;

/**
 * Any PythonScript that is executed will be monitored by this watchdog which will terminate the process
 * if it runs too long
 */
public final class PythonScriptExecutorWatchdog {
    private int MAX_RUNTIME_MSEC = 2000;
    private static PythonScriptExecutorWatchdog INSTANCE;

    private Thread pythonExecutorWatchdogThread = null;
    private PythonExecutorWatchdogWorker pythonExecutorWatchdogWorker = null;
    //private ArrayList<MonitoredProcess> monitoredProcesses = new ArrayList<MonitoredProcess>();
    private HashMap<Long,MonitoredProcess> monitoredProcessHashMap = new HashMap<>();

    public static PythonScriptExecutorWatchdog getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new PythonScriptExecutorWatchdog();
        }

        return INSTANCE;
    }
    public PythonScriptExecutorWatchdog() {

    }

    public void setMaxProcessExecutionTimeSec( int sec ) {
        MAX_RUNTIME_MSEC = sec*1000;
        Logger.log("DEBUG", String.format("Python watchdog timeout set to %d msec", MAX_RUNTIME_MSEC));
    }

    public int getMaxProcessExecutionTimeSec() {
        return MAX_RUNTIME_MSEC/1000;
    }

    public Process startProcess( ProcessBuilder pb ) throws IOException {
        Process process = pb.start();
        MonitoredProcess monitoredProcess = new MonitoredProcess(process);
        monitoredProcessHashMap.put(monitoredProcess.getPid(),monitoredProcess);
        Logger.log("DEBUG", String.format("Watchdog monitoring python process with PID %d", process.pid()));
        return process;
    }

    public void startWatchDog() {
        pythonExecutorWatchdogWorker = new PythonExecutorWatchdogWorker();
        pythonExecutorWatchdogThread = new Thread(pythonExecutorWatchdogWorker);
        pythonExecutorWatchdogThread.start();
    }

    public void stopWatchDog() {
        pythonExecutorWatchdogWorker.shutdown();
        try {
            pythonExecutorWatchdogThread.join();
            pythonExecutorWatchdogThread = null;
            pythonExecutorWatchdogWorker = null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private class PythonExecutorWatchdogWorker implements Runnable {
        private boolean shutdownRequested = false;
        public PythonExecutorWatchdogWorker() {

        }

        private void shutdown() {
            killAll();
            shutdownRequested = true;
        }
        @Override
        public void run() {
            while ( !shutdownRequested ) {
                try {
                    terminateLongRunningProcesses();
                    deadProcessReaper();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    ;
                }
            }
        }

        private void deadProcessReaper() {
            for (Long pid : monitoredProcessHashMap.keySet()) {
                if ( !monitoredProcessHashMap.get(pid).getProcess().isAlive() ) {
                    monitoredProcessHashMap.remove(pid);
                }
            }
        }

        private void killAll() {
            for (Long pid : monitoredProcessHashMap.keySet()) {
                if ( monitoredProcessHashMap.get(pid).getProcess().isAlive() ) {
                    killProcess(pid);
                }
            }
        }

        private void killProcess( long pid ) {
            monitoredProcessHashMap.get(pid).getProcess().destroyForcibly();
        }

        private void terminateLongRunningProcesses() {
            for (Long pid : monitoredProcessHashMap.keySet()) {
                if ( System.currentTimeMillis() - monitoredProcessHashMap.get(pid).getStartTime() > MAX_RUNTIME_MSEC ) {
                    Logger.log(
                            "INFO",
                            String.format(
                                    "Watchdog terminating PID %d because it exceeded the maximum execution time of %d msec",
                                    monitoredProcessHashMap.get(pid).getProcess().pid(),
                                    MAX_RUNTIME_MSEC
                            )
                    );
                    killProcess(pid);
                }
            }
        }
    }
}
