package com.carlyu.pmxv.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceUtils {

    fun clearUnusedSharedPreferences(context: Context, keys: List<String>) {
        val sharedPreferences = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val lastVersionCode = sharedPreferences.getLong("version_code", 0)

        val currentVersionCode =
            context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode

        if (currentVersionCode > lastVersionCode) {
            // 如果 keys 不是 listOf("")，则执行清理操作
            if (keys != listOf("")) {
                with(sharedPreferences.edit()) {
                    keys.forEach { key ->
                        remove(key)
                    }
                    apply()
                }
            }
            updateVersionCode(sharedPreferences, currentVersionCode)
        } else if (lastVersionCode == 0L) {
            // 如果 version_code 不存在，即其值为默认值 0，那么就直接执行 updateVersionCode
            updateVersionCode(sharedPreferences, currentVersionCode)
        }
    }

    private fun updateVersionCode(sharedPreferences: SharedPreferences, versionCode: Long) {
        // 更新存储的版本号
        with(sharedPreferences.edit()) {
            putLong("version_code", versionCode)
            apply()
        }
    }
}