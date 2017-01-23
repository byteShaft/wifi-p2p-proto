package com.byteshaft.hotspot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class ReceiveActivity extends AppCompatActivity {
    private P2PBroadcastReceiver mReceiver;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    private IntentFilter getWifiP2PIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return filter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new P2PBroadcastReceiver();
        registerReceiver(mReceiver, getWifiP2PIntentFilter());
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int i) {

            }
        });
    }

    class P2PBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION):
                    System.out.println("Wifi P2P State Changed");
                    break;
                case (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION):
                    mManager.requestPeers(mChannel, peersListListener);
                    System.out.println("Wifi P2P Peers Changed");
                    break;
                case (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION):
                    System.out.println("Wifi P2P Connection Changed");
                    break;
                case (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION):
                    System.out.println("Wifi P2P Device Changed");
                    break;
            }
        }
    }

    private WifiP2pManager.PeerListListener peersListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
        }
    };
}
