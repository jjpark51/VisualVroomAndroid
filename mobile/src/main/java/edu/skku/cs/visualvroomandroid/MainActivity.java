package edu.skku.cs.visualvroomandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String VIBRATION_PATH = "/vibration";
    private Button vibrationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrationButton = findViewById(R.id.vibration_button);
        vibrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendVibrationRequest();
                    }
                }).start();
            }
        });
    }

    private void sendVibrationRequest() {
        try {
            // Get all connected devices
            Task<List<Node>> nodesTask = Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            List<Node> nodes = Tasks.await(nodesTask);

            for (Node node : nodes) {
                // Send message to each connected device
                Task<Integer> sendTask = Wearable.getMessageClient(getApplicationContext())
                    .sendMessage(node.getId(), VIBRATION_PATH, new byte[0]);

                // Wait for the task to complete
                Tasks.await(sendTask);

                // Show feedback on UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
                            "Vibration command sent to watch",
                            Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (ExecutionException | InterruptedException e) {
        e.printStackTrace();

        // Show error on UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,
                    "Failed to send vibration command: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
    }
}