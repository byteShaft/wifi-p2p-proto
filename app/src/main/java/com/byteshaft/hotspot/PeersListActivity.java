package com.byteshaft.hotspot;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PeersListActivity extends ListActivity implements ConnectionInfoListener {
    private ProgressBar mDiscoveryProgressBar;

    private List<WifiP2pDevice> peers = new ArrayList<>();
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

    private PeerListListener peersListListener = new PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
            if (!refreshedPeers.equals(peers)) {
                peers.clear();
                peers.addAll(refreshedPeers);
            }
            mDiscoveryProgressBar.setVisibility(View.GONE);
            if (peers.size() == 0) {
                getListView().setVisibility(View.GONE);
                Log.d("TAG", "No devices found");
            } else {
                getListView().setVisibility(View.VISIBLE);
                PeersListAdapter peersListAdapter = new PeersListAdapter(
                        getApplicationContext(), peers);
                setListAdapter(peersListAdapter);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peers_list);
        mDiscoveryProgressBar = (ProgressBar) findViewById(R.id.progress_bar_searching);
        mDiscoveryProgressBar.setVisibility(View.VISIBLE);
        mDiscoveryProgressBar.setProgress(0);
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

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        System.out.println(wifiP2pInfo.groupFormed);
        System.out.println(wifiP2pInfo.groupOwnerAddress);
        System.out.println(wifiP2pInfo.isGroupOwner);
    }

    class P2PBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION):
                    switch (intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)) {
                        case WifiP2pManager.WIFI_P2P_STATE_ENABLED:
                            break;
                        default:
                            break;
                    }
                    System.out.println("Wifi P2P State Changed");
                    break;
                case (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION):
                    mManager.requestPeers(mChannel, peersListListener);
                    System.out.println("Wifi P2P Peers Changed");
                    break;
                case (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION):
                    NetworkInfo networkInfo = intent
                            .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                    if (networkInfo.isConnected()) {
                        mManager.requestConnectionInfo(mChannel, PeersListActivity.this);
                    } else {
                        System.out.println("Failed to connnect");
                    }
                    System.out.println("Wifi P2P Connection Changed");
                    break;
                case (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION):
                    System.out.println("Wifi P2P Device Changed");
                    break;
            }
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        WifiP2pDevice device = (WifiP2pDevice) l.getAdapter().getItem(position);
        connect(device);
    }

    private void connect(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
