package com.mentalmachines.droidcon_boston.views;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import com.mentalmachines.droidcon_boston.R;
import com.mentalmachines.droidcon_boston.views.agenda.AgendaFragment;
import com.mentalmachines.droidcon_boston.views.web.WebFragment;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "csLPIPIQ6AoWyhzCSHlK2lOen";

    private static final String TWITTER_SECRET = "p3c45qpNvIOQiTZi6iK9Cffb3xRH4X7SThT4EfVo7fIu42SNWD";

    final FragmentManager fragmentManager = getSupportFragmentManager();

    ActionBarDrawerToggle drawerToggle;

    DrawerLayout mDrawerLayout;

    ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.main_activity);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        mDrawerList = findViewById(R.id.fragmentList);
        mDrawerList.setAdapter(new NavigationAdapter(this));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new AgendaFragment()).commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        /*if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/
        // Handle your other action bar items...
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                } else if (fragmentManager.getBackStackEntryCount() == 1) {
                    // to avoid looping below on initScreen
                    super.onBackPressed();
                    finish();
                } else {
                    if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                }
                return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        super.onBackPressed();
    }


    class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (position < 3) {
                ((NavigationAdapter) parent.getAdapter()).setSelectedIndex(position);
            } //others are contact links
            String targetUrl = null;
            final Fragment fragment;
            switch (position) {
                case 0: //agenda
                    fragment = new AgendaFragment();
                    break;
                case 1: //tweet
                    fragment = new TweetsFragment();
                    break;
                case 2: //faq
                    fragment = new FAQFragment();
                    break;
                case 3: //contact us
                    fragment = new WebFragment();
                    targetUrl = NavigationAdapter.LN_CONTACT;
                    break;
                case 4: //contact, facebook
                    fragment = new WebFragment();
                    targetUrl = NavigationAdapter.LN_FB;
                    break;
                case 5:
                    fragment = new WebFragment();
                    targetUrl = NavigationAdapter.LN_INSTA;
                    break;
                case 6:
                    fragment = new WebFragment();
                    targetUrl = NavigationAdapter.LN_LINKD;
                    break;
                case 7: //contact twitter, instagram, linked in
                    fragment = new WebFragment();
                    targetUrl = NavigationAdapter.LN_TWEET;
                    break;
                default:
                    return;
            }

            if (targetUrl != null) {
                Bundle bundle = new Bundle();
                bundle.putString("url", targetUrl);
                fragment.setArguments(bundle);
            }

            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            fragmentManager.executePendingTransactions();
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    } //end click listener

    public void faqClick(View v) {
        final Intent tnt = new Intent(Intent.ACTION_VIEW);
        tnt.setData(Uri.parse((String) v.getTag()));
        startActivity(tnt);
    }
}
