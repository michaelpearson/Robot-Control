package nz.co.michaelpearson.robotcontrol;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements EventCallback<String> {
    private JoystickView leftJoystick;
    private JoystickView rightJoystick;

    private TransmitterThread transmitterThread;
    private DataReference data = new DataReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        leftJoystick = (JoystickView)findViewById(R.id.joystick_left);
        rightJoystick = (JoystickView)findViewById(R.id.joystick_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        beginConnection();
    }

    private void beginConnection() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int speed = sp.getInt("speed", 1);
        data.setSpeed((byte)(speed * 2.55));
        int port = Integer.valueOf(sp.getString("port", "0"));
        int frequency = sp.getInt("frequency", 1);
        String ipAddress = sp.getString("ipAddress", "");
        transmitterThread = new TransmitterThread(ipAddress, port, frequency, data, this, new Handler());
        transmitterThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        transmitterThread.stopTransmitting();
        transmitterThread = null;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        leftJoystick = null;
        rightJoystick = null;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_options_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
        return true;
    }

    @Override
    public void callback(String s) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(s)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(i);
                    }
                })
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        beginConnection();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        beginConnection();
                    }
                })
                .create()
                .show();
    }
}