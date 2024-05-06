package it.magical.slimloader;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private void openSettings(String setting) {
        startActivity(
            new Intent(setting,
                Uri.parse(
                    String.format(
                        "package:%s",
                        getPackageName()
                    )
                )
            )
        );
    }

    private void showDialog(String title, String text, Runnable action) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    action.run();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text)
                .setTitle(title)
                .setPositiveButton("OK", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show();
    }

    private void checkPermissions() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);

        if (!prefs.getBoolean("asked_file_access", false) || !Environment.isExternalStorageManager()) {
            showDialog("File access", "Please allow this app to access the device's files", () -> {
                openSettings(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                prefs.edit().putBoolean("asked_file_access", true).apply();
            });
        }
        if (!prefs.getBoolean("asked_install_access", false) || !getPackageManager().canRequestPackageInstalls()) {
            showDialog("Unknown app sources", "Please allow this app to install other applications", () -> {
                openSettings(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                prefs.edit().putBoolean("asked_install_access", true).apply();
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkPermissions();
    }
}