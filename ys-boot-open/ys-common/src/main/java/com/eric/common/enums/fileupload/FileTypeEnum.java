package com.eric.common.enums.fileupload;

import com.eric.common.enums.BaseEnum;
import com.eric.common.module.domain.vo.EnumVo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 文件类型枚举
 * @ClassName: FileTypeEnum
 * @author: Eric
 * @date: 2025-09-12
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
@Getter
@AllArgsConstructor
public enum FileTypeEnum implements BaseEnum {

    // 图片类型
    IMAGE("image", "图片"),
    // 文档类型
    DOCUMENT("document", "文档"),
    // 视频类型
    VIDEO("video", "视频"),
    // 音频类型
    AUDIO("audio", "音频"),
    // 压缩包类型
    ARCHIVE("archive", "压缩包"),
    // 其他类型
    OTHER("other", "其他");

    public final String key;
    public final String value;

    @Override
    public Object getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }



    /**
     * 根据文件扩展名获取文件类型
     */
    public static FileTypeEnum getByExtension(String extension) {
        if (extension == null || extension.trim().isEmpty()) {
            return OTHER;
        }
        
        String ext = extension.toLowerCase().replace(".", "");
        
        // 图片类型
        if ("jpg,jpeg,png,gif,bmp,webp,svg,ico".contains(ext)) {
            return IMAGE;
        }
        // 文档类型
        if ("doc,docx,pdf,txt,md,xls,xlsx,ppt,pptx,csv".contains(ext)) {
            return DOCUMENT;
        }
        // 视频类型
        if ("mp4,avi,mkv,mov,wmv,flv,webm,m4v".contains(ext)) {
            return VIDEO;
        }
        // 音频类型
        if ("mp3,wav,flac,aac,ogg,wma,m4a".contains(ext)) {
            return AUDIO;
        }
        // 压缩包类型
        if ("zip,rar,7z,tar,gz,bz2".contains(ext)) {
            return ARCHIVE;
        }
        
        return OTHER;
    }

    public static List<EnumVo> getSelectList() {
        return Arrays.stream(values()).map(item -> new EnumVo(item.getKey(), item.getValue())).toList();
    }
}