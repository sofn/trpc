package com.github.sofn.trpc.direct;

import com.github.sofn.trpc.demo.Hello;
import org.apache.thrift.TException;

import java.util.concurrent.TimeUnit;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-23 19:11
 */
public class HelloServer implements Hello.Iface {

    public String hi(String name) throws TException {
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello " + name;
    }
}
