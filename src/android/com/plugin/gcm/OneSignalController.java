package com.plugin.gcm;

import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.json.JSONObject;

import com.onesignal.OSDeviceState;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

public class OneSignalController {
  private static final String TAG = "OneSignalPush";

  /**
   * Misc
   */
  public static boolean getIds(CallbackContext callbackContext) {
    // Returns an `OSDeviceState` object with the current immediate device state
    // info
    OSDeviceState device = OneSignal.getDeviceState();
    // Get the OneSignal Push Player Id
    String userId = device.getUserId();
    // Get the OneSignal User Push Token
    String pushToken = device.getPushToken();
    if (userId == null || pushToken == null) {
      OneSignal.addSubscriptionObserver(new OSSubscriptionObserver() {
        @Override
        public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
          if (stateChanges.getTo().getUserId() != null || stateChanges.getFrom().getPushToken() != null) {
            Log.i(TAG, "getIds (Async) - userId: " + stateChanges.getTo().getUserId());
            Log.i(TAG, "getPushTokens (Async) - pushToken: " + stateChanges.getTo().getPushToken());
            executeGetIdCallback(callbackContext, stateChanges.getTo().getUserId(),
                stateChanges.getTo().getPushToken());
            OneSignal.removeSubscriptionObserver(this);
          }
        }
      });
    } else {
      Log.i(TAG, "getIds - userId: " + userId);
      Log.i(TAG, "getPushTokens - pushToken: " + pushToken);
      executeGetIdCallback(callbackContext, userId, pushToken);
    }

    return true;
  }

  public static void executeGetIdCallback(CallbackContext callbackContext, String userId, String pushToken) {
    JSONObject jsonIds = new JSONObject();
    try {
      jsonIds.put("userId", userId);
      jsonIds.put("pushToken", pushToken);
      CallbackHelper.callbackSuccess(callbackContext, jsonIds);
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
}
