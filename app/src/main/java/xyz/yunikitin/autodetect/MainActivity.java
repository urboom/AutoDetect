package xyz.yunikitin.autodetect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button registrationButton;
    private Button takePhotoButton;
    private Button openDatabaseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registrationButton = (Button) findViewById(R.id.registration_button);
        takePhotoButton = (Button) findViewById(R.id.take_photo_button);
        openDatabaseButton = (Button) findViewById(R.id.open_DB_button);

        registrationButton.setOnClickListener(this);
        takePhotoButton.setOnClickListener(this);
        openDatabaseButton.setOnClickListener(this);
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
