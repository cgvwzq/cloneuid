package net.vwzq.cloneuid;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.support.v4.content.LocalBroadcastManager;
import java.io.IOException;

public class MifareListenerUID implements NfcAdapter.ReaderCallback {

    protected NfcA remoteTag = null;
    protected byte[] tUID = new byte[8]; // 2 pages
    protected Context mContext;

    public MifareListenerUID(Context context) {
        super();
        mContext = context;
    }


    @Override
    public void onTagDiscovered(Tag tag) {

        remoteTag = NfcA.get(tag);

        try {
            String str = readUID();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent("tag-detected"));
        } catch (Exception e) {
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent("error"));
        }
    }

    public String readUID() throws IOException {

        byte[] cmd = {(byte)0x30, (byte)0x00};
        byte[] response = new byte[16];

        if (remoteTag != null) {
            remoteTag.connect();
        } else {
            throw new IOException();
        }

        if (remoteTag.isConnected()) {
            // reads 4 pages from offset 0
            response = remoteTag.transceive(cmd);
            // copy 3 first pages to tUID
            System.arraycopy(response, 0, tUID, 0, 8);
            remoteTag.close();
        } else {
            throw new IOException();
        }

        // convert UID to string and return it
        return Utils.bytesToHex(tUID);

    }

    public void writeUID() throws IOException {

        byte[] cmd1 = {(byte)0xA2, (byte)0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
        byte[] cmd2 = {(byte)0xA2, (byte)0x01, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
        //byte[] cmd3 = {(byte)0xA2, (byte)0x01, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};

        // Calculate check bytes
        byte CT = (byte)0x88; // extract from NXP datasheet
        byte BCC0 = (byte)(CT ^ tUID[0] ^ tUID[1] ^ tUID[2]);
        //byte BCC1 = (byte)(tUID[4] ^ tUID[5] ^ tUID[6] ^ tUID[7]);

        tUID[3] = BCC0;
        //tUID[9] = BCC1;

        System.arraycopy(tUID, 0, cmd1, 2, 4);
        System.arraycopy(tUID, 4, cmd2, 2, 4);
        //System.arraycopy(tUID, 8, cmd3, 2, 4);

        if (remoteTag != null) {

            remoteTag.connect();

            if (remoteTag.isConnected()) {

                remoteTag.transceive(cmd1);
                remoteTag.transceive(cmd2);
                //remoteTag.transceive(cmd3);

                remoteTag.close();

                return;

            }

        }

        throw new IOException();

    }

}
