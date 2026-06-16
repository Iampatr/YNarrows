package ru.link.YNarrows.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ExeCommands {

    private boolean bRunning;
    private boolean bSynchronous;
    private BufferedReader errorResult;
    public ReadWriteLock lock;
    private DataOutputStream os;
    private Process process;
    private StringBuffer result;
    private BufferedReader successResult;

    public ExeCommands(boolean synchronous) {
        this.bRunning = false;
        this.lock = new ReentrantReadWriteLock();
        this.result = new StringBuffer();
        this.bSynchronous = synchronous;
    }

    public String getResult() {
        Lock readLock = this.lock.readLock();
        readLock.lock();
        try {
            Log.i("auto", "getResult");
            return new String(this.result);
        } finally {
            readLock.unlock();
        }
    }

    public boolean isRunning() {
        return this.bRunning;
    }

    public ExeCommands run(String command, final int maxTime) {
        Log.i("auto", "run command:" + command + ",maxtime:" + maxTime);
        if (command != null && !command.isEmpty()) {
            try {
                this.process = Runtime.getRuntime().exec("sh");
                this.bRunning = true;
                this.successResult = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
                this.errorResult = new BufferedReader(new InputStreamReader(this.process.getErrorStream()));
                DataOutputStream dataOutputStream = new DataOutputStream(this.process.getOutputStream());
                this.os = dataOutputStream;
                try {
                    dataOutputStream.write(command.getBytes());
                    this.os.writeBytes("\n");
                    this.os.flush();
                    this.os.writeBytes("exit\n");
                    this.os.flush();
                    this.os.close();
                    if (maxTime > 0) {
                        new Thread(new Runnable() { // from class: com.geely.dockbar.common.utils.ExeCommands.1
                            @Override // java.lang.Runnable
                            public void run() {
                                try {
                                    Thread.sleep(maxTime);
                                } catch (Exception unused) {
                                }
                                try {
                                    Log.i("auto", "exitValue Stream over" + ExeCommands.this.process.exitValue());
                                } catch (IllegalThreadStateException unused2) {
                                    Log.i("auto", "take maxTime,forced to destroy process");
                                    ExeCommands.this.process.destroy();
                                }
                            }
                        }).start();
                    }
                    final Thread thread = new Thread(new Runnable() { // from class: com.geely.dockbar.common.utils.ExeCommands.2
                        @Override // java.lang.Runnable
                        public void run() {
                            Lock writeLock = ExeCommands.this.lock.writeLock();
                            while (true) {
                                try {
                                    try {
                                        String readLine = ExeCommands.this.successResult.readLine();
                                        if (readLine == null) {
                                            try {
                                                ExeCommands.this.successResult.close();
                                                Log.i("auto", "read InputStream over");
                                                return;
                                            } catch (Exception e) {
                                                Log.i("auto", "close InputStream exception:" + e.toString());
                                                return;
                                            }
                                        }
                                        writeLock.lock();
                                        ExeCommands.this.result.append(readLine + "\n");
                                        writeLock.unlock();
                                    } catch (Exception e2) {
                                        Log.i("auto", "read InputStream exception:" + e2.toString());
                                        try {
                                            ExeCommands.this.successResult.close();
                                            Log.i("auto", "read InputStream over");
                                            return;
                                        } catch (Exception e3) {
                                            Log.i("auto", "close InputStream exception:" + e3.toString());
                                            return;
                                        }
                                    }
                                } catch (Throwable th) {
                                    try {
                                        ExeCommands.this.successResult.close();
                                        Log.i("auto", "read InputStream over");
                                    } catch (Exception e4) {
                                        Log.i("auto", "close InputStream exception:" + e4.toString());
                                    }
                                    throw th;
                                }
                            }
                        }
                    });
                    thread.start();
                    final Thread thread2 = new Thread(new Runnable() { // from class: com.geely.dockbar.common.utils.ExeCommands.3
                        @Override // java.lang.Runnable
                        public void run() {
                            Lock writeLock = ExeCommands.this.lock.writeLock();
                            while (true) {
                                try {
                                    try {
                                        String readLine = ExeCommands.this.errorResult.readLine();
                                        if (readLine == null) {
                                            try {
                                                ExeCommands.this.errorResult.close();
                                                Log.i("auto", "read ErrorStream over");
                                                return;
                                            } catch (Exception e) {
                                                Log.i("auto", "read ErrorStream exception:" + e.toString());
                                                return;
                                            }
                                        }
                                        writeLock.lock();
                                        ExeCommands.this.result.append(readLine + "\n");
                                        writeLock.unlock();
                                    } catch (Exception e2) {
                                        Log.i("auto", "read ErrorStream exception:" + e2.toString());
                                        try {
                                            ExeCommands.this.errorResult.close();
                                            Log.i("auto", "read ErrorStream over");
                                            return;
                                        } catch (Exception e3) {
                                            Log.i("auto", "read ErrorStream exception:" + e3.toString());
                                            return;
                                        }
                                    }
                                } catch (Throwable th) {
                                    try {
                                        ExeCommands.this.errorResult.close();
                                        Log.i("auto", "read ErrorStream over");
                                    } catch (Exception e4) {
                                        Log.i("auto", "read ErrorStream exception:" + e4.toString());
                                    }
                                    throw th;
                                }
                            }
                        }
                    });
                    thread2.start();
                    Thread thread3 = new Thread(new Runnable() { // from class: com.geely.dockbar.common.utils.ExeCommands.4
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                thread.join();
                                thread2.join();
                                ExeCommands.this.process.waitFor();
                            } catch (Exception unused) {
                            } finally {
                                ExeCommands.this.bRunning = false;
                                Log.i("auto", "run command process end");
                            }
                        }
                    });
                    thread3.start();
                    if (this.bSynchronous) {
                        Log.i("auto", "run is go to end");
                        thread3.join();
                        Log.i("auto", "run is end");
                    }
                } catch (Exception e) {
                    Log.i("auto", "run command process exception:" + e.toString());
                }
            } catch (Exception unused) {
            }
        }
        return this;
    }

    public ExeCommands() {
        this.bRunning = false;
        this.lock = new ReentrantReadWriteLock();
        this.result = new StringBuffer();
        this.bSynchronous = true;
    }
}