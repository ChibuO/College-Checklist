package com.example.collegechecklist;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SaveInstructions extends AppCompatActivity {
    private String itemFile;
    private String checkFile;

    //to choose which sharedPref list to show
    private void whichChoice(String choice) {
        switch (choice) {
            case "bed" :
                itemFile = "bItems";
                checkFile = "bChecked";
                break;
            case "supplies" :
                itemFile = "sItems";
                checkFile = "sChecked";
                break;
            case "toilet" :
                itemFile = "tItems";
                checkFile = "tChecked";
                break;
            case "other" :
                itemFile = "oItems";
                checkFile = "oChecked";
                break;
            case "devices" :
                itemFile = "dItems";
                checkFile = "dChecked";
                break;
            case "personal" :
                itemFile = "pItems";
                checkFile = "pChecked";
                break;
            case "meds" :
                itemFile = "mItems";
                checkFile = "mChecked";
                break;
            case "clothes" :
                itemFile = "aItems";
                checkFile = "aChecked";
                break;
            default:
                itemFile = "defaultItems";
                checkFile = "defaultChecked";
        }
    }

    public void save(ArrayList<String> ilist, ArrayList<Boolean> clist, Context cxt) {
        whichChoice(ListActivity.getOption());
        ArrayList<String> itemsList;
        ArrayList<Boolean> checkedList;
        itemsList = ilist;
        checkedList = clist;

        SharedPreferences sharedPreferences = cxt.getSharedPreferences("allLists", cxt.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json1 = gson.toJson(itemsList);
        String json2 = gson.toJson(checkedList);
        editor.putString(itemFile, json1);
        editor.putString(checkFile, json2);
        editor.apply();
    }

    public ArrayList<String> loadItems(Context cxt) {
            whichChoice(ListActivity.getOption());
            ArrayList<String> loadedList;
            SharedPreferences sharedPreferences = cxt.getSharedPreferences("allLists", cxt.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferences.getString(itemFile, "");
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            loadedList = gson.fromJson(json, type);

            return loadedList;
    }

    public ArrayList<Boolean> loadChecks(Context cxt) {
        whichChoice(ListActivity.getOption());
        ArrayList<Boolean> loadedList;
        SharedPreferences sharedPreferences = cxt.getSharedPreferences("allLists", cxt.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(checkFile, "");
        Type type = new TypeToken<ArrayList<Boolean>>() {}.getType();
        loadedList = gson.fromJson(json, type);

        return loadedList;
    }
}
