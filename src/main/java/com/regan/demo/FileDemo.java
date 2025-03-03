package com.regan.demo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileDemo {

    public static void main(String[] args) throws IOException {
        setMib();
        //C:\SWSetup\SP99473
//        String dir = "C:\\SWSetup\\SP99473";
//        String nameFilter = "exe";
//        Collection<File> files = FileUtils.listFilesAndDirs(new File(dir), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
//        for (File file : files) {
//            if (file.getName().toLowerCase().contains(nameFilter.toLowerCase())) {
//                System.out.println(file.getAbsolutePath());
//            }
//        }

//        File file = new File("static/statkeys");
//        String content = FileUtils.readFileToString(file, "UTF-8");
//        JSONObject jsonObject = JSONObject.parseObject(content);
//        JSONArray jsonArray = jsonObject.getJSONArray("stat-key");
//        Object[] o = jsonArray.toArray();
//        List<String> list = new ArrayList<>();
//        for (int i=1;i<o.length;i++) {
//            JSONObject j = (JSONObject) o[i];
//            list.add(j.getString("key"));
//        }
//        list.sort((e1,e2) -> e1.compareTo(e2));
//        list.forEach(l -> System.out.println(l));

    }

    public static void setMib() {
        // Q-BRIDGE-MIB.mib
        String dir = "C:\\Users\\sheng\\Desktop\\徐汇区中心医院\\华为交换机\\FM-MIB_V200R022C00SPC500\\MIBs";
        String outputFile = dir + "\\AAA.mib"; // 输出文件
        String nameFilter = ".mib";
        Collection<File> files = FileUtils.listFilesAndDirs(new File(dir), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        int i = 0;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (File file : files) {
                if (file.getName().toLowerCase().contains(nameFilter.toLowerCase())) {
                    System.out.println(file.getAbsolutePath());
                    i++;
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            writer.write(line + System.lineSeparator()); // 添加换行符
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(i);
    }
}
