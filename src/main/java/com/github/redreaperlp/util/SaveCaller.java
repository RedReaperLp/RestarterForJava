package com.github.redreaperlp.util;

import com.github.redreaperlp.Main;

public class SaveCaller implements Runnable{
    @Override
    public void run() {
        Main.process.destroy();
        System.out.println("Process Destroyed");
    }
}
