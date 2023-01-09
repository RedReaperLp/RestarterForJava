package com.github.redreaperlp.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Config {

    private List<String> config = new ArrayList<String>();
    private File file = new File("programConfig.yaml");

    public Config() {
        if (!file.exists()) {
            try {
                file.createNewFile();

                config.add("//Here you can Specify the Program to check for");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                config.add(scanner.nextLine());
            }
            this.contains("program", "<program name here>");
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setConfig(String pos, String value) {
        if (this.contains(pos)) {
            for (int i = 0; i < config.size(); i++) {
                if (config.get(i).startsWith(pos)) {
                    if (value == null) {
                        config.remove(i);
                    } else {
                        config.set(i, pos + "[" + value + "]");
                    }
                }
            }
        } else {
            config.add(pos + "[" + value + "]");
        }
    }

    public void saveConfig() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String s : config) {
                writer.write(s);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean contains(String pos) {
        for (String s : config) {
            if (s.startsWith(pos + "[")) {
                return true;
            }
        }
        return false;
    }

    public void contains(String pos, String value) {
        if (!this.contains(pos)) {
            this.setConfig(pos, value);
        }
    }

    public String getConfig(String pos) {
        for (String s : config) {
            if (s.startsWith(pos + "[")) {
                return s.substring(pos.length() + 1, s.length() - 1);
            }
        }
        return null;
    }
}
