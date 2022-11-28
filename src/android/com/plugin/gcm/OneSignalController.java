package com.plugin.gcm;

import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collection;


import com.onesignal.OneSignal;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OSInAppMessageAction;

import com.onesignal.OneSignal.NotificationOpenedHandler;
import com.onesignal.OneSignal.NotificationReceivedHandler;
import com.onesignal.OneSignal.GetTagsHandler;
import com.onesignal.OneSignal.IdsAvailableHandler;
import com.onesignal.OneSignal.PostNotificationResponseHandler;

public class OneSignalController {
  private static final String TAG = "OneSignalPush";

  /**
   * Misc
   */
  public boolean getIds(CallbackContext callbackContext) {
    // Returns an `OSDeviceState` object with the current immediate device state info
    OSDeviceState device = OneSignal.getDeviceState();
    //Get the OneSignal Push Player Id
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
            executeGetIdCallback(callbackContext, stateChanges.getTo().getUserId(), stateChanges.getTo().getPushToken());
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

  public void executeGetIdCallback(CallbackContext callbackContext, String userId, String pushToken) {
    JSONObject jsonIds = new JSONObject();
    try {
      jsonIds.put("userId", userId);
      jsonIds.put("pushToken", pushToken);
      CallbackHelper.callbackSuccess(callbackContext, jsonIds);
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
  }
}