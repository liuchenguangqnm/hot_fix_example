package com.example.hotfix.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/8/18.
 */
public class StringUtils {

    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str) || "null".equals(str) || str.length() == 0)
            return true;
        return false;
    }

    public static boolean isNotEmpty(String str) {
        return str != null && str.trim().length() > 0;
    }

    public static boolean isTelNumber(String phone) {
//        String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
//
//        Pattern p = Pattern.compile(regExp);
//
//        Matcher m = p.matcher(str);
//
//        return m.find();

        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(phone);
        boolean isMatch = m.matches();
        return isMatch;
    }

    public static boolean isNickName(String str) {
        String regExp = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{1,12}$";

        Pattern p = Pattern.compile(regExp);

        Matcher m = p.matcher(str);

        return m.find();
    }

    public static boolean isNumber(String str) {
        if (isEmpty(str))
            return false;

        String regExp = "^[0-9]{1}$";

        Pattern p = Pattern.compile(regExp);

        Matcher m = p.matcher(str.substring(0, 1));

        return m.find();
    }

    // 国标码和区位码转换常??
    static final int GB_SP_DIFF = 160;
    // 存放国标????汉字不同读音的起始区位码
    static final int[] secPosValueList = {1601, 1637, 1833, 2078, 2274, 2302,
            2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027,
            4086, 4390, 4558, 4684, 4925, 5249, 5600};
    // 存放国标????汉字不同读音的起始区位码对应读音
    static final char[] firstLetter = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x',
            'y', 'z'};

    // 获取????字符串的拼音??
    public static String getFirstLetter(String oriStr) {
        String str = oriStr.toLowerCase();
        StringBuffer buffer = new StringBuffer();
        char ch;
        char[] temp;
        for (int i = 0; i < str.length(); i++) { // 依次处理str中每个字??
            ch = str.charAt(i);
            temp = new char[]{ch};
            byte[] uniCode = new String(temp).getBytes();
            if (uniCode[0] < 128 && uniCode[0] > 0) { // 非汉??
                buffer.append(temp);
            } else {
                buffer.append(convert(uniCode));
            }
        }
        return buffer.toString();
    }

    /**
     * 获取????汉字的拼音首字母??GB码两个字节分别减??60，转换成10进制码组合就可以得到区位??
     * 例如汉字“你”的GB码是0xC4/0xE3，分别减??xA0??60）就??x24/0x43
     * 0x24转成10进制就是36??x43??7，那么它的区位码就是3667，在对照表中读音为??n??
     */
    static char convert(byte[] bytes) {
        char result = '-';
        int secPosValue = 0;
        int i;
        for (i = 0; i < bytes.length; i++) {
            bytes[i] -= GB_SP_DIFF;
        }
        secPosValue = bytes[0] * 100 + bytes[1];
        for (i = 0; i < 23; i++) {
            if (secPosValue >= secPosValueList[i]
                    && secPosValue < secPosValueList[i + 1]) {
                result = firstLetter[i];
                break;
            }
        }
        return result;
    }

    // 判断字符串的合法性
    public static boolean checkStr(String str) {
        if (null == str) {
            return false;
        }
        if ("".equals(str)) {
            return false;
        }
        if ("".equals(str.trim())) {
            return false;
        }
        if ("null".equals(str) || "nul".equals(str) || "Nal".equals(str)) {
            return false;
        }
        return true;
    }

    // 判断电话号码格式是否正确
    public static boolean isMobileNO(String mobiles) {

		/*
         * Pattern p = Pattern
		 *
		 * .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		 */

        if (!checkStr(mobiles))
            return false;
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(mobiles);

        return m.matches() & mobiles.trim().length() == 11;

    }

    // 判断密码格式是否正确
    public static boolean isPassword(String password) {
        if (password.length() >= 6) {
            return true;
        }
        if (password.length() <= 13) {
            return true;
        }
        return false;
    }

    /*
     * 判断字符串是否含有数字
     */
    public static boolean isContainsNum(String content) {
        if (!checkStr(content))
            return false;
        boolean isDigit = false;
        for (int i = 0; i < content.length(); i++) { // 循环遍历字符串
            if (Character.isDigit(content.charAt(i))) { // 用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            }
            if (Character.isLetter(content.charAt(i))) { // 用char包装类中的判断字母的方法判断每一个字符
                // isLetter = true;
            }
        }
        return isDigit;
    }

    /**
     * 是否含有特殊字符
     *
     * @return
     */
    public static boolean isContainUnNormal(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        return str.contains(regEx);
    }

    /*
     * 是否为中文
     */
    public static boolean isChineseChar(String str) {
        if (!checkStr(str))
            return false;
        if (isContainsNum(str)) {
            return false;
        }
        if (isContainUnNormal(str)) {
            return false;
        }
        if (str.matches("^[a-zA-Z]*")) {
            return false;
        }
        boolean temp = false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            temp = true;
        }
        return temp;
    }

    public static String formatMoney(String s) {//, int len
        if (!checkStr(s))
            return "";
        int len = s.length();
        NumberFormat formater = null;
        double num = Double.parseDouble(s);
        if (len == 0) {
            formater = new DecimalFormat("###,###");

        } else {
            StringBuffer buff = new StringBuffer();
            buff.append("###,###.");
            for (int i = 0; i < len; i++) {
                buff.append("#");
            }
            formater = new DecimalFormat(buff.toString());
        }
        String result = formater.format(num);
        /*if (result.indexOf(".") == -1) {
            result = "￥" + result + ".00";
		} else {
			result = "￥" + result;
		}*/
        return result;
    }

    /*
     * 检查是否为小数
     */
    public static boolean isPointNum(String value) {
        if (!checkStr(value))
            return false;
        try {
            Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /*
     * 检查是否为小数
     */
    public static boolean isIntNum(String value) {
        if (!checkStr(value))
            return false;
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static String getStringsByList(ArrayList<String> list) {
        if (null == list || list.size() <= 0)
            return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i) + ",");
        }
        return sb.toString();
    }

    public static boolean isHttpUrl(String url) {
        if (!checkStr(url))
            return false;
        if (url.startsWith("http://"))
            return true;
        return false;
    }
