package bez.dev.featurenotes.misc

import android.app.ActivityManager
import android.content.Context
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager


object Utils {

    val isAppForeground: Boolean
        get() {
            val appProcessInfo = ActivityManager.RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(appProcessInfo)
            return appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
        }


    fun hideKeyboard(context: Context, window: Window?) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window?.currentFocus!!.windowToken, 0)
    }

    fun showKeyboard(window: Window?) {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

}
