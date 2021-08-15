package com.example.collegechecklist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
//import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //buttons
        Button bedButton = findViewById(R.id.bedButton);
        Button toiletButton = findViewById(R.id.toiletButton);
        Button suppliesButton = findViewById(R.id.suppliesButton);
        Button devicesButton = findViewById(R.id.devicesButton);
        Button personalButton = findViewById(R.id.personalButton);
        Button otherButton = findViewById(R.id.otherButton);
        Button medButton = findViewById(R.id.medButton);
        Button clothesButton = findViewById(R.id.clothesButton);

        bedButton.setOnClickListener(view -> {
            //need intent to go to new activity
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            intent.putExtra(EXTRA_MESSAGE, "bed");
            startActivity(intent);
        });

        toiletButton.setOnClickListener(view -> {
            //need intent to go to new activity
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            intent.putExtra(EXTRA_MESSAGE, "toilet");
            startActivity(intent);
        });

        suppliesButton.setOnClickListener(view -> {
            //need intent to go to new activity
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            intent.putExtra(EXTRA_MESSAGE, "supplies");
            startActivity(intent);
        });

        devicesButton.setOnClickListener(view -> {
            //need intent to go to new activity
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            intent.putExtra(EXTRA_MESSAGE, "devices");
            startActivity(intent);
        });

        personalButton.setOnClickListener(view -> {
            //need intent to go to new activity
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            intent.putExtra(EXTRA_MESSAGE, "personal");
            startActivity(intent);
        });

        otherButton.setOnClickListener(view -> {
            //need intent to go to new activity
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            intent.putExtra(EXTRA_MESSAGE, "other");
            startActivity(intent);
        });

        medButton.setOnClickListener(view -> {
            //need intent to go to new activity
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            intent.putExtra(EXTRA_MESSAGE, "meds");
            startActivity(intent);
        });

        clothesButton.setOnClickListener(view -> {
            //need intent to go to new activity
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            intent.putExtra(EXTRA_MESSAGE, "clothes");
            startActivity(intent);
        });
    }

    //to add the button to the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_export, menu);
        return true;
    }

    //to set the function of the buttons in the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.expListBtn) {
            saveDialog().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static final int WRITE_REQUEST_CODE = 44;

    private Dialog saveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View vflator = inflater.inflate(R.layout.dialog_save, null);

        builder.setView(vflator)
                .setPositiveButton(R.string.export_list, (dialogInterface, i) -> {
                    String fileName = getTitle().toString() + ".txt";
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

                    // Filter to only show results that can be "opened", such as a file (as opposed to a list of contacts or timezones).
                    intent.addCategory(Intent.CATEGORY_OPENABLE);

                    // Create a file with the requested MIME type.
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TITLE, fileName);
                    startActivityForResult(intent, WRITE_REQUEST_CODE);
                })
                .setNegativeButton(R.string.canceltxt, (dialogInterface, i) -> dialogInterface.dismiss());

        return builder.create();
    }

    //to get the document so i can edit it
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                alterDocument(uri);
            }
        }
    }

    //to add the list to the document
    private void alterDocument(Uri uri) {
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
            assert pfd != null;
            FileOutputStream stream = new FileOutputStream(pfd.getFileDescriptor());
            buildList(stream, "bItems", "bChecked","Beddings List: \n");
            buildList(stream, "sItems", "sChecked", "Supplies List: \n");
            buildList(stream, "tItems", "tChecked", "Toiletries List: \n");
            buildList(stream, "dItems", "dChecked", "Electronics List: \n");
            buildList(stream, "pItems", "pChecked", "Personal List: \n");
            buildList(stream, "mItems", "mChecked", "Medical List: \n");
            buildList(stream, "aItems", "aChecked", "Apparel List: \n");
            buildList(stream, "oItems", "oChecked", "Miscellaneous List: \n");
            // Let the document provider know you're done by closing the stream.
            stream.close();
            pfd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildList(FileOutputStream stream, String items, String checked, String title) throws IOException {
        stream.write(title.getBytes());
        ArrayList<String> listItems;
        ArrayList<Boolean> listChecked;
        SharedPreferences sharedPreferences = getSharedPreferences("allLists", MODE_PRIVATE);
        Gson gson_i = new Gson();
        Gson gson_c = new Gson();
        String json_i = sharedPreferences.getString(items, "");
        String json_c = sharedPreferences.getString(checked, "");
        Type type_i = new TypeToken<ArrayList<String>>() {}.getType();
        Type type_c = new TypeToken<ArrayList<Boolean>>() {}.getType();
        listItems = gson_i.fromJson(json_i, type_i);
        listChecked = gson_c.fromJson(json_c, type_c);
        if(listItems != null) {
            for (String s : listItems) {
                stream.write(s.getBytes());
            if (listChecked.get(listItems.indexOf(s))) {
                stream.write(" $".getBytes());
            }
                stream.write("\n".getBytes());
            }
        }
        stream.write("\n\n".getBytes());
    }

}
