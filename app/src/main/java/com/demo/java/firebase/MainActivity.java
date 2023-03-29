package com.demo.java.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.URL;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        Allo.i ("onSaveInstanceState " + getClass ());
        super.onSaveInstanceState (outState);
    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState)
    {
        Allo.i ("onRestoreInstanceState " + getClass ());
        super.onRestoreInstanceState (savedInstanceState);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        Allo.i ("onCreate " + getClass ());
        super.onCreate (savedInstanceState);

        try
        {
            setContentView(R.layout.activity_main);
            rotateFirebase ();
            enableNotificationPermission ();
        } catch (Exception e) { e.printStackTrace (); }

    }

    @Override
    public void onStart ()
    {
        Allo.i ("onStart " + getClass ());
        super.onStart ();
    }

    @Override
    public void onRestart ()
    {
        Allo.i ("onRestart " + getClass ());
        super.onRestart ();
    }

    @Override
    protected void onResume ()
    {
        Allo.i ("onResume " + getClass ());
        super.onResume ();

        try
        {
            rotateNotification ();
        } catch (Exception e) { e.printStackTrace (); }
    }

    @Override
    public void onPause ()
    {
        Allo.i ("onPause " + getClass ());
        super.onPause ();
    }

    @Override
    public void onStop ()
    {
        Allo.i ("onStop " + getClass ());
        super.onStop ();
    }

    @Override
    public void onDestroy ()
    {
        Allo.i ("onDestroy " + getClass ());
        super.onDestroy ();
    }

    @Override
    // 푸시 알림의 데이터를 활용하기 위해 반드시 override 해야함 (특히 카카오톡과 같은 다중 알림)
    protected void onNewIntent (Intent intent)
    {
        super.onNewIntent (intent);
        Allo.i ("onNewIntent " + getClass ());

        try
        {
            setIntent (intent);
        } catch (Exception e) { e.printStackTrace (); }
    }

    private void enableNotificationPermission ()
    {
        Allo.i ("enableNotificationPermission " + getClass ());

        try
        {
            boolean enabled = false;
            Set<String> managedPackages = NotificationManagerCompat.getEnabledListenerPackages (this);
            String packageName = getPackageName ();
            for (String managedPackage : managedPackages)
            {
                if (packageName.equals (managedPackage))
                {
                    enabled = true; break;
                }
            }
            if (!enabled)
            {
                startActivity (new Intent ("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            }
        } catch (Exception e) { e.printStackTrace (); }
    }

    private void rotateNotification ()
    {
        Allo.i ("rotateNotification " + getClass ());

        try
        {
            Intent params = getIntent ();
            if (null != params)
            {
                if (null != params.getStringExtra (Allo.CUBE_LINK))
                {
                    String link = params.getStringExtra (Allo.CUBE_LINK);
                    Allo.i ("Check link [" + link + "]");
                    try
                    {
                        new URL (link); // 유효하지 않은 경우엔 Exception 및 스킵함
                        startActivity (new Intent (Intent.ACTION_VIEW).setData (Uri.parse (link)));
                    } catch (Exception x) { x.printStackTrace (); }
                    // 푸시 알림 패러미터 재실행 방지를 위해 데이터 삭제요
                    // 예 : (데이터 삭제를 안하면) 띄워진 외부 링크 확인후 앱으로 넘어오면 다시 외부 링크를 띄움 (무한 반복)
                    params.removeExtra (Allo.CUBE_LINK);
                }
            }
        } catch (Exception e) { e.printStackTrace (); }
    }

    private void rotateFirebase ()
    {
        Allo.i ("rotateFirebase " + getClass ());

        try
        {
            FirebaseMessaging.getInstance ().getToken ()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete (@NonNull Task<String> task) {
                            if (!task.isSuccessful ()) {
                                Allo.i ("getInstanceId failed " + task.getException ());
                                return;
                            }

                            // Get new Instance ID token
                            final String token = task.getResult ();
                            (new Handler ()).postDelayed (new Runnable ()
                            {
                                @Override
                                public void run ()
                                {
                                    rotateToken (token);
                                }
                            }, 100);
                        }
                    });
        } catch (Exception e) { e.printStackTrace (); }
    }

    private void rotateToken (String token)
    {
        Allo.i ("rotateToken " + getClass ());

        try
        {
            registDevice (token);
            SharedPreferences sharedPreferences = getPreferences (Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit ();
            editor.putString (Allo.CUBE_TOKEN, token); editor.commit ();
        } catch (Exception e) { e.printStackTrace (); }
    }

    private void registDevice (final String token)
    {
        Allo.i ("registDevice " + getClass ());

        try
        {
            new Thread ()
            {
                public void run ()
                {
                    // 필요시 로컬 및 리모트 서버 연동하여 저장함
                    Allo.i ("Check token [" + token + "]");
                }
            }.start ();
        } catch (Exception e) { e.printStackTrace (); }
    }
}