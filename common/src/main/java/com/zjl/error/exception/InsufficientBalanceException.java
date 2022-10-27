package com.zjl.error.exception;

import java.io.IOException;

public class InsufficientBalanceException extends IOException {
    public InsufficientBalanceException() {
    }

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
