package com.example.wanghanp.http;


public class UserHttpResult<T> {
    private int result;
    private String result_info;
    private T data;

    public UserHttpResult() {
    }

    public UserHttpResult(int result, String result_info, T data) {
        this.result = result;
        this.result_info = result_info;
        this.data = data;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getResult_info() {
        return result_info;
    }

    public void setResult_info(String result_info) {
        this.result_info = result_info;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserHttpResult{" +
                "result=" + result +
                ", result_info='" + result_info + '\'' +
                ", data=" + data +
                '}';
    }
}
