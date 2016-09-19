package com.github.sofn.trpc.core.config;

import lombok.Data;

import java.io.Serializable;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-19 22:13.
 */
@Data
public abstract class AbstractConfig implements Serializable {
    private static final long serialVersionUID = 4267533505537413570L;

    protected int id;
}
