package kaist.iclab.abclogger.domain

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings

object Permission {

    fun hasAppUsageAccess(context: Context):Boolean{
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.packageName
            )
            } else {
                appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), context.packageName
                )
            }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    //  It requires startActivity()
//    fun requestAppUsageAccess(){
//        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
//    }
}