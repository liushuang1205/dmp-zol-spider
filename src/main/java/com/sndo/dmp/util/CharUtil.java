package com.sndo.dmp.util;

/**
 * 字符串辅助类
 *
 * @author yangqi
 * @date 2013-11-27 上午11:34:40
 */
public class CharUtil {

    private static final char DBC_CHAR_START = 33;// 半角 !
    private static final char DBC_CHAR_END = 126;// 半角 ~
    private static final char SBC_CHAR_START = 65281;// 全角 ！
    private static final char SBC_CHAR_END = 65374;// 全角 ~
    private static final int CONVERT_STEP = 65248;// 全角半角转换间隔
    private static final char SBC_SPACE = 160;// 全角空格
    private static final char SBC_SPACE3 = 8194;// 全角空格
    private static final char SBC_SPACE2 = 12288;// 全角空格
    private static final char DBC_SPACE = 32;// 半角空格
    private static final char SBC_LINE = 8211;        // 全角–
    private static final char DBC_LINE = 45;        // 半角-

    public static String halfToFull(String input) {

        if (input == null) {
            return input;
        }
        StringBuilder buf = new StringBuilder(input.length());
        char[] ca = input.toCharArray();
        for (int i = 0; i < ca.length; i++) {
            if (ca[i] == DBC_SPACE) { // 如果是半角空格，直接用全角空格替代
                buf.append(SBC_SPACE);
            } else if ((ca[i] >= DBC_CHAR_START) && (ca[i] <= DBC_CHAR_END)) { // 字符是!到~之间的可见字符
                buf.append((char) (ca[i] + CONVERT_STEP));
            } else { // 不对空格以及ascii表中其他可见字符之外的字符做任何处理
                buf.append(ca[i]);
            }
        }
        return buf.toString();
    }

    public static String fullToHalf(String input) {
        if (input == null) {
            return input;
        }
        StringBuilder buf = new StringBuilder(input.length());
        char[] ca = input.toCharArray();
        for (int i = 0; i < input.length(); i++) {
            if (ca[i] >= SBC_CHAR_START && ca[i] <= SBC_CHAR_END) { // 如果位于全角！到全角～区间内
                buf.append((char) (ca[i] - CONVERT_STEP));
            } else if (ca[i] == SBC_SPACE || ca[i] == SBC_SPACE2 || ca[i] == SBC_SPACE3) { // 如果是全角空格
                buf.append(DBC_SPACE);
            } else if (ca[i] == SBC_LINE) {
                buf.append(DBC_LINE);
            } else { // 不处理全角空格，全角！到全角～区间外的字符
                buf.append(ca[i]);
            }
        }
        return buf.toString();
    }

}
