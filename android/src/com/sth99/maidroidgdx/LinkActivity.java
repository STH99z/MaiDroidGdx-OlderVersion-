package com.sth99.maidroidgdx;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class LinkActivity extends Activity {

    ListView listView;

    private static ArrayList<String> toArrayList(String[] array) {
        ArrayList<String> list = new ArrayList<>(array.length);
        for (String s : array)
            list.add(s);
        System.out.println(">>>" + list.size());
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);
        listView = (ListView) findViewById(R.id.LinksListView);
        listView.setAdapter(new ArrayAdapter<String>(
                this,
                R.layout.list_view_item_text_view,
                toArrayList(getResources().getStringArray(R.array.links_address))
        ));
    }
}
