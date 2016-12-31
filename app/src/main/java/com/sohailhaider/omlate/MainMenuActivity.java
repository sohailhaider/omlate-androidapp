package com.sohailhaider.omlate;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainMenuActivity extends AppCompatActivity {
    ArrayList<Data> listItems=new ArrayList<Data>();
    ArrayAdapter<Data> adapter;
    private ListView list;
    String[] data=new String[]{"1", "2", "3"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        list = (ListView) findViewById(R.id.listView);
        adapter=new ArrayAdapter<Data>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listItems);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id)
            {
                Data  item = (Data) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), item.link, Toast.LENGTH_SHORT).show();
            }
        });
        Data d1 = new Data();
        d1.name = "d1";
        d1.link = "1";
        Data d2 = new Data();
        d2.name = "d2";
        d2.link = "2";
        Data d3 = new Data();
        d3.name = "d3";
        d3.link = "3";

        listItems.add(d1);
        adapter.notifyDataSetChanged();
        listItems.add(d2);
        adapter.notifyDataSetChanged();
        listItems.add(d3);
        adapter.notifyDataSetChanged();
    }
}

class Data {
    String name;
    String link;

    @Override
    public String toString() {
        return name;
    }
}
