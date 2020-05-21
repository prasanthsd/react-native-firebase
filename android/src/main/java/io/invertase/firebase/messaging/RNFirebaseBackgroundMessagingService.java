package io.invertase.firebase.messaging;

import android.content.Intent;
import android.os.Bundle;
import android.os.Build;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;
import com.google.firebase.messaging.RemoteMessage;
import android.app.Notification;

import javax.annotation.Nullable;

public class RNFirebaseBackgroundMessagingService extends HeadlessJsTaskService {
  @Override
  protected @Nullable
  HeadlessJsTaskConfig getTaskConfig(Intent intent) {
    Bundle extras = intent.getExtras();
    if (extras != null) {
      RemoteMessage message = intent.getParcelableExtra("message");
      WritableMap messageMap = MessagingSerializer.parseRemoteMessage(message);
      return new HeadlessJsTaskConfig(
        "RNFirebaseBackgroundMessage",
        messageMap,
        60000,
        false
      );
    }
    return null;
  }

  // should be override on use of startForegroundService
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
