package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @author laijunhan
 * 文件上传 和 文件下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    /**
     * 文件上传
     * @param file 上传的文件
     * @return 返回结果类
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        // 原始文件名
        String originalFilename = file.getOriginalFilename();
        // 文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 使用UUID作为文件名
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        // 保存文件
        try{
            file.transferTo(new File("D:/reggie-images/" + uuid + suffix));
        } catch (IOException e){
            e.printStackTrace();
        }
        return R.success("D:/reggie-images/" + uuid + suffix);
    }

    /**
     * 文件下载
     * @param name 文件名
     * @param response 响应对象
     */
    @GetMapping("/download")
    public void download(@RequestParam("name") String name, HttpServletResponse response) {
        try {
            // 输入流 读取文件内容
            FileInputStream fis = new FileInputStream(new File(name));
            // 输出流 通过输出流将文件写入浏览器，浏览器展示图片
            ServletOutputStream out = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = fis.read(bytes)) != -1){
                out.write(bytes, 0, len);
                out.flush();
            }
            // 关闭流
            fis.close();
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