//===================================================================================================

    /**
     * 正则表达式：验证身份证
     */

    public static String IDCardValidate(String IDStr) {
        String tipInfo = "该身份证有效！";// 记录错误信息
        try {
            String Ai = "";
            // 判断号码的长度 15位或18位
            if (IDStr.length() != 15 && IDStr.length() != 18) {
                tipInfo = "身份证号码长度应该为15位或18位。";
                return tipInfo;
//                return false;
            }


            // 18位身份证前17位位数字，如果是15位的身份证则所有号码都为数字
            if (IDStr.length() == 18) {
                Ai = IDStr.substring(0, 17);
            } else if (IDStr.length() == 15) {
                Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
            }
            if (isNumeric(Ai) == false) {
                tipInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
                return tipInfo;
//                return false;
            }


            // 判断出生年月是否有效
            String strYear = Ai.substring(6, 10);// 年份
            String strMonth = Ai.substring(10, 12);// 月份
            String strDay = Ai.substring(12, 14);// 日期
            if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
                tipInfo = "身份证出生日期无效。";
                return tipInfo;
//                return false;
            }
            GregorianCalendar gc = new GregorianCalendar();
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
            try {
                if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                        || (gc.getTime().getTime() - s.parse(
                        strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                    tipInfo = "身份证生日不在有效范围。";
                    return tipInfo;
//                    return false;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
                tipInfo = "身份证月份无效";
                return tipInfo;
//                return false;
            }
            if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
                tipInfo = "身份证日期无效";
                return tipInfo;
//                return false;
            }


            // 判断地区码是否有效
            Hashtable areacode = GetAreaCode();
            //如果身份证前两位的地区码不在Hashtable，则地区码有误
            if (areacode.get(Ai.substring(0, 2)) == null) {
                tipInfo = "身份证地区编码错误。";

//                return false;
            }

            if (IDStr.endsWith("x")) {
                tipInfo = " X必须大写 ";
                return tipInfo;
            }

            if (isVarifyCode(Ai, IDStr) == false) {
                tipInfo = "身份证校验码无效，不是合法的身份证号码";
                return tipInfo;
//                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tipInfo;
    }


    /*
     * 判断第18位校验码是否正确
    * 第18位校验码的计算方式：
       　　1. 对前17位数字本体码加权求和
       　　公式为：S = Sum(Ai * Wi), i = 0, ... , 16
       　　其中Ai表示第i个位置上的身份证号码数字值，Wi表示第i位置上的加权因子，其各位对应的值依次为： 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2
       　　2. 用11对计算结果取模
       　　Y = mod(S, 11)
       　　3. 根据模的值得到对应的校验码
       　　对应关系为：
       　　 Y值：     0  1  2  3  4  5  6  7  8  9  10
       　　校验码： 1  0  X  9  8  7  6  5  4  3   2
    */
    private static boolean isVarifyCode(String Ai, String IDStr) {
        String[] VarifyCode = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum = sum + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
        }
        int modValue = sum % 11;
        String strVerifyCode = VarifyCode[modValue];
        Ai = Ai + strVerifyCode;
        if (IDStr.length() == 18) {
            if (Ai.equals(IDStr) == false) {
                return false;

            }
        }
        return true;
    }


    /**
     * 将所有地址编码保存在一个Hashtable中
     *
     * @return Hashtable 对象
     */

    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**
     * 判断字符串是否为数字,0-9重复0次或者多次
     *
     * @param strnum
     * @return
     */
    private static boolean isNumeric(String strnum) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(strnum);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 功能：判断字符串出生日期是否符合正则表达式：包括年月日，闰年、平年和每月31天、30天和闰月的28天或者29天
     *
     * @return
     */
    public static boolean isDate(String strDate) {

        Pattern pattern = Pattern
                .compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))?$");
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }
}
