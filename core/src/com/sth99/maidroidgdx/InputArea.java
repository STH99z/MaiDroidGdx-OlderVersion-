package com.sth99.maidroidgdx;

/**
 * Created by STH99 on 2016/10/18.
 */

public class InputArea {
    private InputStatus inputStatus;

    public void setInputStatus(InputStatus status) {
        this.inputStatus = status;
    }

    public InputStatus getInputStatus() {
        return this.inputStatus;
    }

    public boolean isTouched() {
        return inputStatus == InputStatus.justTouched || inputStatus == InputStatus.hold;
    }

    public InputArea() {
        super();
    }
}
