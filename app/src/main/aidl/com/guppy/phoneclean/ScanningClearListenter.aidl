// ClearServicesResuleCallBack.aidl
package com.guppy.phoneclean;

// Declare any non-default types here with import statements
import java.lang.String;
import com.guppy.phoneclean.bean.ListenterInfo;
interface ScanningClearListenter {
    void onCleanStarted();

    void onScanProgressUpdated(in ListenterInfo bean,in int type);

    void onCleanCompleted();
}