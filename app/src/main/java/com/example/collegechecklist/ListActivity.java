package com.example.collegechecklist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
//import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import android.os.Environment;
import android.os.ParcelFileDescriptor;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

//import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//import static android.os.Environment.getExternalStorageDirectory;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ListActivity extends AppCompatActivity {

    private ArrayList<String> mItems = new ArrayList<>();
    private ArrayList<Boolean> mChecked = new ArrayList<>();

    private SaveInstructions listSaver;
    private RecyclerViewAdapter adapter;

    private static String option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        FloatingActionButton newCheckbox = findViewById(R.id.newCheckboxBtn);
        listSaver = new SaveInstructions();

        Intent intent = getIntent();
        option = intent.getStringExtra(EXTRA_MESSAGE);
        if(option == null) {
            option = "wrong";
        }
        whichChoice(option);

        newCheckbox.setOnClickListener(v -> popupBox().show());

        initRecyclerView();
    }

    //to get the option choice to Saveinstructions
    public static String getOption() {
        return option;
    }

    //to initiate the recycler view from the other class and make it appear
    private void initRecyclerView() {
        //load previous list if not empty
       if(listSaver.loadItems(this) != null) {
            mItems = listSaver.loadItems(this);
            mChecked = listSaver.loadChecks(this);
        }
        RecyclerView recView = findViewById(R.id.recview);
        adapter = new RecyclerViewAdapter(mItems, mChecked, this);
        recView.setAdapter(adapter);
        recView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void whichChoice(String choice) {
        switch (choice) {
            case "bed" :
                setTitle(R.string.bedbtntext);
                break;
            case "supplies" :
                setTitle(R.string.splsbtntext);
                break;
            case "toilet" :
               setTitle(R.string.tltrsbtntext);
                break;
            case "other" :
                setTitle(R.string.otherbtntext);
                break;
            case "devices" :
                setTitle(R.string.devsbtntext);
                break;
            case "personal" :
                setTitle(R.string.persbtntext);
                break;
            case "meds" :
                setTitle(R.string.medbtntext);
                break;
            case "clothes" :
                setTitle(R.string.clthsBtnText);
                break;
            default:
                setTitle("Something went wrong :(");
        }
    }

    //what happens when an item is clicked
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case 121:
                changeItem(1, item.getGroupId()).show();
                return true;
            case 122:
                changeItem(2, item.getGroupId()).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    //add item dialog box
    private Dialog popupBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View vflator = inflater.inflate(R.layout.dialog_additem, null);

        builder.setView(vflator)
                .setPositiveButton(R.string.poptextbtn, (dialogInterface, i) -> {
                    EditText textinput = vflator.findViewById(R.id.textinput);
                    //check if something was typed
                    if(textinput.getText().toString().isEmpty()) {
                        dialogInterface.dismiss();
                    } else {
                        addItem(textinput.getText().toString());
                        Toast toast = Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                })
                .setNegativeButton(R.string.canceltxt, (dialogInterface, i) -> dialogInterface.dismiss());

        return builder.create();
    }

    private void addItem(String txt) {
        mItems.add(txt);
        mChecked.add(false);
        adapter.notifyDataSetChanged();
        listSaver.save(mItems, mChecked, getApplicationContext());
    }

    //for deleting and renaming items
    private Dialog changeItem(int option, final int focusedItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View vflator;

        if(option == 1) {
            vflator = inflater.inflate(R.layout.dialog_rename, null);
            final EditText textinput = vflator.findViewById(R.id.newname);
            textinput.setText(mItems.get(focusedItem));

            builder.setView(vflator)
                    .setPositiveButton(R.string.renamebtntext, (dialogInterface, i) -> {
                        adapter.renameItem(focusedItem, textinput.getText().toString());
                        listSaver.save(mItems, mChecked, getApplicationContext());
                        Toast toastr = Toast.makeText(getApplicationContext(), "Renamed", Toast.LENGTH_SHORT);
                        toastr.show();
                    })
                    .setNegativeButton(R.string.canceltxt, (dialogInterface, i) -> dialogInterface.dismiss());
        } else if(option == 2) {
            vflator = inflater.inflate(R.layout.dialog_delete, null);

            builder.setView(vflator)
                    .setPositiveButton(R.string.deletebtntext, (dialogInterface, i) -> {
                        adapter.deleteItem(focusedItem);
                        listSaver.save(mItems, mChecked, getApplicationContext());
                        Toast toastd = Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT);
                        toastd.show();
                    })
                    .setNegativeButton(R.string.canceltxt, (dialogInterface, i) -> dialogInterface.dismiss());
        }

        return builder.create();
    }

    //to add the button to the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    //to set the function of the buttons in the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearListBtn:
                clearSaveDialog(3).show();
                return true;
            case R.id.expListBtn:
                clearSaveDialog(4).show();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public Dialog clearSaveDialog(int option) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View vflator;


        if(option == 3) {
            vflator = inflater.inflate(R.layout.dialog_clear, null);
            builder.setView(vflator)
                    .setPositiveButton(R.string.clear_list, (dialogInterface, i) -> {
                        mItems.clear();
                        mChecked.clear();
                        adapter.notifyDataSetChanged();
                        listSaver.save(mItems, mChecked, getApplicationContext());
                        Toast toastc = Toast.makeText(getApplicationContext(), "Cleared", Toast.LENGTH_SHORT);
                        toastc.show();
                    })
                    .setNegativeButton(R.string.canceltxt, (dialogInterface, i) -> dialogInterface.dismiss());
        } else {
            vflator = inflater.inflate(R.layout.dialog_save, null);
            builder.setView(vflator)
                    .setPositiveButton(R.string.export_list, (dialogInterface, i) -> {
                        createFile();
                        Toast toaste = Toast.makeText(getApplicationContext(), " List Downloaded", Toast.LENGTH_SHORT);
                        toaste.show();
                    })
                    .setNegativeButton(R.string.canceltxt, (dialogInterface, i) -> dialogInterface.dismiss());
        }

        return builder.create(); //must have .show() oon the function call for it to work
    }

    // Unique request code.
    private static final int WRITE_REQUEST_CODE = 43;

    //to create the document
    private void createFile() {
        String fileName = getTitle().toString() + ".txt";
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, WRITE_REQUEST_CODE);
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
            stream.write((getTitle().toString() + " List: \n\n").getBytes());
            if(mItems != null) {
                for (String s : mItems) {
                    stream.write(s.getBytes());
                    if (mChecked.get(mItems.indexOf(s))) {
                        stream.write(" $".getBytes());
                    }
                    stream.write("\n".getBytes());
                }
            }
            // Let the document provider know you're done by closing the stream.
            stream.close();
            pfd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}