package com.logistics.service.dto;

import lombok.Data;

@Data
public class SimpleResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> SimpleResponse<T> success(T data) {
        SimpleResponse<T> response = new SimpleResponse<>();
        response.setSuccess(true);
        response.setMessage("操作成功");
        response.setData(data);
        return response;
    }

    public static <T> SimpleResponse<T> error(String message) {
        SimpleResponse<T> response = new SimpleResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}