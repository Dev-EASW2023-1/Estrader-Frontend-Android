package kr.easw.estrader.android.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.IOException
import java.security.GeneralSecurityException

/**
 * 간단한 정보를 저장해야 되는 상황이 발생할 때 사용할 수 있는 내부 DB인 SharedPreference
 * SharedPreferences 객체는 값 변경 시 editor 객체 사용
 */

class PreferenceUtil(private val context: Context) {
    private var editor: SharedPreferences.Editor? = null
    private var instance: PreferenceUtil? = null

    // SharedPreferences 암호화, AES-256 GCM 알고리즘 사용
    // Key 암호화 AES256_SIV 알고리즘, Value 암호화 AES256_GCM 알고리즘 사용
    private val prefs: SharedPreferences by lazy {
        try {
            val masterKeyAlias = MasterKey
                .Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                FILE_NAME,
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: GeneralSecurityException) {
            null
        } catch (e: IOException) {
            null
        } ?: throw IllegalStateException("prefs cannot be null")
    }

    companion object {
        private const val FILE_NAME = "encrypted_settings"
    }

    @Synchronized
    fun init(): PreferenceUtil {
        if (instance == null) instance = PreferenceUtil(context)
        return instance as PreferenceUtil
    }

    fun start(): PreferenceUtil {
        prefs
        editor = prefs.edit()
        return this
    }

    fun setString(key: String, value: String): PreferenceUtil {
        editor?.putString(key, value)?.commit()
        return this
    }

    fun setInt(key: String, value: Int): PreferenceUtil {
        editor?.putInt(key, value)?.commit()
        return this
    }

    fun setBoolean(key: String, value: Boolean): PreferenceUtil {
        editor?.putBoolean(key, value)?.commit()
        return this
    }

    fun getString(key: String, defValue: String = ""): String? {
        return prefs.getString(key, defValue)
    }

    fun getInt(key: String, defValue: Int = 0): Int {
        return prefs.getInt(key, defValue)
    }

    fun getBoolean(key: String, defValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defValue)
    }

    fun contains(key: String): Boolean {
        return prefs.contains(key)
    }

    fun destroyPref() {
        editor?.clear()?.commit()
    }
}