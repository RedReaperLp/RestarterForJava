package com.github.redreaperlp.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProgramOutput implements Runnable{
    Process process;
    Thread thread;

    public ProgramOutput(Process process, Thread thread) {
        this.process = process;
        if (thread != null) {
            thread.interrupt();
        }
        thread = new Thread(this);
        thread.start();
    }
    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while (true) {
            try {
                if ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Thread thread() {
        return thread;
    }
}
