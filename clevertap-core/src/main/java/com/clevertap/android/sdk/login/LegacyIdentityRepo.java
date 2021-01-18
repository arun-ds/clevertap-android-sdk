package com.clevertap.android.sdk.login;

import static com.clevertap.android.sdk.LogConstants.LOG_TAG_ON_USER_LOGIN;

import androidx.annotation.NonNull;
import com.clevertap.android.sdk.CleverTapInstanceConfig;

/**
 * Legacy class which handles old static identity logic.
 * Here the profile set was fixed as < Email, Identity >
 */
public class LegacyIdentityRepo implements IdentityRepo {

    private static final String TAG = "LegacyIdentityRepo";

    private IdentitySet identities;

    private final CleverTapInstanceConfig mConfig;

    public LegacyIdentityRepo(final CleverTapInstanceConfig config) {
        this.mConfig = config;
        loadIdentitySet();
    }

    @Override
    public IdentitySet getIdentitySet() {
        return identities;
    }

    @Override
    public boolean hasIdentity(@NonNull final String Key) {
        boolean hasIdentity = identities.contains(Key);
        mConfig.log(LOG_TAG_ON_USER_LOGIN,
                "isIdentity [Key: " + Key + " , Value: " + hasIdentity + "]");
        return hasIdentity;
    }

    private void loadIdentitySet() {
        this.identities = IdentitySet.getDefault();
        mConfig.log(LOG_TAG_ON_USER_LOGIN,
                TAG + " Setting the default IdentitySet[" + identities + "]");
    }
}