package com.omarea.vboot

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.omarea.shared.AutoClickService
import com.omarea.shared.ServiceHelper


/**
 * Created by helloklf on 2016/8/27.
 */
class AccessibilityServiceVTools : AccessibilityService() {

    /*
override fun onCreate() {
    super.onCreate()

    Notification.Builder builder = new Notification.Builder(this);
    //Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://blog.csdn.net/itachi85/"));
    Intent mIntent = new Intent(getApplicationContext(),AccessibilitySettingsActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
    builder.setContentIntent(pendingIntent);
    builder.setSmallIcon(R.drawable.linux);
    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.linux));
    builder.setAutoCancel(true);
    //RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notxxx_layout);
    //builder.setContent(remoteViews);
    //builder.setContentTitle("微工具箱");
    //builder.setContentInfo("增强服务正在运行，点此进入设置");
    //Notification.Action action = new Notification.Action(R.drawable.p3,"性能",pendingIntent);
    //Notification.Action action1 = new Notification.Action(R.drawable.p2,"均衡",pendingIntent);
    //Notification.Action action2 = new Notification.Action(R.drawable.p1,"省电",pendingIntent);
    //builder.addAction(action);
    //builder.addAction(action1);
    //builder.addAction(action2);

    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    //notificationManager.notify(74545342, builder.build());

    startForeground(74545342, builder.build());//该方法已创建通知管理器，设置为前台优先级后，点击通知不再自动取消
    }
    */

    internal var serviceHelper: ServiceHelper? = null

    public override fun onServiceConnected() {
        /*
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        // We are interested in all types of accessibility events.
        info.eventTypes = AccessibilityEvent.TYPE_WINDOWS_CHANGED;
        // We want to provide specific type of feedback.
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL;
        // We want to receive events in a certain interval.
        info.notificationTimeout = 100;
        // We want to receive accessibility events only from certain packages.
        info.packageNames = null;
        setServiceInfo(info);
        */
        super.onServiceConnected()
        initServiceHelper()
    }

    private fun initServiceHelper(){
        if (serviceHelper == null)
            serviceHelper = ServiceHelper(applicationContext)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.packageName == null || event.className == null)
            return
        val packageName = event.packageName.toString().toLowerCase()
        //修复傻逼一加桌面文件夹抢占焦点导致的问题
        if ((packageName == "net.oneplus.h2launcher" || packageName == "net.oneplus.launcher") && event.className == "android.widget.LinearLayout") {
            return
        }

        if (packageName.contains("packageinstaller")) {
            if (event.className == "com.android.packageinstaller.permission.ui.GrantPermissionsActivity")
                return
            AutoClickService().packageinstallerAutoClick(this.applicationContext, event)
        } else if (packageName == "com.miui.securitycenter") {
            AutoClickService().miuiUsbInstallAutoClick(event)
            return
        }
        if (serviceHelper == null)
            initServiceHelper()
        serviceHelper?.onFocusAppChanged(event.packageName.toString())
    }
    /*
    Thread(Runnable {
        val inst = Instrumentation()
        inst.sendKeyDownUpSync(event.keyCode) }).start()
        */

    override fun onInterrupt() {
        if (serviceHelper != null){
            serviceHelper!!.onInterrupt()
            serviceHelper = null
        }
        //android.os.Process.killProcess(android.os.Process.myPid());
    }

    override fun onDestroy() {
        if (serviceHelper != null){
            serviceHelper!!.onInterrupt()
            serviceHelper = null
        }
        //android.os.Process.killProcess(android.os.Process.myPid());
    }
}