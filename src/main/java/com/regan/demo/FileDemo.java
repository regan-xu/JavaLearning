package com.regan.demo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class FileDemo {

    public static void main(String[] args) throws IOException {
        updatePic();
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

    public static void updatePic() {
        String dir = "D:\\data\\Desktop\\效果图";
        Collection<File> files = FileUtils.listFilesAndDirs(new File(dir), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        System.out.println(files.size());
        long id = 531155881056911301L;
        for (File file : files) {
           if (file.isFile()) {
//               String[] path = file.getParent().split("\\\\");
//               String pId = path[4].split(" ")[0];
//               String suffix = file.getName().substring(file.getName().lastIndexOf("."));
////               if (pId.endsWith("108401") || pId.endsWith("108440") || pId.endsWith("108449")) {
//
//               String newName = file.getParent() + "\\" + pId + suffix;
//               System.out.println(newName);
//               file.renameTo(new File(newName));
               String newName = "D:\\data\\Desktop\\produce\\" + file.getName();
               file.renameTo(new File(newName));
           }
//            String name = file.getName();
//            if (name.contains("效果图")) {
//                continue;
//            }
//            String[] names = name.split(" ");
//            String picUrl = "https://mobiletest.stdfood.com.cn:53001/oss/produce/" + names[0] + ".png";
//            String sql = "INSERT INTO `app_produce`(`id`, `tenant_id`, `produce_id`, `produce_name`, `spec`, `picture_url`, `sort`, `remark`, `create_user`, `create_time`, `update_user`, `update_time`, `is_deleted`) VALUES (" +
//                    id + ", 10000, '"+names[0]+"', '"+names[1]+"', NULL, '"+picUrl+"', NULL, NULL, 10000, '2025-08-05 17:00:00', NULL, NULL, 0);";
//            System.out.println(sql);
//            id ++;
        }
    }

    public static void setMib() {
        // Q-BRIDGE-MIB.mib
        // 指定包含MIB文件的目录
        String dir = "D:\\data\\Desktop\\general_mibs";
        // 文件名过滤器，这里设置为".mib"
        String nameFilter = ".mib";
        // 列出目录下的所有文件和子目录
        Collection<File> files = FileUtils.listFilesAndDirs(new File(dir), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        int i = 0; // 合并的文件数量
        int fileIndex = 1; // 输出文件的索引
        int totalLineCount = 0; // 当前输出文件的总行数
        BufferedWriter writer = null;
        try {
            for (File file : files) {
                // 检查文件名是否包含".mib"
                if (file.getName().toLowerCase().contains(nameFilter.toLowerCase())) {
                    System.out.println(file.getAbsolutePath());
                    i++;
                    int fileLineCount = 0; // 当前MIB文件的行数

                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        // 读取文件内容并计算行数
                        while ((line = reader.readLine()) != null) {
                            fileLineCount++;
                        }
                    }

                    // 如果当前文件的行数加上总行数超过10000，关闭当前文件并创建新文件
                    if (totalLineCount + fileLineCount > 10000) {
                        if (writer != null) {
                            writer.close();
                        }
                        String outputFile = dir + "\\AAA_" + fileIndex + ".mib"; // 输出文件
                        writer = new BufferedWriter(new FileWriter(outputFile));
                        fileIndex++;
                        totalLineCount = 0;
                    }

                    // 重新打开文件并写入内容
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (writer == null) {
                                String outputFile = dir + "\\AAA_" + fileIndex + ".mib"; // 输出文件
                                writer = new BufferedWriter(new FileWriter(outputFile));
                            }
                            writer.write(line + System.lineSeparator()); // 添加换行符
                            totalLineCount++;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(i);
    }
}
