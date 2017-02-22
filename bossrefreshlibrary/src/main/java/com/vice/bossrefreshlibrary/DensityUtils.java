package com.vice.bossrefreshlibrary;

import android.content.Context;

/**
 * Created by vice on 2017/2/18 0018.
 */
public class DensityUtils {
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int Px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
