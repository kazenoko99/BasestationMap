package com.wenruisong.basestationmap.common;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

/**
 * Created by wen on 2016/5/27.
 */
public class SearchCsv {


    public static void getCsvFile(final List<CsvFile> list, File file) {// 获得视频文件

        file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                // sdCard找到视频名称
                String name = file.getName();

                int i = name.indexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".csv")){
                        CsvFile vi = new CsvFile();
                        vi.csvName=(file.getName());
                        vi.csvPath=(file.getAbsolutePath());
                        list.add(vi);
                        return true;
                    }
                } else if (file.isDirectory()) {
                    getCsvFile(list, file);
                }
                return false;
            }
        });
    }
}
