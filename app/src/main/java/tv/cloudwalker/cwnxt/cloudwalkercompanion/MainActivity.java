package tv.cloudwalker.cwnxt.cloudwalkercompanion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import appUtils.PreferenceManager;
import de.hdodenhof.circleimageview.CircleImageView;
import fragment.MovieBoxFragment;
import fragment.NsdDeviceFragment;
import fragment.TvRemoteFragment;
import fragment.YoutubeFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    final Fragment fragment1 = new NsdDeviceFragment();
    final Fragment fragment2 = new TvRemoteFragment();
    final Fragment fragment3 = new MovieBoxFragment();
    final Fragment fragment4 = new YoutubeFragment();
    final FragmentManager fm = getSupportFragmentManager();
    public BottomNavigationView bottomNavigationView;
    private Fragment active = fragment1;
    private String nsdHost;
    private int nsdPort;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NsdServiceInfo resolvedNsdServiceInfo;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private String[] coloumnProjection = {
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.CONTACT_STATUS,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.NAME_RAW_CONTACT_ID
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showContacts();
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

        fm.beginTransaction().add(R.id.fragment_container, fragment4, "4").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment1, "1").commit();
        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void showContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI
                    , coloumnProjection
                    , null, null, null);
            if(cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (cursor.moveToNext()) {
                    String hasPhone = cursor.getString(2);
                    if(Integer.valueOf(hasPhone) == 1){
                        Cursor phones = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                                , null
                                , ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ cursor.getString(3)
                                , null
                                , null);
                        while (phones.moveToNext()) {
                            String phoneNumber = phones.getString(phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));
                            Log.d(TAG, "onCreate: phone number "+phoneNumber);
                            Log.d(TAG, "onCreate: name "+cursor.getString(0));
                        }
                        phones.close();
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: success ");
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;

                case R.id.navigation_dashboard:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;

                case R.id.navigation_notifications:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;

                case R.id.navigation_youtube:
                    fm.beginTransaction().hide(active).show(fragment4).commit();
                    active = fragment4;
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
}




























