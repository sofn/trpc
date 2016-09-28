package com.github.sofn.trpc.core.exception;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-19 16:58
 */
public class TRpcRegistryException extends RuntimeException {

    public TRpcRegistryException(Exception e) {
        super(e);
    }

    public TRpcRegistryException(String s, Exception e) {
        super(s, e);
    }

    public TRpcRegistryException(String s) {
        super(s);
    }
}
