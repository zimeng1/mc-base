package com.mc.base.common.model;

/**
 * 自定义响应体
 * graceful-response.response-class-full-name 中配置
 *
 * @author Conor
 * @since 2024/5/29 上午11:06
 */
public class RetResult<T> {
    // 预定的状态码
    public static final String CODE_SUCCESS = "200";
    public static final String CODE_ERROR = "500";

    protected String code;
    protected String msg;
    protected T data;

    // ============================  getter/setter  ==================================

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 构建
     */
    public RetResult() {
    }

    /**
     * 构建
     *
     * @param code 状态码
     * @param msg  信息
     * @param data 数据
     */
    public RetResult(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ============================  静态方法快速构建  ==================================

    // 构建成功
    public static <T> RetResult<T> ok() {
        return new RetResult<>(CODE_SUCCESS, "ok", null);
    }

    public static <T> RetResult<T> ok(String msg) {
        return new RetResult<>(CODE_SUCCESS, msg, null);
    }

    public static <T> RetResult<T> ok(String msg, T data) {
        return new RetResult<>(CODE_SUCCESS, msg, data);
    }

    public static <T> RetResult<T> code(String code) {
        return new RetResult<>(code, null, null);
    }

    public static <T> RetResult<T> code(String code, String msg) {
        return new RetResult<>(code, msg, null);
    }

    public static <T> RetResult<T> data(T data) {
        return new RetResult<>(CODE_SUCCESS, "ok", data);
    }

    // 构建失败
    public static <T> RetResult<T> error() {
        return new RetResult<>(CODE_ERROR, "error", null);
    }

    public static <T> RetResult<T> error(String msg) {
        return new RetResult<>(CODE_ERROR, msg, null);
    }

    public static <T> RetResult<T> error(Throwable t) {
        String message = t.getMessage();
        if (message.length() > 100) {
            message = message.substring(0, 80);
        }
        return new RetResult<>(CODE_ERROR, message, null);
    }

    // 构建指定状态码
    public static <T> RetResult<T> get(String code, String msg, T data) {
        return new RetResult<>(code, msg, data);
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{"
                + "\"code\": " + this.getCode()
                + ", \"msg\": " + transValue(this.getMsg())
                + ", \"data\": " + transValue(this.getData())
                + "}";
    }

    /**
     * 转换 value 值：
     * 如果 value 值属于 String 类型，则在前后补上引号
     * 如果 value 值属于其它类型，则原样返回
     *
     * @param value 具体要操作的值
     * @return 转换后的值
     */
    private String transValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return "\"" + value + "\"";
        }
        return String.valueOf(value);
    }

    public boolean ifSuccess() {
        return CODE_SUCCESS.equals(this.getCode());
    }
}
