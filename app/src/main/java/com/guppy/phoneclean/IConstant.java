package com.guppy.phoneclean;

import com.guppy.phoneclean.ad.PopAd;
import com.guppy.phoneclean.utils.RandomUntil;

public class IConstant {
    public static final boolean IS_DEBUG = true;

    public static PopAd popAd;
    public static final long AD_LOAD_DELAY = 2 * 1000;

    public static int temperature_increase = RandomUntil.getNum(3,7);
    public static boolean isRuningClearServices = false;
    public static boolean isRuningIClearServices = false;
    public static boolean wtiteTipShow = false;

    public static final String INIT_URL = "https://guppyphoneclean.top/apitest.php";
    //public static final String INIT_URL = "https://guppyphoneclean.top/apitest.php";
    public static final String PRIVACY_POLICY_URL = "https://guppyphoneclean.top/html/privacy.html";
    public static final String INDEX_BUY = "https://sweepercleaner.top/pay.php";

    public static String TO_ADVERTISE = "TO_ADVERTISE";

    public static final String ACTION_NOTIFICATION = "Notification.OnClick";
    public static final String ACTION_HOME = "MainActivity";
    public static final String ACTION_CACHE = "CacheClearActivity";
    public static final String ACTION_COOLDOWN = "CpuCoolDownActivity";
    public static final String ACTION_BATTERY = "BatteryActivity";
    public static final String ACTION_SPEED = "SpeedActivity";

    //自动生成区域
    public static final String DB_NAME="tn";
    public static final String DATAS_TABLE="ddura";
    public static final String DB_COLUMN_ID="p";
    public static final String DB_COLUMN_CONTENT="lvkw";
    public static final String[] SYSTEM_CONFIG_KEYS={"ei","grsh","zxni","r","z","hpacaq","kwsg","fjbyu","sv","itq","o","b","ot","hzecso","dkf","sdkfj","dkfk","dkf","sie","soe","dhgej","dgel","cjge","scvje","seeog","sllxd"};
}
