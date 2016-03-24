package xyz.yunikitin.autodetect;


import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;


public class DatabaseActivity extends AppCompatActivity {
    
    private MobileServiceClient mClient;
    private MobileServiceTable<DatabaseItem> mDatabaseTable;
    private DatabaseItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
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

            mAdapter = new DatabaseItemAdapter(this, R.layout.row_list_database);
            ListView listViewDatabase = (ListView) findViewById(R.id.listViewDatabase);
            listViewDatabase.setAdapter(mAdapter);
            getItem();

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e) {
            createAndShowDialog(e, "Error");
        }
    }

    public void getItem(){

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<DatabaseItem> results = refreshItemsFromMobileServiceTable();

                    //Offline Sync
                    //final List<ToDoItem> results = refreshItemsFromMobileServiceTableSyncTable();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (DatabaseItem item : results) {
                                mAdapter.add(item);
                            }
                        }
                    });
                } catch (final Exception e){
                    createAndShowDialogFromTask(e, "Error");
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

    private List<DatabaseItem> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mDatabaseTable.where().field("complete").eq(val(false)).execute().get();
    }

    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
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
}
