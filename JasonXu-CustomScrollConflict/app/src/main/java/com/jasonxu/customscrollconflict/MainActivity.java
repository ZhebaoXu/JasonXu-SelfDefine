package com.jasonxu.customscrollconflict;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jasonxu.customscrollconflict.activity.OuterActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnInner,btnOuter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnInner = (Button)this.findViewById(R.id.btn_inner);
        btnOuter = (Button)this.findViewById(R.id.btn_out);
        btnInner.setOnClickListener(this);
        btnOuter.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.btn_out:
                intent = new Intent(MainActivity.this,OuterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_inner:

                break;
        }
    }
}
