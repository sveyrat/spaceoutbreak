package com.github.sveyrat.spaceoutbreak.util;

import java.util.ArrayList;
import java.util.Collection;

public class StringUtil {

    private StringUtil() {
    }

    public static boolean containsIgnoreCase(Collection<String> collection, String str) {
        for (String toto : collection) {
            if (toto.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }
}
