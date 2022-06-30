package com.guppy.phoneclean.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.guppy.phoneclean.DbHelper;
import com.guppy.phoneclean.IClearApplication;
import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.model.SysConfig;

import org.json.JSONArray;

import java.util.UUID;

public final class DbUtils {
    private static final Object OBJECT = new Object();

    public static final synchronized SysConfig getConfig() {
        synchronized (OBJECT) {
            try {
                SysConfig config = null;
                SQLiteDatabase db = IClearApplication.getDb();
                Cursor cursor = db.query(DbHelper.DATAS_TABLE, null, null, null, null, null, null);
                if (cursor.moveToNext()) {
                    String data = cursor.getString(cursor.getColumnIndex(IConstant.DB_COLUMN_CONTENT));
                    config = new SysConfig().parser(HttpUtils.decodeString(data));
                }
                cursor.close();
                if (config == null) {
                    config = new SysConfig();
                    //config.setUid(UUID.randomUUID().toString());
                    ContentValues values = new ContentValues();
                    values.put(IConstant.DB_COLUMN_ID, UUID.randomUUID().toString());
                    values.put(IConstant.DB_COLUMN_CONTENT, HttpUtils.encodeString(config.toString()));
                    db.insert(IConstant.DATAS_TABLE, null, values);
                }
                if (IConstant.IS_DEBUG) {
                    JSONArray banner = new JSONArray();
                    banner.put("ca-app-pub-3940256099942544/6300978111");
                    banner.put("ca-app-pub-3940256099942544/6300978111");
                    banner.put("ca-app-pub-3940256099942544/6300978111");
                    JSONArray pop = new JSONArray();
                    pop.put("ca-app-pub-3940256099942544/1033173712");
                    pop.put("ca-app-pub-3940256099942544/1033173712");
                    pop.put("ca-app-pub-3940256099942544/1033173712");
                    JSONArray rewards = new JSONArray();
                    rewards.put("ca-app-pub-3940256099942544/5224354917");
                    rewards.put("ca-app-pub-3940256099942544/5224354917");
                    rewards.put("ca-app-pub-3940256099942544/5224354917");
                    JSONArray open = new JSONArray();
                    open.put("ca-app-pub-3940256099942544/3419835294");
                    JSONArray nativead = new JSONArray();
                    nativead.put("ca-app-pub-3940256099942544/2247696110");
                    JSONArray pangle_pop = new JSONArray();
                    pangle_pop.put("980153007");
                    JSONArray pangle_rewards = new JSONArray();
                    pangle_rewards.put("980153008");
                    JSONArray pangle_banner50 = new JSONArray();
                    pangle_banner50.put("980153009");
                    JSONArray pangle_banner250 = new JSONArray();
                    pangle_banner250.put("980153010");
                    JSONArray pangle_nativead = new JSONArray();
                    pangle_nativead.put("980153011");

                    config.setAdmob_banners(banner.toString());
                    config.setAdmob_pops(pop.toString());
                    config.setAdmob_rewards(rewards.toString());
                    config.setAdmob_open(open.toString());
                    config.setAdmob_nativead(nativead.toString());
                    config.setPangle_pop(pangle_pop.toString());
                    config.setPangle_rewarded(pangle_rewards.toString());
                    config.setPangle_banner50(pangle_banner50.toString());
                    config.setPangle_banner250(pangle_banner250.toString());
                    config.setPangle_nativead(pangle_nativead.toString());
                    /*Logger.log("proxy self="+config.getProxy_self());
                    Logger.log("pop[]="+config.getAdmob_pops());
                    Logger.log("banner[]="+config.getAdmob_banners());
                    Logger.log("rewards[]="+config.getAdmob_rewards());*/
                }
                return config;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    public static final synchronized void updateSystemConfig(SysConfig config) {
        synchronized (OBJECT) {
            try {
                if (config == null) return;
                SQLiteDatabase db = IClearApplication.getDb();
                ContentValues values = new ContentValues();
                values.put(IConstant.DB_COLUMN_CONTENT, HttpUtils.encodeString(config.toString()));
                db.update(IConstant.DATAS_TABLE, values, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
