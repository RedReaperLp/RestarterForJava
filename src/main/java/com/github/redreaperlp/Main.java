package com.github.redreaperlp;

import com.github.redreaperlp.util.Config;
import com.github.redreaperlp.util.ProgramOutput;
import com.github.redreaperlp.util.SaveCaller;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class Main implements Runnable {
    static String oldHash = "";
    static Config conf = new Config();
    static String program;

    public static Process process;

    static String RED = "\u001B[31m";
    static String RESET = "\u001B[0m";

    static BufferedWriter writer;

    public static void main(String[] args) throws InterruptedException, IOException {
        Runtime rt = Runtime.getRuntime();
        rt.addShutdownHook(new Thread(new SaveCaller()));
        new Thread(new Main()).start();

        conf.saveConfig();
        program = conf.getConfig("program");
        System.out.println("Starting " + program);
        oldHash = digest();

        ProcessBuilder builder = new ProcessBuilder("java", "-jar", program);
        process = builder.start();
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        ProgramOutput p = new ProgramOutput(process, null);
        try {
            while (true) {
                TimeUnit.SECONDS.sleep(2);
                String newHash = digest();
                if (!oldHash.equals(newHash)) {
                    System.out.println(RED + "\n\n\n" + program + " has been updated, restarting...\n\n\n" + RESET);
                    oldHash = newHash;
                    process.destroy();
                    process = builder.start();
                    p = new ProgramOutput(process, p.thread());
                    writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(RED + "Program was Specified Wrong, please check \"programConfig.yaml\" " + RESET);
            System.exit(0);
        } catch (IOException e) {
            System.out.println(RED + "Program was Specified Wrong, please check \"programConfig.yaml\" " + RESET);
            System.exit(0);
        }
    }

    public static String digest() {
        File file = new File(program);
        String path = file.getAbsolutePath();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream stream = new FileInputStream(file);
            byte[] bytesArray = new byte[1024];
            int bytesCount = 0;

            while ((bytesCount = stream.read(bytesArray)) > 0) {
                md.update(bytesArray, 0, bytesCount);
            }
            stream.close();

            byte[] bytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(RED + "Program was Specified Wrong, please check \"programConfig.yaml\"" + RESET);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line;
            try {
                if ((line = reader.readLine()) != null) {
                    if (line.equals("exit")) {
                        System.out.println("Exiting Restart Manager");
                        System.exit(0);
                    } else if (line.equals("restart")) {
                        System.out.println("Restarting " + program);
                        process.destroy();
                        process = new ProcessBuilder("java", "-jar", program).start();
                        new ProgramOutput(process, Thread.currentThread());
                        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                    } else {
                        writer.write(line);
                        writer.flush();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}