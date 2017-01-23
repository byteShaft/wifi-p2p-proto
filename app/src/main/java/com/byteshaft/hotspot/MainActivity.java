package com.byteshaft.hotspot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonSend = (Button) findViewById(R.id.button_send);
        Button buttonReceive = (Button) findViewById(R.id.button_receive);
        buttonSend.setOnClickListener(this);
        buttonReceive.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_send:
                startActivity(new Intent(getApplicationContext(), PeersListActivity.class));
                break;
            case R.id.button_receive:
                startActivity(new Intent(getApplicationContext(), ReceiveActivity.class));
        }
    }
}
