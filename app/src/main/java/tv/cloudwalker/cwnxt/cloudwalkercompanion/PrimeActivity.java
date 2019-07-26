package tv.cloudwalker.cwnxt.cloudwalkercompanion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import adapter.FragmentAdapter;
import appUtils.PreferenceManager;
import fragment.CwTvProfileFragment;
import fragment.CwTvRemoteFragment;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.databinding.ActivityPrimeBinding;

public class PrimeActivity extends AppCompatActivity {

    private ActivityPrimeBinding activityPrimeBinding;
    private static final String TAG = "PrimeActivity";
    private CwTvRemoteFragment cwTvRemoteFragment = new CwTvRemoteFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPrimeBinding = DataBindingUtil.setContentView(this, R.layout.activity_prime);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        Log.d(TAG, "onCreate: google status "+preferenceManager.getGoogleSignInStatus());
        Log.d(TAG, "onCreate: interm status "+preferenceManager.getCwIntermidiateStatus());
        Log.d(TAG, "onCreate: Pref status "+preferenceManager.getCwPrefrenceStatus());
        Log.d(TAG, "onCreate: Google id "+preferenceManager.getGoogleId());

        getSupportActionBar().hide();
        checkingSignInStatus();
        setupFm(getSupportFragmentManager(), activityPrimeBinding.primeViewPager);
        activityPrimeBinding.primeViewPager.setOnPageChangeListener(new PageChange());
    }

    private void checkingSignInStatus(){
        Log.d(TAG, "checkingSignInStatus: ");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        boolean isGoogleSignIn = preferenceManager.getGoogleSignInStatus();
        boolean isCloudwalkerIntermidateSet = preferenceManager.getCwIntermidiateStatus();
        boolean isCloudwalkerPrefSet = preferenceManager.getCwPrefrenceStatus();

        if(!isGoogleSignIn){
            startActivity(new Intent(this, CwGoogleActivity.class));
            finish();
        }else if(!isCloudwalkerIntermidateSet){
            startActivity(new Intent(this, CwIntermideateActivity.class));
            finish();
        }else if(!isCloudwalkerPrefSet){
            startActivity(new Intent(this, CwPreferenceActivity.class));
            finish();
        }

    }

    public void setupFm(FragmentManager fragmentManager, ViewPager viewPager){
        FragmentAdapter adapter = new FragmentAdapter(fragmentManager);
        adapter.add(cwTvRemoteFragment, "Tv Remote");
        adapter.add(new CwTvProfileFragment(), "Tv Profile");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if(resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: in ");
            String address = data.getStringExtra("nsdAddress");
            int port = data.getIntExtra("port", 0);
            cwTvRemoteFragment.setNewDeviceForCommunication(address, port);
        }
    }

    public class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    activityPrimeBinding.primeBottomNavBar.setSelectedItemId(R.id.tv_remote);
                    break;
                case 1:
                    activityPrimeBinding.primeBottomNavBar.setSelectedItemId(R.id.tv_profile);
                    break;
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
