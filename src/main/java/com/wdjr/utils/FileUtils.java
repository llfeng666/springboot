package com.wdjr.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

/**
 * 23:25:18.398 [pool-1-thread-1] INFO  commTest.AskInfoHandler - 准考证号码: 16682512697
 * 23:25:20.890 [pool-1-thread-1] INFO  commTest.AskInfoHandler - 学号: 197864210009
 * 23:25:20.945 [pool-1-thread-1] INFO  commTest.AskInfoHandler - 姓名: 潘宏培
 * 23:25:20.989 [pool-1-thread-1] INFO  commTest.AskInfoHandler - 报名批次: 2020年9月统考
 * 23:25:21.048 [pool-1-thread-1] INFO  commTest.AskInfoHandler - 考试科目: 大学英语B
 * 23:25:21.114 [pool-1-thread-1] INFO  commTest.AskInfoHandler - 通过情况: 合格
 * 23:25:21.166 [pool-1-thread-1] INFO  commTest.AskInfoHandler - 得分: 91
 */
public class FileUtils {




    public static void writeTxtFile(String content, File fileName)
            throws Exception {

        FileWriter fwriter = fwriter = new FileWriter(fileName);

        fwriter.write(content); //这里要放入string类型

        fwriter.flush();

        fwriter.close();
    }



    public static void readTxtFile(String content, File fileName)
            throws Exception {

        try ( InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), "utf-8");
              BufferedReader br = new BufferedReader(isr);
              FileWriter fwriter = new FileWriter(fileName,true);
        ){
            if(fileName.isFile() && fileName.exists()) {
                String lineTxt = null;
                String lastLine = null;
                while(true){
                    lastLine = lineTxt;
                    if((lineTxt = br.readLine()) == null){
                        fwriter.write(content);
                        fwriter.flush();
                        break;
                    }
                }
            } else {
                System.out.println("文件不存在!");
            }

        } catch (Exception e) {

            System.out.println("文件读取错误!");

        }
    }




}
