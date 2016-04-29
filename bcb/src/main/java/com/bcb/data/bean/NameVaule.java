package com.bcb.data.bean;

import java.io.Serializable;

/**
 * Created by Ray on 2016/4/29.
 *
 * @desc 公共的Name ：Value
 */
public class NameVaule implements Serializable {
    public String Name;
    public int Value;

    public NameVaule() {
        Name = "";
        Value = 0;
    }

    public NameVaule(String name, int value) {
        Name = name;
        Value = value;
    }

    @Override
    public String toString() {
        return Name;
    }

    public int getValue() {
        return Value;
    }

    public String getName() {
        return Name;
    }
}
