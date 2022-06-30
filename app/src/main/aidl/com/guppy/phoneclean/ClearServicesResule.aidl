// ClearServicesResuleCallBack.aidl
package com.guppy.phoneclean;

// Declare any non-default types here with import statements
import com.guppy.phoneclean.ClearServicesResuleCallBack;
import com.guppy.phoneclean.ScanningClearListenter;
interface ClearServicesResule {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void setServicesListenterResult(in ClearServicesResuleCallBack clearServicesResuleCallBack);


    void startScanningClear(ScanningClearListenter scanningClearListenter);

}