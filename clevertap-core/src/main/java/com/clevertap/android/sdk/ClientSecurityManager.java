package com.clevertap.android.sdk;

public interface ClientSecurityManager {
    String encryptString(String data);

    String decryptString(String data);
}
