package xyz.yunikitin.autodetect.Activity;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URLEncoder;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

import xyz.yunikitin.autodetect.Database.DatabaseItem;
import xyz.yunikitin.autodetect.R;


public class RegistrationActivity extends AppCompatActivity {

    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<DatabaseItem> mDatabaseTable;

    private EditText mBrandAuto, mEvent;
    private EditText mNumberPlate;
    private EditText mColorAuto;
    private EditText mCity;
    private EditText mPhone;
    private EditText mEmail;

    private String HubEndpoint = null;
    private String HubSasKeyName = null;
    private String HubSasKeyValue = null;
    private String HubFullAccess = "Endpoint=sb://boomnotificationnamespace.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=f6bznYNdnhcotj26coOzoLaV8ke78H4wjx2k9UCgsA8=";

    private String HubName = "boomnotificationhub";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://platedetectapp.azurewebsites.net",
                    this);

            // Get the Mobile Service Table instance to use

            mDatabaseTable = mClient.getTable(DatabaseItem.class);

            mNumberPlate = (EditText) findViewById(R.id.editPlateNumber);
            mBrandAuto = (EditText) findViewById(R.id.editBrand);
            mColorAuto = (EditText) findViewById(R.id.editColorAuto);
            mCity = (EditText) findViewById(R.id.editCityText);
            mPhone = (EditText) findViewById(R.id.editPhone);
            mEmail = (EditText) findViewById(R.id.editEmail);
            mEvent = (EditText) findViewById(R.id.editEvent);

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e) {
            createAndShowDialog(e, "Error");
        }
    }

    private void ParseConnectionString(String connectionString)
    {
        String[] parts = connectionString.split(";");
        if (parts.length != 3)
            throw new RuntimeException("Error parsing connection string: "
                    + connectionString);

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].startsWith("Endpoint")) {
                this.HubEndpoint = "https" + parts[i].substring(11);
            } else if (parts[i].startsWith("SharedAccessKeyName")) {
                this.HubSasKeyName = parts[i].substring(20);
            } else if (parts[i].startsWith("SharedAccessKey")) {
                this.HubSasKeyValue = parts[i].substring(16);
            }
        }
    }

    private String generateSasToken(String uri) {

        String targetUri;
        try {
            targetUri = URLEncoder
                    .encode(uri.toString().toLowerCase(), "UTF-8")
                    .toLowerCase();

            long expiresOnDate = System.currentTimeMillis();
            int expiresInMins = 60; // 1 hour
            expiresOnDate += expiresInMins * 60 * 1000;
            long expires = expiresOnDate / 1000;
            String toSign = targetUri + "\n" + expires;

            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = HubSasKeyValue.getBytes("UTF-8");
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA256");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(toSign.getBytes("UTF-8"));

            // Using android.util.Base64 for Android Studio instead of
            // Apache commons codec
            String signature = URLEncoder.encode(
                    Base64.encodeToString(rawHmac, Base64.NO_WRAP).toString(), "UTF-8");

            // Construct authorization string
            String token = "SharedAccessSignature sr=" + targetUri + "&sig="
                    + signature + "&se=" + expires + "&skn=" + HubSasKeyName;
            return token;
        } catch (Exception e) {
            //DialogNotify("Exception Generating SaS",e.getMessage().toString());
        }

        return null;
    }

    public void addItem(View view) {
        if (mClient == null) {
            return;
        }
        final Toast toast = Toast.makeText(getApplicationContext(), "Номер успішно зареєстровано", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0,0);
        // Create a new item
        final DatabaseItem item = new DatabaseItem();

        item.setNumberPlate(mNumberPlate.getText().toString());
        item.setBrandAuto(mBrandAuto.getText().toString());
        item.setColorAuto(mColorAuto.getText().toString());
        item.setCity(mCity.getText().toString());
        item.setPhone(mPhone.getText().toString());
        item.setEmail(mEmail.getText().toString());
        item.setEvent(mEvent.getText().toString());

        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                  addItemInTable(item);
                    toast.show();
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };
        runAsyncTask(task);

        EditText notificationText = (EditText) findViewById(R.id.editPlateNumber);
        final String json = "{\"data\":{\"message\":\"" + notificationText.getText().toString() + "\"}}";

        new Thread()
        {
            public void run()
            {
                try
                {
                    HttpClient client = new DefaultHttpClient();

                    // Based on reference documentation...
                    // http://msdn.microsoft.com/library/azure/dn223273.aspx
                    ParseConnectionString(HubFullAccess);
                    String url = HubEndpoint + HubName + "/messages/?api-version=2015-01";
                    HttpPost post = new HttpPost(url);

                    // Authenticate the POST request with the SaS token
                    post.setHeader("Authorization", generateSasToken(url));

                    // JSON content for GCM
                    post.setHeader("Content-Type", "application/json;charset=utf-8");

                    // Notification format should be GCM
                    post.setHeader("ServiceBusNotification-Format", "gcm");
                    post.setEntity(new StringEntity(json));

                    HttpResponse response = client.execute(post);
                }
                catch(Exception e)
                {
                    // DialogNotify("Exception",e.getMessage().toString());
                }
            }
        }.start();

        mNumberPlate.setText("");
        mBrandAuto.setText("");
        mColorAuto.setText("");
        mCity.setText("");
        mPhone.setText("");
        mEmail.setText("");
        mEvent.setText("");
    }

    /**
     * Add an item to the Mobile Service Table
     *
     * @param item
     *            The item to Add
     */
    public DatabaseItem addItemInTable(DatabaseItem item) throws ExecutionException, InterruptedException {
        return mDatabaseTable.insert(item).get();
    }

    /**
     * Run an ASync task on the corresponding executor
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }
}
