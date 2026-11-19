package com.eric.excel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Excel导入配置类
 * 用于配置导入过程中的各项参数，消除硬编码
 * 通过application.yml中excel.import前缀进行配置
 * @ClassName:   ExcelImportConfig
 * @author:      liuQ
 * @date:        2025-02-27
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
@Data
@Component
@ConfigurationProperties(prefix = "excel.import")
public class ExcelImportConfig {

    /**
     * 表头行号（从1开始）
     * 默认第1行是表头
     */
    private int headerRowNum = 1;

    /**
     * 数据起始行号（从1开始）
     * 默认第2行开始是数据
     */
    private int dataStartRowNum = 2;

    /**
     * 行号字段名
     * 用于在DTO中设置/获取行号
     */
    private String rowNumField = "rowNum";

    /**
     * 有效性字段名
     * 用于标记数据是否通过校验
     */
    private String validField = "valid";

    /**
     * 错误信息字段名
     * 用于存储校验错误信息
     */
    private String errorMsgField = "errorMsg";

    /**
     * 批量操作批次大小
     * 每批次处理的数据条数
     */
    private int batchSize = 1000;

    /**
     * 批量操作失败时是否回退到单条处理
     * true: 批量失败时逐条处理以精确定位错误
     * false: 批量失败时直接报错
     */
    private boolean fallbackToSingleOnBatchFail = true;

    /**
     * 是否自动设置行号
     * true: 自动在预览阶段设置行号
     * false: 不自动设置行号
     */
    private boolean autoSetRowNum = true;

    /**
     * 是否自动标记有效性
     * true: 自动在校验后标记有效性
     * false: 不自动标记有效性
     */
    private boolean autoSetValid = true;

    /**
     * 是否自动设置错误信息
     * true: 自动在校验失败后设置错误信息
     * false: 不自动设置错误信息
     */
    private boolean autoSetErrorMsg = true;

    /**
     * 计算实际的数据行号
     *
     * @param index 数据在列表中的索引（从0开始）
     * @return 实际的Excel行号
     */
    public int calculateRowNum(int index) {
        return dataStartRowNum + index;
    }

    /**
     * 计算数据在列表中的索引
     *
     * @param rowNum Excel行号
     * @return 列表索引（从0开始）
     */
    public int calculateIndex(int rowNum) {
        return rowNum - dataStartRowNum;
    }
}
