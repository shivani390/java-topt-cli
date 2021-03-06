package com.dreamsforge;/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Clock input for the time-based OTPs (TOTP). The input is based on the current system time
 * and is adjusted by a persistently stored correction value (offset in minutes).
 *
 * @author klyubin@google.com (Alex Klyubin)
 */
class TotpClock {

    private NetworkTimeProvider timeProvider = new NetworkTimeProvider(HttpClientBuilder.create().build());

    //  private final SharedPreferences mPreferences;

    private final Object mLock = new Object();

    /**
     * Cached value of time correction (in minutes) or {@code null} if not cached. The value is cached
     * because it's read very frequently (once every 100ms) and is modified very infrequently.
     *
     * @GuardedBy {@link #mLock}
     */
    private Integer mCachedCorrectionMinutes;

    TotpClock() {
//    mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//    mPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Gets the number of milliseconds since epoch.
     */
    long currentTimeMillis() {

        long timeMillis;

        try {
            timeMillis = timeProvider.getNetworkTime();
        } catch (Throwable t) {
            timeMillis = System.currentTimeMillis();
        }

        return timeMillis + getTimeCorrectionMinutes() * Utilities.MINUTE_IN_MILLIS;
    }

    /**
     * Gets the currently used time correction value.
     *
     * @return number of minutes by which this device is behind the correct time.
     */
    private int getTimeCorrectionMinutes() {
        synchronized (mLock) {
            if (mCachedCorrectionMinutes == null) {
                try {
                    mCachedCorrectionMinutes = 0;//mPreferences.getInt(PREFERENCE_KEY_OFFSET_MINUTES, 0);
                } catch (ClassCastException e) {
                    mCachedCorrectionMinutes = 0;//Integer.valueOf(mPreferences.getString(PREFERENCE_KEY_OFFSET_MINUTES, "0"));
                }
            }
            return mCachedCorrectionMinutes;
        }
    }

    //  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//    if (key.equals(PREFERENCE_KEY_OFFSET_MINUTES)) {
//      // Invalidate the cache
//      mCachedCorrectionMinutes = null;
//    }
//  }
}
