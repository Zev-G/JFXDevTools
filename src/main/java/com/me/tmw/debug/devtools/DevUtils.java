package com.me.tmw.debug.devtools;

public final class DevUtils {

    public static String getSimpleClassName(Class<?> aClass) {
        if (aClass.isAnonymousClass()) {
            return "Anonymous " + aClass.getSuperclass().getSimpleName();
        } else {
            return aClass.getSimpleName();
        }
    }

}
