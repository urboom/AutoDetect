package xyz.yunikitin.autodetect;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncTable;

import java.net.MalformedURLException;

public class ShowDBActivity extends AppCompatActivity {

    private static final String TAG = "MyLog";
    private MobileServiceClient mClient;
    private MobileServiceTable<DatabaseItem> mDatabaseItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_db);

        try {
            mClient = new MobileServiceClient(
                    "https://platedetectapp.azurewebsites.net",
                    this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        TextView txtProduct = (TextView) findViewById(R.id.product_label);
        mDatabaseItem = mClient.getTable("DatabaseItem", DatabaseItem.class);

        Intent intent = getIntent();
        String number = intent.getStringExtra("position");

            txtProduct.setText(number);

    }


    private void getDBitem(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final MobileServiceList<DatabaseItem> result =
                            mDatabaseItem.where().field("platenumber").eq("СВ 6374 АС").execute().get();
                    for (DatabaseItem item : result) {
                        Toast.makeText(ShowDBActivity.this, "Read object with ID " + item.getId(), Toast.LENGTH_SHORT).show();
                       Log.i(TAG, "Read object with ID " + item.getId());
                    }
                } catch (Exception exception) {
                   // createAndShowDialog(exception, "Error");
                }
                return null;
            }
        }.execute();
    }
}
