package xyz.yunikitin.autodetect.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;

import xyz.yunikitin.autodetect.Database.DatabaseItem;
import xyz.yunikitin.autodetect.R;

public class ShowDBActivity extends AppCompatActivity {

    private static final String TAG = "MyApp";
    private String number;
    private TextView mBrandAuto,mEvent, mColorAuto,mCity, mPhone, mEmail;
    private String brand, color, city, email, phone, event;
    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<DatabaseItem> mDatabaseTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_db);
        Log.i(TAG, "Read object with ID ");

        try {
            mClient = new MobileServiceClient(
                    "https://platedetectapp.azurewebsites.net",
                    this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        mDatabaseTable = mClient.getTable(DatabaseItem.class);

        TextView mNumberPlate = (TextView) findViewById(R.id.textNumber);
        mBrandAuto = (TextView) findViewById(R.id.textBrand);
        mColorAuto = (TextView) findViewById(R.id.textColor);
        mCity = (TextView) findViewById(R.id.textCity);
        mEmail = (TextView) findViewById(R.id.textEmail);
        mPhone = (TextView) findViewById(R.id.textPhone);
        mEvent = (TextView) findViewById(R.id.textEvent);

        Intent intent = getIntent();
        number = intent.getStringExtra("position");
        mNumberPlate.setText(number);
        getItemDB();
    }

    public void onClick(View view) {
        getItemDB();
        mBrandAuto.setText(brand);
        mColorAuto.setText(color);
        mCity.setText(city);
        mEmail.setText(email);
        mPhone.setText(phone);
        mEvent.setText(event);
    }

    private void getItemDB(){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final MobileServiceList<DatabaseItem> result = mDatabaseTable.where().field("platenumber").eq(number).execute().get();
                    Log.i(TAG, "Read object with ID " + result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                    for (DatabaseItem item : result) {
                            Log.i(TAG, "Read object with ID " + item.getBrandAuto());
                            brand = item.getBrandAuto();
                            Log.i(TAG, "Read object with ID " + item.getCity());
                            color = item.getColorAuto();
                            Log.i(TAG, "Read object with ID " + item.getPhone());
                            city = item.getCity();
                            email = item.getEmail();
                            phone = item.getPhone();
                            event = item.getEvent();
                    }
                            }
                    });
                } catch (Exception exception) {
                    // createAndShowDialog(exception, "Error");
                }
                return null;
            }
        };
        runAsyncTask(task);
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }
}
