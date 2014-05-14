package androidkeystore.android.security;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.utils.Platform;
import com.sckftr.android.utils.Procedure;
import com.sckftr.android.utils.Strings;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import androidkeystore.Crypto;

/**
 * Helper class to manage with keystore
 *
 * For more information read <a href="http://nelenkov.blogspot.com/2012/05/storing-application-secrets-in-androids.html">Storing application secrets in Android's credential storage</a>
 */
public abstract class KeyStoreManager extends AsyncTask<String, Void, String> {

    private static final String TAG = KeyStoreManager.class.getSimpleName();

    /**
     * Flag to indicate that keystore is not unlocked
     */
    public static final String ERROR_LOCKED = "error_locked";

    /**
     * Flag to indicate successful operation
     */
    public static final String SUCCESS = "success";

    private KeyStore keyStore;

    @Override
    protected String doInBackground(String... params) {
        try {

            KeyStore keyStore = getKeyStore();
            if (keyStore.state() != KeyStore.State.UNLOCKED) {
                return ERROR_LOCKED;
            }
            return doWork(params);
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage(), e);

            return null;
        }
    }

    abstract String doWork(String... params) throws Exception;


    protected void checkRc(boolean success) {
        if (!success) {
            String errorStr = rcToStr(keyStore.getLastError());
            Log.d("Keystore", "last error = " + errorStr);

            //throw new RuntimeException("Keystore error: " + errorStr);
        }
    }

    public KeyStore getKeyStore() {
        if (keyStore != null) {
            return keyStore;
        } else if (Platform.hasKK()) {
            keyStore = KeyStoreKk.getInstance();
        } else if (Platform.hasJB43()) {
            keyStore = KeyStoreJb43.getInstance();
        } else {
            keyStore = KeyStore.getInstance();
        }
        return keyStore;
    }

    private String rcToStr(int rc) {
        switch (rc) {
            case KeyStore.NO_ERROR:
                return "NO_ERROR";
            case KeyStore.LOCKED:
                return "LOCKED";
            case KeyStore.UNINITIALIZED:
                return "UNINITIALIZED";
            case KeyStore.SYSTEM_ERROR:
                return "SYSTEM_ERROR";
            case KeyStore.PROTOCOL_ERROR:
                return "PROTOCOL_ERROR";
            case KeyStore.PERMISSION_DENIED:
                return "PERMISSION_DENIED";
            case KeyStore.KEY_NOT_FOUND:
                return "KEY_NOT_FOUND";
            case KeyStore.VALUE_CORRUPTED:
                return "VALUE_CORRUPTED";
            case KeyStore.UNDEFINED_ACTION:
                return "UNDEFINED_ACTION";
            case KeyStore.WRONG_PASSWORD:
                return "WRONG_PASSWORD";
            default:
                return "Unknown RC";
        }
    }

    /**
     * Puts the value to keystore
     * @param key to associate with value
     * @param value which to store
     * @param callback returns result status {@link KeyStoreManager#ERROR_LOCKED}, {@link KeyStoreManager#SUCCESS} or <code>null</code>
     */
    public static void put(String key, String value, final Procedure<String> callback) {

        new KeyStoreManager() {
            @Override protected String doWork(String... params) {

                KeyStore keyStore = getKeyStore();

                SecretKey key = Crypto.generateAesKey();
                boolean success = keyStore.put(params[0], key.getEncoded());
                Log.d("Keystore", "put key success: " + success);
                checkRc(success);

                String ciphertext = Crypto.encryptAesCbc(params[1], key);
                AppConst.API.get().putPreference(params[0], ciphertext);

                return SUCCESS;
            }

            @Override protected void onPostExecute(String s) {
                callback.apply(s);
            }

        }.execute(key, value);

    }

    /**
     * Returns the value from keystore
     * @param key associated with value
     * @param callback returns actual value or result status {@link KeyStoreManager#ERROR_LOCKED} or <code>null</code>
     */
    public static void get(String key, final Procedure<String> callback) {

        new KeyStoreManager() {
            @Override protected String doWork(String... params) {
                KeyStore keyStore = getKeyStore();

                final String key1 = params[0];
                byte[] keyBytes = keyStore.get(key1);
                if (keyBytes == null) {
                    Log.w(TAG, "Encryption key not found in keystore: " + key1);

                    return null;
                }

                SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

                String ciphertext = AppConst.API.get().getPreferenceString(key1, Strings.EMPTY);
                String plaintext = Crypto.decryptAesCbc(ciphertext, key);

                return plaintext;
            }

            @Override protected void onPostExecute(String string) {
                callback.apply(string);
            }
        }.execute(key);
    }

    /**
     * Delete all keystore entries
     */
    public static void deleteEntries() {

        new KeyStoreManager() {
            @Override protected String doWork(String... params) {
                KeyStore keyStore = getKeyStore();

                String[] keys = keyStore.saw("");
                for (String key : keys) {
                    boolean success = keyStore.delete(key);
                    AppConst.Log.d(TAG, String.format("delete key '%s' success: %s", key, success));
                    if (!success && Platform.hasJB()) {
                        success = keyStore.delKey(key);
                        AppConst.Log.d(TAG, String.format("delKey '%s' success: %s", key, success));
                    }
                    // delete_keypair() is optional, don't fail
                    checkRc(success);
                }

                return null;
            }
        }.execute();
    }

    /**
     * Starts system activity that the user could enter secret pin to unlock keystore
     * if it is not unlocked
     */
    private void unlock() {
        if (getKeyStore().state() == KeyStore.State.UNLOCKED) {
            return;
        }

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                AppConst.API.get().startActivity(new Intent("android.credentials.UNLOCK"));
            } else {
                AppConst.API.get().startActivity(new Intent("com.android.credentials.UNLOCK"));
            }
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "No UNLOCK activity: " + e.getMessage(), e);
//            Toast.makeText(this, "No keystore unlock activity found.", Toast.LENGTH_SHORT).show();

        }
    }
}

