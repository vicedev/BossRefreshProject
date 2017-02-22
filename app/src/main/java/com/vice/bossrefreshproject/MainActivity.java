package com.vice.bossrefreshproject;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.vice.bossrefreshlibrary.BossRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private BossRefreshLayout mBossRefresh;
    private ListView lv;
    private List<String> data;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBossRefresh = (BossRefreshLayout) findViewById(R.id.boss_refresh);
        lv = (ListView) findViewById(R.id.lv);

        init();
    }

    private void init() {
        loadData();
        adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, data);
        lv.setAdapter(adapter);
        mBossRefresh.setOnRefreshingListener(new BossRefreshLayout.onRefreshingListener() {
            @Override
            public void onRefreshing() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                        SystemClock.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                mBossRefresh.setComplete();
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void loadData() {
        if (data==null){
            data=new ArrayList<>();
        }else{
            data.clear();
        }
        for (int i=0;i<60;i++){
            data.add("data"+new Random().nextInt(100));
        }
    }
}
