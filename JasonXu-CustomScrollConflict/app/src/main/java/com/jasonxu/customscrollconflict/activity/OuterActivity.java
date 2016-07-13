package com.jasonxu.customscrollconflict.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jasonxu.customscrollconflict.MyUtils;
import com.jasonxu.customscrollconflict.R;
import com.jasonxu.customscrollconflict.customview.HorizontalScrollViewEx;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by t_xuz on 7/7/16.
 */
public class OuterActivity extends AppCompatActivity{
    private HorizontalScrollViewEx mScrollView;
    private List<String> datas ;
    private int screenWidth;
    private int screenHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.outter_layout);

        initData();

        initViews();
    }

    private void initData(){
        datas = new ArrayList<>();
        for (int i=0;i<50;i++){
            datas.add("name : "+i);
        }
        screenWidth = MyUtils.getScreenMetrics(this).widthPixels;
        screenHeight = MyUtils.getScreenMetrics(this).heightPixels;
    }

    private void initViews(){
        LayoutInflater inflater = getLayoutInflater();
        mScrollView = (HorizontalScrollViewEx)this.findViewById(R.id.container);
        for (int i=0;i<3;i++){
            ViewGroup layout = (ViewGroup)inflater.inflate(R.layout.container_layout,mScrollView,false);
            layout.getLayoutParams().width = screenWidth;
            TextView textView = (TextView)layout.findViewById(R.id.title);
            textView.setText("page : " + (i+1));
            layout.setBackgroundColor(Color.rgb(255/(i+1),0,255/(i+1)));
            createList(layout);
            mScrollView.addView(layout);
        }
    }

    private void createList(ViewGroup layout){
        ListView listView = (ListView)layout.findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.content_list_item,R.id.name,datas);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(OuterActivity.this,"click item : "+position,Toast.LENGTH_SHORT).show();
            }
        });

    }
}
