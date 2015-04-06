package net.vwzq.cloneuid;

import android.view.View;

public class Utils {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String byteToHex(byte val) {
        char[] hexChars = new char[2];
        int v = val & 0xFF;
        hexChars[0] = hexArray[v >>> 4];
        hexChars[1] = hexArray[v & 0x0F];
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];
        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static void showSuccess(View v, boolean ok) {

        final View view = v;

        if (ok) {
            view.setBackground(view.getContext().getResources().getDrawable(R.drawable.border_green, null));
        } else {
            view.setBackground(view.getContext().getResources().getDrawable(R.drawable.border_red, null));
        }

        final Runnable r = new Runnable() {
            public void run() {
                view.setBackground(view.getContext().getResources().getDrawable(R.drawable.border_default, null));
            }
        };

        view.getHandler().postDelayed(r, 2000);

    }

}
