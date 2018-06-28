package com.example.ptdev.soundrecordercopy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.ptdev.soundrecordercopy.database.RecordingDbHelper;
import com.example.ptdev.soundrecordercopy.fragment.RecordingFragment;
import com.example.ptdev.soundrecordercopy.fragment.SavedRecordingsFragment;


public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private SoundRecorderAdapter mSoundRecorderAdapter;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.activity_main_widget_toolbar);
        setSupportActionBar(mToolbar);

        mViewPager = (ViewPager) findViewById(R.id.activity_main_view_pager);
        SoundRecorderAdapter mSoundRecorderAdapter = new SoundRecorderAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSoundRecorderAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.activity_main_tab_layout);
        mTabLayout.setupWithViewPager(mViewPager, true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_setting){
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);


            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SoundRecorderAdapter extends FragmentPagerAdapter{
        private String[] title = {"fragment_recording", "fragment_saved_recordings"};

        public SoundRecorderAdapter(FragmentManager fm) {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return RecordingFragment.newInstance();
                case 1:
                    return SavedRecordingsFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return title.length;
        }
    }


}
