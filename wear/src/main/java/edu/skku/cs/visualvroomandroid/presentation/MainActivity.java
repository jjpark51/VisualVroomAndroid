/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package edu.skku.cs.visualvroomandroid.presentation;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import edu.skku.cs.visualvroomandroid.R;

public class MainActivity extends AppCompatActivity implements MessageClient.OnMessageReceivedListener {

    private static final String TAG = "WearMainActivity";
    private static final String VIBRATION_PATH = "/vibration";

    private TextView statusTextView;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.status_text);

        // Get vibrator service - use the appropriate method based on API level
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            vibrator = getSystemService(Vibrator.class);
        } else {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the message listener
        Wearable.getMessageClient(this).addListener(this);
        statusTextView.setText("Ready to receive vibration commands");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the message listener
        Wearable.getMessageClient(this).removeListener(this);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(VIBRATION_PATH)) {
            Log.d(TAG, "Vibration command received");

            // Update UI
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    statusTextView.setText("Vibrating...");
                }
            });

            // Vibrate the watch
            if (vibrator.hasVibrator()) {
                // Create a vibration pattern suitable for notifications
                // Vibrate for 500ms, pause for 100ms, vibrate for 500ms
                long[] pattern = {0, 500, 100, 500};

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
                } else {
                    // Deprecated in API 26
                    vibrator.vibrate(pattern, -1);
                }
            }

            // Reset status text after vibration
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Wait for vibration to complete before updating text
                    statusTextView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            statusTextView.setText("Ready to receive vibration commands");
                        }
                    }, 1500); // Slightly longer than the total vibration time
                }
            });
        }
    }
}