package com.team05.demo.global.exception;


import com.team05.demo.global.rsData.RsData;

public class ServiceException extends RuntimeException {

    private String msg;
    private String resultCode;

    public ServiceException(String msg, String resultCode) {
        super(msg);
        this.msg = msg;
        this.resultCode = resultCode;
    }

    public RsData<Void> getRsData() {
        return new RsData<>(
                msg, resultCode
        );
    }
}
