package com.github.sofn.trpc.client;

import lombok.Data;

import java.io.IOException;
import java.net.*;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-24 23:35.
 */
@Data
public class Invoker {
    public static void main(String[] args) throws MalformedURLException {
        URL.setURLStreamHandlerFactory(protocol -> new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u) throws IOException {
                return null;
            }
        });


        URL url = new URL("zookeeper://127.0.0.1:8412?hello=world&hello2=world2");
        System.out.println(url.getProtocol());
        System.out.println(url.getHost());
        System.out.println(url.getPort());
        System.out.println(url.getDefaultPort());
        System.out.println(url.getQuery());

    }
}
