package com.eric.excel.handler;

/**
 * 行号处理器接口
 * 用于在DTO中设置和获取行号、有效性标记、错误信息等
 *
 * @ClassName:   RowNumHandler
 * @author:      liuQ
 * @param <T> DTO类型
 * @date:        2025-02-27
 * @Copyright    ERIC 微信公众号：Eric的技术杂货库
 */
public interface RowNumHandler<T> {

    /**
     * 设置行号
     *
     * @param dto    数据对象
     * @param rowNum 行号
     */
    void setRowNum(T dto, int rowNum);

    /**
     * 获取行号
     *
     * @param dto 数据对象
     * @return 行号，如果未设置则返回0
     */
    int getRowNum(T dto);

    /**
     * 设置有效性标记
     *
     * @param dto   数据对象
     * @param valid 是否有效
     */
    void setValid(T dto, boolean valid);

    /**
     * 获取有效性标记
     *
     * @param dto 数据对象
     * @return 是否有效，如果未设置则返回true
     */
    boolean getValid(T dto);

    /**
     * 设置错误信息
     *
     * @param dto      数据对象
     * @param errorMsg 错误信息
     */
    void setErrorMsg(T dto, String errorMsg);

    /**
     * 获取错误信息
     *
     * @param dto 数据对象
     * @return 错误信息，如果未设置则返回null
     */
    String getErrorMsg(T dto);

    /**
     * 是否支持该DTO类型
     *
     * @param dtoClass DTO类
     * @return true-支持，false-不支持
     */
    boolean supports(Class<?> dtoClass);
}
