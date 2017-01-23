package com.byteshaft.hotspot;

import android.content.Context;
import android.graphics.Typeface;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

class PeersListAdapter extends BaseAdapter {
    private Context mContext;
    private List<WifiP2pDevice> mPeers;

    PeersListAdapter(Context context, List<WifiP2pDevice> peers) {
        super();
        mContext = context;
        mPeers = peers;
    }

    @Override
    public int getCount() {
        return mPeers.size();
    }

    @Override
    public Object getItem(int i) {
        return mPeers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private static class ViewHolder {
        TextView textView;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.peers_row, null);
            holder = new ViewHolder();
            holder.textView = (TextView) view.findViewById(R.id.peers_list_item);
            holder.textView.setTypeface(null, Typeface.BOLD);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        WifiP2pDevice device = (WifiP2pDevice) getItem(i);
        holder.textView.setText(device.deviceName);
        return view;
    }
}
