package org.talend.dataquality.wordnet;

public class TextUtils {

    public static String cutText(String input) {
        StringBuilder sb = new StringBuilder();
        int len = input.length();
        if (len > 0) {
            sb.append(input.charAt(0));
            char lastCh = input.charAt(0);
            for (int i = 1; i < len; i++) {
                char ch = input.charAt(i);
                if (Character.isUpperCase(ch)) {// current char is uppercased.
                    char nextChar = i < len - 1 ? input.charAt(i + 1) : ' ';
                    if (Character.isLowerCase(lastCh) && Character.isLetter(nextChar)) {
                        // last char is lowercased, which means the current char starts a new word.
                        sb.append(' ');
                    } else if (Character.isUpperCase(lastCh) && Character.isLowerCase(nextChar)) {
                        // next char is lowercased, which alse means the current char starts a new word.
                        sb.append(' ');
                    }
                }
                sb.append(ch);
                lastCh = ch;
            }
        }
        return sb.toString();
    }

    public static String[] cutTextAndSplit(String input) {
        return cutText(input).split(" ");
    }
}
