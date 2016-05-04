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

public class MainActivity extends AppCompatActivity implements EventCallback<String> {
    private TransmitterThread transmitterThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        beginConnection();
        super.onResume();
    }

    private void beginConnection() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int speed = (int)(sp.getInt("speed", 1) * 2.55);
        int frequency = sp.getInt("frequency", 1);
        int port = Integer.valueOf(sp.getString("port", "0"));
        String ipAddress = sp.getString("ipAddress", "");

        JoystickView joysticks[] = new JoystickView[] {
                (JoystickView) findViewById(R.id.joystick_left),
                (JoystickView)findViewById(R.id.joystick_right)
        };

        transmitterThread = new TransmitterThread(ipAddress, port, frequency, this, joysticks, speed, 0);
        transmitterThread.start();
    }

    @Override
    protected void onPause() {
        transmitterThread.stopTransmitting();
        transmitterThread = null;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_options_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settings_menu_button:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.action_1_menu_button:
                transmitterThread.setButtons(0b00000001);
                return true;
            case R.id.action_2_menu_button:
                transmitterThread.setButtons(0b00000010);
                return true;
            case R.id.action_3_menu_button:
                transmitterThread.setButtons(0b00000100);
                return true;
            case R.id.action_4_menu_button:
                transmitterThread.setButtons(0b00001000);
                return true;
            default:
                return false;
        }
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