package com.byebug.automation.api.param;

import java.io.Serializable;

public class BaseParam implements Serializable, Cloneable {
    private static final long serialVersionUID = 1905122041950251207L;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
