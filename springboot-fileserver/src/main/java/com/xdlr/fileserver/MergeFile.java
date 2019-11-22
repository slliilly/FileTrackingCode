package com.xdlr.fileserver;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MergeFile {
    public boolean merge(List<String> fileName) {
        Map<Integer, String> map = new TreeMap<>(Integer::compareTo);
        for (String name : fileName) {
            if (name != null && !name.equals("")) {
                Integer index = Integer.valueOf(name.substring(0, name.indexOf("-")));
                map.put(index, name);
            }
        }

        String path = "D:\\下载\\";
        String newFileName = "new-" + fileName.get(0).substring(fileName.get(0).indexOf("-") + 1);
        String codeFileName = "code-" + fileName.get(0).substring(fileName.get(0).indexOf("-") + 1);
        String newFileNameU = "111-" + fileName.get(0).substring(fileName.get(0).indexOf("-") + 1);

        File resultFile = new File(path + newFileName);
        File codetFile = new File(path + codeFileName);
        File resultFileu = new File(path + newFileNameU);


        try (
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(resultFile));
                BufferedOutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(codetFile));
                BufferedOutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(resultFileu));


        ) {
            byte[] buffer = new byte[50];
            for (Map.Entry<Integer, String> entry : map.entrySet()) {
                int len;
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path + entry.getValue()));
                while ((len = bis.read(buffer)) != -1) {
                    outputStream2.write(buffer,8, (len-8));
                    outputStream1.write(buffer,0,8);
                    outputStream.write(buffer, 0, len);
                }
                bis.close();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String downloadFile(String ipAddr, int port, String fileName, int index, int blockNum) throws IOException {

        String address = ipAddr + ":" + port + "/download?fileName=" + fileName + "&index=" + index + "&blockNum=" + blockNum;
        URL url = new URL(address);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(3 * 1000);
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        InputStream inputStream = conn.getInputStream();
        byte[] getData = readInputStream(inputStream);

        String savePath = "D:\\下载";
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        // File.separator \\符号
        File file = new File(saveDir + File.separator + index +"-" + fileName);
        FileOutputStream fos = new FileOutputStream(file);

        String id = "(id:001)";
        fos.write(id.getBytes());
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        return index + "-" + fileName;
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();

        //inputstream输入流转换成byte[]字节数组
        return bos.toByteArray();
    }


    public static void main(String[] args) throws IOException {
        String ipAddr = "http://localhost";
        int port = 8080;
        String fileName = "bjyxszd.jpg";
        int blockNum = 10;
        MergeFile mergeFile = new MergeFile();
        List<String> fileNames = new ArrayList<>();
        System.out.println("start to download file");

//        int i = 2;
//        System.out.println("download the " + i + "th file!");
//        String absPath = null;
//        absPath = mergeFile.downloadFile(ipAddr, port, fileName, i, blockNum);
//        System.out.println("finish " + i);
//        fileNames.add(absPath);


        for (int i = 0; i < blockNum; i++) {
            System.out.println("download the " + i + " th file!");
            String absPath = null;
            try {
                absPath = mergeFile.downloadFile(ipAddr, port, fileName, i, blockNum);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("finish " + i);
            fileNames.add(absPath);
        }
//        for (int i = 0; i < blockNum; i++) {
//            System.out.println("down load file " + i);
//            String path = mergeFile.downloadFile(ipAddr, port, fileName, i, blockNum);
//            fileNames.add(path);
//        }

        boolean res = mergeFile.merge(fileNames);
        if (res) {
            System.out.println("merge file succeed!");
        } else {
            System.out.println("merge file failed!");
        }

    }

}
