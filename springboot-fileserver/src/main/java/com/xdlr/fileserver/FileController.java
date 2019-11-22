package com.xdlr.fileserver;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
public class FileController {

    @RequestMapping("/download")
    public String download(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           String fileName,
                           int index,
                           int blockNum
    ) {
        if (!StringUtils.hasText(fileName)) {
            return "下载失败";
        }
        if (index >= blockNum) {
            return "下载失败";
        }

        File file = new File("D:\\下载\\" + fileName);
        if (file.exists()) {
            httpServletResponse.setContentType("application/force-download");// 设置强制下载不打开
            httpServletResponse.addHeader("Content-Disposition", "attachment;fileName=" + index + "-" + fileName);// 设置文件名
            try (
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    OutputStream os = httpServletResponse.getOutputStream();
            ) {
                int len;
                int blockSize = (int) Math.ceil(file.length() * 1.0 / blockNum);


                byte[] buffer = new byte[blockSize];
                for (int i = 0; i < index; i++) {
                    bis.read(buffer);
                }
                if ((len = bis.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                    return "下载成功";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return "下载失败";
    }
}
