package org.dq.lombok;

import org.apache.commons.lang3.StringUtils;

public class Utils {
    /**
     * 首字母转大写
     *
     * @return
     */
    public static String toFirstCapital(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        String firstWord = String.valueOf(str.charAt(0));
        return str.replaceFirst(firstWord, firstWord.toUpperCase());
    }
}
