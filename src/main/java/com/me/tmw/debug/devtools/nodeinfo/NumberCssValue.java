package com.me.tmw.debug.devtools.nodeinfo;

public class NumberCssValue extends StringCssValue {

    NumberCssValue(Number initialValue, Runnable updateNode) {
        super(initialValue.toString(), updateNode);
    }

    public double getDoubleValue() {
        return Double.parseDouble(genAlt().trim());
    }

}
