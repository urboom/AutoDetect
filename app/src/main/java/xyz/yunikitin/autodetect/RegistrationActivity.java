package xyz.yunikitin.autodetect;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

public class RegistrationActivity extends AppCompatActivity {


    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<DatabaseItem> mDatabaseTable;

    /**
     * Adapter to sync the items list with the view
     */
    private DatabaseItemAdapter mAdapter;

    //Offline Sync
    /**
     * Mobile Service Table used to access and Sync data
     */
    //private MobileServiceSyncTable<ToDoItem> mToDoTable;

    /**
     * Adapter to sync the items list with the view
     */
   // private ToDoItemAdapter mAdapter;

    /**
     * EditText containing the "New To Do" text
     */
    private EditText mBrandAuto, mEvent;
    private EditText mNumberPlate;
    private EditText mColorAuto;
    private EditText mCity;
    private EditText mPhone;
    private EditText mEmail;

    /**
     * Initializes the activity
     */

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

            // Offline Sync
            //mToDoTable = mClient.getSyncTable("ToDoItem", ToDoItem.class);

            //Init local storage
            //initLocalStore().get();

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

    /*private AsyncTask<Void, Void, Void> initLocalStore() throws MobileServiceLocalStoreException, ExecutionException, InterruptedException {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
             @Override
            protected Void doInBackground(Void... params) {
                try {
                    MobileServiceSyncContext syncContext = mClient.getSyncContext();

                    if (syncContext.isInitialized())
                        return null;

                    SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), "OfflineStore", null, 1);

                    Map<String, ColumnDataType> tableDefinition = new HashMap<>();
                    tableDefinition.put("id", ColumnDataType.String);
                    tableDefinition.put("text", ColumnDataType.String);
                    tableDefinition.put("complete", ColumnDataType.Boolean);
                    tableDefinition.put("plateNumber", ColumnDataType.String);
                    tableDefinition.put("brandAuto", ColumnDataType.String);

                    localStore.defineTable("DatabaseItem", tableDefinition);

                    SimpleSyncHandler handler = new SimpleSyncHandler();
                    syncContext.initialize(localStore, handler).get();

                } catch (final Exception e) {
                   createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        return runAsyncTask(task);
    }*/
    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }
}
