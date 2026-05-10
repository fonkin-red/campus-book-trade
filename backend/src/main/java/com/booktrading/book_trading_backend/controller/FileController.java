package com.booktrading.book_trading_backend.controller;

import com.booktrading.book_trading_backend.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    private static final Pattern SAFE_FILENAME = Pattern.compile(
            "^[0-9a-fA-F-]+\\.(jpg|jpeg|png|gif|webp|bmp)$");
    private static final Pattern SAFE_IMAGE_EXT = Pattern.compile(
            "\\.(jpg|jpeg|png|gif|webp|bmp)$", Pattern.CASE_INSENSITIVE);

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error(400, "请选择要上传的文件");
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error(400, "只能上传图片文件");
            }
            String originalName = file.getOriginalFilename();
            String ext = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf(".")).toLowerCase() : ".jpg";
            if (!SAFE_IMAGE_EXT.matcher(ext).matches()) {
                return Result.error(400, "不支持的图片格式");
            }
            String fileName = UUID.randomUUID() + ext;

            Path destDir = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(destDir)) {
                Files.createDirectories(destDir);
            }
            Path dest = destDir.resolve(fileName);
            file.transferTo(dest.toFile());
            log.info("上传成功: {} ({})", fileName, file.getSize());

            return Result.ok("/uploads/" + fileName);
        } catch (IOException e) {
            log.error("上传文件写入失败: ", e);
            return Result.error(500, "文件写入失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("上传异常: ", e);
            return Result.error(500, "上传失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/file")
    public Result<?> delete(@RequestParam String filename) {
        try {
            String name = Paths.get(filename).getFileName().toString();
            if (!SAFE_FILENAME.matcher(name).matches()) {
                return Result.error(400, "非法文件名");
            }
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(name).normalize();
            if (!filePath.startsWith(uploadPath)) {
                return Result.error(400, "非法文件名");
            }
            if (Files.deleteIfExists(filePath)) {
                log.info("删除文件: {}", name);
                return Result.ok();
            }
            return Result.error(404, "文件不存在");
        } catch (IOException e) {
            log.error("删除文件失败: ", e);
            return Result.error(500, "删除失败: " + e.getMessage());
        }
    }
}
