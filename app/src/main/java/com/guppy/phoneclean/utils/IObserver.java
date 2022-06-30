package com.guppy.phoneclean.utils;

import com.blankj.utilcode.util.UiMessageUtils;

import java.util.Observable;

import androidx.annotation.NonNull;

public class IObserver extends Observable {
    private IObserver() {
    }

    private static IObserver iObserver = null;

    public static IObserver getInstance() {
        if (iObserver == null) {
            synchronized (IObserver.class) {
                if (iObserver == null) {
                    iObserver = new IObserver();
                }
            }
        }
        return iObserver;
    }

    public void post(@NonNull ObserverType observerType) {
        setChanged();
        notifyObservers(observerType);
    }

    public enum ObserverType {
        //发送消息的类型：请求白名单
        WHITE_OPEN_TIP;
    }



    public void postSM(@NonNull ObserverTypePro observerTypePro) {
        UiMessageUtils.getInstance().send(observerTypePro.getCode());
    }

    public enum ObserverTypePro {
        //发送消息的类型：检查白名单，加速或者杀毒或者cpu降温
        CHEACK_WHITE_OPEN(0), ACTION_TYPE_SEED_OR_ANTIVITRUE_OR_CPUCOOLDOWN(1);

        private int code;

        ObserverTypePro(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}