package com.mall.backend.dto;

public class Result<T> {
    private int code;
    private String message;
    private T data;

    public Result() {}

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public static <T> Result<T> ok(T data) {
        return new Result<T>(200, "success", data);
    }

    public static <T> Result<T> ok(String message, T data) {
        return new Result<T>(200, message, data);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<T>(400, message, null);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<T>(code, message, null);
    }
}
