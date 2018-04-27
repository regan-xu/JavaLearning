package com.regan.demo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileDemo {

    public static void main(String[] args) throws IOException {

        File file = new File("static/statkeys");
        String content = FileUtils.readFileToString(file, "UTF-8");
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONArray jsonArray = jsonObject.getJSONArray("stat-key");
        Object[] o = jsonArray.toArray();
        List<String> list = new ArrayList<>();
        for (int i=1;i<o.length;i++) {
            JSONObject j = (JSONObject) o[i];
            list.add(j.getString("key"));
        }
        list.sort((e1,e2) -> e1.compareTo(e2));
        list.forEach(l -> System.out.println(l));


    }
}
