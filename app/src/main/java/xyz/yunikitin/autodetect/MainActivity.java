package xyz.yunikitin.autodetect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.os.AsyncTask;
import com.google.android.gms.gcm.*;
import com.microsoft.windowsazure.messaging.*;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import android.widget.Toast;

import xyz.yunikitin.autodetect.Activity.DatabaseActivity;
import xyz.yunikitin.autodetect.Activity.RegistrationActivity;
import xyz.yunikitin.autodetect.Activity.TakePhotoActivity;

public class MainActivity extends Activity implements View.OnClickListener {


    private String SENDER_ID = "325250664040";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;
    private String HubName = "boomnotificationhub";
    private String HubListenConnectionString = "Endpoint=sb://boomnotificationnamespace.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=VB1JlZOjqTrIojvxMRoefx/5wMTmA7752pvmqvXIa5o=";
    private static Boolean isVisible = false;

    private Button registrationButton;
    private Button takePhotoButton;
    private Button openDatabaseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyHandler.mainActivity = this;
        NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);
        gcm = GoogleCloudMessaging.getInstance(this);
        hub = new NotificationHub(HubName, HubListenConnectionString, this);
        registerWithNotificationHubs();

        registrationButton = (Button) findViewById(R.id.registration_button);
        takePhotoButton = (Button) findViewById(R.id.take_photo_button);
        openDatabaseButton = (Button) findViewById(R.id.open_DB_button);

        registrationButton.setOnClickListener(this);
        takePhotoButton.setOnClickListener(this);
        openDatabaseButton.setOnClickListener(this);
    }

    @SuppressWarnings("unchecked")
    private void registerWithNotificationHubs() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    String regid = gcm.register(SENDER_ID);
                    ToastNotify("Registered Successfully - RegId : " +
                            hub.register(regid).getRegistrationId());
                } catch (Exception e) {
                    ToastNotify("Registration Exception Message - " + e.getMessage());
                    return e;
                }
                return null;
            }
        }.execute(null, null, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    public void ToastNotify(final String notificationMessage)
    {
        if (isVisible == true)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, notificationMessage, Toast.LENGTH_LONG).show();
                }
            });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.registration_button:
                Intent intent1 = new Intent(this, RegistrationActivity.class);
                startActivity(intent1);
                break;
            case R.id.take_photo_button:
                Intent intent2 = new Intent(this, TakePhotoActivity.class);
                startActivity(intent2);
                break;
            case R.id.open_DB_button:
                Intent intent3 = new Intent(this, DatabaseActivity.class);
                startActivity(intent3);
                break;
        }
    }
}
