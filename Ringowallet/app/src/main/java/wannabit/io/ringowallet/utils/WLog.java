package wannabit.io.ringowallet.utils;

import android.util.Log;

import wannabit.io.ringowallet.base.BaseConstant;

public class WLog {

    public static void e(String msg) {
        if (BaseConstant.IS_SHOWLOG) {
            Log.e(BaseConstant.LOG_TAG, msg + "\n");
        }
    }

    public static void w(String msg) {
        if (BaseConstant.IS_SHOWLOG) {
            Log.w(BaseConstant.LOG_TAG, msg+ "\n");
        }
    }

    public static void d(String msg) {
        if (BaseConstant.IS_SHOWLOG) {
            Log.d(BaseConstant.LOG_TAG, msg+ "\n");
        }
    }

//    public static void r(String msg) {
//        e(msg);
//        try {
//            Crashlytics.log(msg);
//        } catch (Exception e) {
//            e("Failed to bug reprot");
//        }
//    }
}
