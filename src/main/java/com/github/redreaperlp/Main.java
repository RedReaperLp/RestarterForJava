package com.github.redreaperlp;

import com.github.redreaperlp.util.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class Main {
    static String oldHash = "";
    static Config conf = new Config();
    static String program;

    static String RED = "\u001B[31m";
    static String RESET = "\u001B[0m";
    public static void main(String[] args) throws InterruptedException, IOException {
        conf.saveConfig();
        program = conf.getConfig("program");
        System.out.println("Starting " + program);
        oldHash = digest();
        try {
            ProcessBuilder builder = new ProcessBuilder("java", "-jar", program);
            builder.inheritIO();
            Process p = builder.start();
            while (true) {
                TimeUnit.SECONDS.sleep(2);
                String newHash = digest();
                if (!oldHash.equals(newHash)) {
                    System.out.println(RED + "\n\n\n" + program + " has been updated, restarting...\n\n\n" + RESET);
                    oldHash = newHash;
                    p.destroy();
                    p = builder.start();
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
}