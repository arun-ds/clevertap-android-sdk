package com.clevertap.android.sdk

import android.content.Context
import com.clevertap.android.sdk.pushnotification.PushProviders
import org.mockito.*

class MockCoreState(context: Context, cleverTapInstanceConfig: CleverTapInstanceConfig) : CoreState(context) {

    init {
        config = cleverTapInstanceConfig
        postAsyncSafelyHandler = MockPostAsyncSafelyHandler(cleverTapInstanceConfig)
        deviceInfo = Mockito.mock(DeviceInfo::class.java)
        pushProviders = Mockito.mock(PushProviders::class.java)
        sessionManager = Mockito.mock(SessionManager::class.java)
    }
}