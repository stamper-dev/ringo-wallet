package wannabit.io.ringowallet.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;

import wannabit.io.ringowallet.utils.DeviceUuidFactory;
import wannabit.io.ringowallet.utils.WLog;

public class BaseApplication extends Application {

    private BaseDao     mBaseDao;
    private AppStatus   mAppStatus;

    @Override
    public void onCreate() {
        super.onCreate();
        new DeviceUuidFactory(this);
        registerActivityLifecycleCallbacks(new LifecycleCallbacks());
    }

    public BaseDao getBaseDao() {
        if (mBaseDao == null)
            mBaseDao = new BaseDao(this);
        return mBaseDao;
    }


    public enum AppStatus {
        BACKGROUND,
        RETURNED_TO_FOREGROUND,
        FOREGROUND;
    }


    public boolean isReturnedForground() {
        return mAppStatus.ordinal() == AppStatus.RETURNED_TO_FOREGROUND.ordinal();
    }

    public boolean needShowLockScreen() {
        if(!isReturnedForground() || !getBaseDao().hasPw()) return false;

        if (getBaseDao().getLockTime() == 4) {
            return false;
        } else if (getBaseDao().getLockTime() == 0) {
            return true;
        } else if (getBaseDao().getLockTime() == 1) {
            if (getBaseDao().getLeaveTime() + BaseConstant.CONSTANT_10S >= System.currentTimeMillis()) return false;

        } else if (getBaseDao().getLockTime() == 2) {
            if (getBaseDao().getLeaveTime() + BaseConstant.CONSTANT_30S >= System.currentTimeMillis()) return false;

        } else if (getBaseDao().getLockTime() == 3) {
            if (getBaseDao().getLeaveTime() + BaseConstant.CONSTANT_M >= System.currentTimeMillis()) return false;

        }
        return true;
    }

    public class LifecycleCallbacks implements ActivityLifecycleCallbacks {

        private int running = 0;

        @Override
        public void onActivityStarted(Activity activity) {
            if (++running == 1) {
                mAppStatus = AppStatus.RETURNED_TO_FOREGROUND;
            } else if (running > 1) {
                mAppStatus = AppStatus.FOREGROUND;
            }

        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (--running == 0) {
                mAppStatus = AppStatus.BACKGROUND;
                getBaseDao().setLeaveTime(System.currentTimeMillis());
            }
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) { }

        @Override
        public void onActivityResumed(Activity activity) { }

        @Override
        public void onActivityPaused(Activity activity) { }


        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }

        @Override
        public void onActivityDestroyed(Activity activity) { }
    }
}
