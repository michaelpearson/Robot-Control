package nz.co.michaelpearon.robotcontrol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.net.Socket;
import java.util.EventListener;

public class MainActivity extends AppCompatActivity {
    private JoystickView leftJoystick;
    private JoystickView rightJoystick;

    private int speed = 0;
    private int port = 0;
    private String ipAddress;

    private TransmitterThread transmitterThread;


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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        speed = sp.getInt("speed", 0);
        port = sp.getInt("port", 0);
        ipAddress = sp.getString("ipAddress", "");
    }

    @Override
    protected void onPause() {
        super.onPause();
        transmitterThread.stopTransmitting();
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


}
