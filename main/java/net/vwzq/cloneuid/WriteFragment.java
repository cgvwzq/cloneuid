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
import android.widget.Button;
import android.widget.EditText;

public class WriteFragment extends Fragment {

    private MifareListenerUID nfcListener;
    private Button readButton;
    private EditText[] miBox = new EditText[8]; // it was 12
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.write_fragment, null);
        nfcListener = ((MainActivity)getActivity()).nfcListener;

        readButton = (Button)view.findViewById(R.id.read_button);

        // Set click event
        if (readButton != null) {
            readButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        updateUID();
                        nfcListener.writeUID();
                        Utils.showSuccess(view, true);
                    } catch (Exception e) {
                        Utils.showSuccess(view, false);
                    }
                }
            });
        }

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
                    // if read mode
                    if (((MainActivity)getActivity()).mViewPager.getCurrentItem() == 0) {
                        fillUIDBytes();
                    } else {
                        Utils.showSuccess(view, true);
                    }
                    break;
                case "error":
                    Utils.showSuccess(view, false);
                    break;
            }
        }
    };

    private void fillUIDBytes() {

        int resId;
        EditText ed;
        for (int i=0; i<8; i++) {
            resId = getResources().getIdentifier("uid_byte"+i, "id", "net.vwzq.cloneuid");
            ed = (EditText)view.findViewById(resId);
            ed.setText(Utils.byteToHex(nfcListener.tUID[i]));
            miBox[i] = ed;
        }

    }

    private void updateUID() {

        for (int i=0; i<miBox.length; i++) {
            nfcListener.tUID[i] = Utils.hexStringToByteArray(miBox[i].getText().toString())[0];
        }

    }

}
