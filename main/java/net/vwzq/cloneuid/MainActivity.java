package net.vwzq.cloneuid;

import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import sheetrock.panda.changelog.ChangeLog;


public class MainActivity extends ActionBarActivity {

    protected SectionsPagerAdapter mSectionsPagerAdapter;
    protected ViewPager mViewPager;

    // Objects for NFC
    protected NfcAdapter mAdapter;
    protected MifareListenerUID nfcListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mAdapter != null && mAdapter.isEnabled()) {
            mAdapter.setNdefPushMessage(null, this);
            nfcListener = new MifareListenerUID(this);
        } else {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("You need to enable NFC");
            adb.setIcon(android.R.drawable.ic_dialog_alert);
            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startNfcSettingsActivity();
                }
            });
            adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            adb.show();
        }

    }

    protected void startNfcSettingsActivity() {
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
        } else {
            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.enableReaderMode(this, nfcListener, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);

    }

    public void onPause() {
        mAdapter.disableReaderMode(this);
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_changelog) {
            ChangeLog cl = new ChangeLog(this, R.raw.changelog, "ChangeLog");
            cl.getFullLogDialog().show();
            return true;
        } else if (id == R.id.action_about) {
            ChangeLog ab = new ChangeLog(this, R.raw.about, "About");
            ab.getFullLogDialog().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ReadFragment();
                case 1:
                    return new WriteFragment();
                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

}
