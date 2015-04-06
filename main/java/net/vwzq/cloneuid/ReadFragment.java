package net.vwzq.cloneuid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;

public class ReadFragment extends Fragment {

    private MifareListenerUID nfcListener;
    private View view;
    private TextView text;
    private LinearLayout next;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.read_fragment, null);
        nfcListener = ((MainActivity)getActivity()).nfcListener;

        text = (TextView)view.findViewById(R.id.read_uid);
        next = (LinearLayout)view.findViewById(R.id.next);

        // Set LocalBroadcast listener
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("tag-detected"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("error"));

        return view;
    }

    @Override
    public void onDestroyView() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()) {
                case "tag-detected":
                    // if in read mode
                    if (((MainActivity)getActivity()).mViewPager.getCurrentItem() == 0) {
                        Utils.showSuccess(view, true);
                        text.setText(Utils.bytesToHex(Arrays.copyOfRange(nfcListener.tUID,0,8)));
                        next.setVisibility(View.VISIBLE);
                    }
                    break;
                case "error":
                    Utils.showSuccess(view, false);
                    break;
            }

        }
    };

}
