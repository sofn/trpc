package com.github.sofn.trpc.core.config;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-19 22:13.
 */
@Data
public abstract class AbstractConfig implements Serializable {
    private static final long serialVersionUID = 4267533505537413570L;
    private static final Map<String, AtomicLong> counter = new ConcurrentHashMap<>();

    protected final String key;
    protected long id = -1;

    protected AbstractConfig(String key) {
        this.key = key;
    }

    private static long nextId(String key) {
        return counter.computeIfAbsent(key, s -> new AtomicLong()).getAndIncrement();
    }

    protected void fillId() {
        if (this.id < 0) {
            this.id = nextId(key);
        }
    }
}
