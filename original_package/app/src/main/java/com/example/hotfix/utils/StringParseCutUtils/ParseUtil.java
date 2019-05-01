package com.example.hotfix.utils.StringParseCutUtils;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

/**
 * Created by ASUS on 2018/1/17.
 */
public class ParseUtil {

    public static int parseInt(String str) {
        if (str == null)
            return 0;
        int returnInt = 0;
        while (str.startsWith("0")) {
            str = str.substring(1, str.length());
        }
        if (str != null && !str.equals("")) {
            int cutIndex = str.indexOf(".");
            while (cutIndex >= 0 && str.length() > 1) {
                if (cutIndex > 0) {
                    str = str.substring(0, str.indexOf("."));
                } else if (cutIndex == 0 && str.length() > 1) {
                    str = str.substring(1, str.length());
                }
                cutIndex = str.indexOf(".");
            }
            try {
                returnInt = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                returnInt = 0;
            }
        }
        return returnInt;
    }

    public static ValueAnimator showParseStringAnimation(int startValue, int endValue, final TextView showText) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startValue, endValue);
        long duringTime = (long) (endValue * 5.5 + .5);
        if (duringTime > 2500) {
            duringTime = 2500;
        }
        valueAnimator.setDuration(duringTime);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int money = (int) animation.getAnimatedValue();
                showText.setText(money + "");
            }
        });
        return valueAnimator;
    }

    public static float parseFloat(String str) {
        if (str == null)
            return 0;
        float returnFloat = 0;
        while (str.startsWith("0")) {
            str = str.substring(1, str.length());
        }
        if (str != null && !str.equals("")) {
            try {
                returnFloat = Float.parseFloat(str);
            } catch (NumberFormatException e) {
                returnFloat = 0;
            }
        }
        return returnFloat;
    }

    public static String removeStrNotNumber(String sourceString) {
        String returnStr = "";
        char[] chars = sourceString.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char perChar = chars[i];
            byte byteAscii = (byte) perChar;
            if (byteAscii >= 48 && byteAscii <= 57) {
                returnStr += perChar;
            }
        }
        return returnStr;
    }

}
