package com.github.sofn.trpc.core.exception;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-19 16:58
 */
public class TRpcException extends RuntimeException {

    public TRpcException(Exception e) {
        super(e);
    }
}
