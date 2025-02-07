package io.invertase.firebase.messaging;

import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.app.Notification;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;
import com.google.firebase.messaging.RemoteMessage;
import io.invertase.firebase.common.ReactNativeFirebaseJSON;

import javax.annotation.Nullable;

public class ReactNativeFirebaseMessagingHeadlessService extends HeadlessJsTaskService {
  private static final long TIMEOUT_DEFAULT = 60000;
  private static final String TIMEOUT_JSON_KEY = "messaging_android_headless_task_timeout";
  private static final String TASK_KEY = "ReactNativeFirebaseMessagingHeadlessTask";

  @Override
  protected @Nullable
  HeadlessJsTaskConfig getTaskConfig(Intent intent) {
    Bundle extras = intent.getExtras();
    if (extras == null) return null;
    RemoteMessage remoteMessage = intent.getParcelableExtra("message");

    return new HeadlessJsTaskConfig(
      TASK_KEY,
      ReactNativeFirebaseMessagingSerializer.remoteMessageToWritableMap(remoteMessage),
      ReactNativeFirebaseJSON.getSharedInstance().getLongValue(TIMEOUT_JSON_KEY, TIMEOUT_DEFAULT),
      // Prevents race condition where the user opens the app at the same time as a notification
      // is delivered, causing a crash.
      true
    );
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    HeadlessJsTaskConfig taskConfig = getTaskConfig(intent);
    if (taskConfig != null) {

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification notification = new Notification();
        startForeground(1, notification);
        startTask(taskConfig);
      } else {
        startTask(taskConfig);
      }

      return 1;
    }
    return 1;
  }
}
