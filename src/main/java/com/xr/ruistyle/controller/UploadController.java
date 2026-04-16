package com.xr.ruistyle.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.xr.ruistyle.common.Result;
import com.xr.ruistyle.common.BusinessException;

import java.io.File;
import java.io.IOException;

@Tag(name = "上传管理", description = "文件上传相关接口")
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${upload.path}")
    private String uploadPath;

    @Operation(summary = "图片上传")
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(400, "上传文件不能为空");
        }

        // 1. 获取原始文件名并提取后缀 (如 .jpg)
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.extName(originalFilename);

        // 2. 生成新的文件名 (使用 UUID/Hutool IdUtil 防止重名覆盖)
        String newFileName = IdUtil.simpleUUID() + "." + suffix;

        // 3. 确保目录存在
        File destDir = new File(uploadPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        // 4. 保存文件到磁盘
        try {
            file.transferTo(new File(uploadPath + newFileName));
        } catch (IOException e) {
            throw new BusinessException(500, "文件保存失败");
        }

        // 5. 返回前端可访问的虚拟 URL 地址
        String fileUrl = "/upload/" + newFileName;
        return Result.success(fileUrl);
    }
}