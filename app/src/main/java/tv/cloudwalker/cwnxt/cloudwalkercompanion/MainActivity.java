package tv.cloudwalker.cwnxt.cloudwalkercompanion;

import android.content.Intent;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import adapter.FragmentAdapter;
import appUtils.PreferenceManager;
import de.hdodenhof.circleimageview.CircleImageView;
import fragment.MovieBoxFragment;
import fragment.NsdDeviceFragment;
import fragment.TvRemoteFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    public BottomNavigationView bottomNavigationView;
    private String nsdHost;
    private int nsdPort;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NsdServiceInfo resolvedNsdServiceInfo;
    public ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cloudwalker Companion");

        ((NavigationView) findViewById(R.id.side_nav_view)).setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        PreferenceManager preferenceManager = new PreferenceManager(this);
        if (preferenceManager.isGoogleSignIn() && preferenceManager.isCloudwalkerSigIn()) {
            View headerView = ((NavigationView) findViewById(R.id.side_nav_view)).getHeaderView(0);
            Glide.with(this)
                    .load(preferenceManager.getProfileImageUrl())
                    .into((CircleImageView) headerView.findViewById(R.id.navProfileImage));
            ((TextView) headerView.findViewById(R.id.navUserName)).setText(preferenceManager.getUserName());
            ((TextView) headerView.findViewById(R.id.navUserEmail)).setText(preferenceManager.getUserEmail());
        }

        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        viewPager = findViewById(R.id.viewpager); //Init Viewpager
        setupFm(getSupportFragmentManager(), viewPager); //Setup Fragment
        viewPager.setCurrentItem(0); //Set Currrent Item When Activity Start
        viewPager.setOnPageChangeListener(new PageChange());
    }

    public static void setupFm(FragmentManager fragmentManager, ViewPager viewPager){
        FragmentAdapter Adapter = new FragmentAdapter(fragmentManager);
        //Add All Fragment To List
        Adapter.add(new NsdDeviceFragment(), "Tv Devices");
        Adapter.add(new TvRemoteFragment(), "Tv Remote");
        Adapter.add(new MovieBoxFragment(), "Movie Box");
        viewPager.setAdapter(Adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.user_profile:
                drawerLayout.closeDrawer(Gravity.LEFT, false);
                Boolean googleResult = new PreferenceManager(this).isGoogleSignIn();
                Boolean cloudwalkerResult = new PreferenceManager(this).isCloudwalkerSigIn();
                if (googleResult && cloudwalkerResult) {
                    startActivity(new Intent(this, ProfileActivity.class));
                } else {
                    Toast.makeText(this, "No Profile is Present. Please Login First", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawer(Gravity.LEFT, true);
                    startActivity(new Intent(this, SignInActivity.class));
                }
                break;

            case R.id.linkedDevices:
                drawerLayout.closeDrawer(Gravity.LEFT, false);
                startActivity(new Intent(this, LinkedDeviceActivity.class));
                break;

            case R.id.exit:
                System.exit(0);
                break;
        }
        return true;
    }

    public String getNsdHost() {
        return nsdHost;
    }

    public void setNsdHost(String nsdHost) {
        this.nsdHost = nsdHost;
    }

    public int getNsdPort() {
        return nsdPort;
    }

    public void setNsdPort(int nsdPort) {
        this.nsdPort = nsdPort;
    }

    public NsdServiceInfo getResolvedNsdServiceInfo() {
        return resolvedNsdServiceInfo;
    }

    public void setResolvedNsdServiceInfo(NsdServiceInfo resolvedNsdServiceInfo) {
        this.resolvedNsdServiceInfo = resolvedNsdServiceInfo;
    }


    public class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                    break;
                case 1:
                    bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);
                    break;
                case 2:
                    bottomNavigationView.setSelectedItemId(R.id.navigation_notifications);
                    break;
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}




























