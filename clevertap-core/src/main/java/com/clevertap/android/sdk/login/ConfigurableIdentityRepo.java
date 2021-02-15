package com.clevertap.android.sdk.login;

import static com.clevertap.android.sdk.utils.LogConstants.LOG_TAG_ON_USER_LOGIN;

import android.content.Context;
import androidx.annotation.NonNull;
import com.clevertap.android.sdk.CleverTapInstanceConfig;
import com.clevertap.android.sdk.DeviceInfo;
import com.clevertap.android.sdk.validation.ValidationResult;
import com.clevertap.android.sdk.validation.ValidationResultFactory;
import com.clevertap.android.sdk.validation.ValidationResultStack;

public class ConfigurableIdentityRepo implements IdentityRepo {

    private static final String TAG = "ConfigurableIdentityRepo";

    private IdentitySet identitySet;

    private final LoginInfoProvider infoProvider;

    private final CleverTapInstanceConfig mConfig;

    private final ValidationResultStack mValidationResultStack;

    public ConfigurableIdentityRepo(Context context, CleverTapInstanceConfig config, DeviceInfo deviceInfo,
            ValidationResultStack mValidationResultStack) {
        this.mConfig = config;
        this.infoProvider = new LoginInfoProvider(context, config, deviceInfo);
        this.mValidationResultStack = mValidationResultStack;
        loadIdentitySet();
    }

    @Override
    public IdentitySet getIdentitySet() {
        return identitySet;
    }

    @Override
    public boolean hasIdentity(@NonNull String Key) {
        boolean hasIdentity = identitySet.contains(Key);
        mConfig.log(LOG_TAG_ON_USER_LOGIN,
                TAG + "isIdentity [Key: " + Key + " , Value: " + hasIdentity + "]");
        return hasIdentity;
    }

    /**
     * Loads the identity set
     */
    void loadIdentitySet() {

        // Read from Pref
        IdentitySet prefKeySet = IdentitySet.from(infoProvider.getCachedIdentityKeysForAccount());

        mConfig.log(LOG_TAG_ON_USER_LOGIN,
                TAG + "PrefIdentitySet [" + prefKeySet + "]");

        /* ----------------------------------------------------------------
         *   For Default Instance - Get Identity Set configured via Manifest
         *   For Multi Instance - Get Identity Set configured via the setter
         * ---------------------------------------------------------------- */
        IdentitySet configKeySet = IdentitySet
                .from(mConfig.getIdentityKeys());

        mConfig.log(LOG_TAG_ON_USER_LOGIN,
                TAG + "ConfigIdentitySet [" + configKeySet + "]");

        /* ---------------------------------------------------
         *    Push error to LC in-case the data available
         *   in the config & preferences are not matching
         * --------------------------------------------------- */
        handleError(prefKeySet, configKeySet);

        /* ---------------------------------------------------
         *      If data is available from Pref, use
         *      else data is available via from config use
         *      else use legacy key set
         * --------------------------------------------------- */
        if (prefKeySet.isValid()) {
            identitySet = prefKeySet;
            mConfig.log(LOG_TAG_ON_USER_LOGIN,
                    TAG + "Identity Set activated from Pref[" + identitySet + "]");
        } else if (configKeySet.isValid()) {
            identitySet = configKeySet;
            mConfig.log(LOG_TAG_ON_USER_LOGIN,
                    TAG + "Identity Set activated from Config[" + identitySet + "]");
        } else {
            identitySet = IdentitySet.getDefault();
            mConfig.log(LOG_TAG_ON_USER_LOGIN,
                    TAG + "Identity Set activated from Default[" + identitySet + "]");
        }
        boolean isSavedInPref = prefKeySet.isValid();
        if (!isSavedInPref) {
            /* -------------------------------------------------------------------------
             *  If Config data is available, save in the preference if not saved already.
             * ------------------------------------------------------------------------ */
            String storedValue = identitySet.toString();
            infoProvider.saveIdentityKeysForAccount(storedValue);
            mConfig.log(LOG_TAG_ON_USER_LOGIN,
                    TAG + "Saving Identity Keys in Pref[" + storedValue + "]");
        }
    }

    /**
     * Push error to LC in-case the data available
     * in the config & preferences are not matching
     *
     * @param prefKeySet   - key set in the preference
     * @param configKeySet - key set in the config
     */
    private void handleError(final IdentitySet prefKeySet, final IdentitySet configKeySet) {
        if (prefKeySet.isValid() && configKeySet.isValid() && !prefKeySet.equals(configKeySet)) {
            ValidationResult error = ValidationResultFactory.create(531);
            mValidationResultStack.pushValidationResult(error);
            mConfig.log(LOG_TAG_ON_USER_LOGIN,
                    TAG + "pushing error due to mismatch [Pref:" + prefKeySet + "], [Config:" + configKeySet + "]");
        } else {
            mConfig.log(LOG_TAG_ON_USER_LOGIN,
                    TAG + "No error found while comparing [Pref:" + prefKeySet + "], [Config:" + configKeySet + "]");
        }
    }
}