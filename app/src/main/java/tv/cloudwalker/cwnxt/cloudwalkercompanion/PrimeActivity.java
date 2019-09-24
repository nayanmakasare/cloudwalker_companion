package tv.cloudwalker.cwnxt.cloudwalkercompanion;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import adapter.FragmentAdapter;
import appUtils.PreferenceManager;
import fragment.CwTvProfileFragment;
import fragment.CwTvRemoteFragment;
import fragment.MovieBoxFragment;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.databinding.ActivityPrimeBinding;

public class PrimeActivity extends AppCompatActivity {

    private ActivityPrimeBinding activityPrimeBinding;
    private static final String TAG = "PrimeActivity";
    public CwTvRemoteFragment cwTvRemoteFragment = new CwTvRemoteFragment();
    private CwTvProfileFragment cwTvProfileFragment = new CwTvProfileFragment();
    private MovieBoxFragment movieBoxFragment = new MovieBoxFragment();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.tv_remote:
                    activityPrimeBinding.primeViewPager.setCurrentItem(0);
                    return true;
                case R.id.tv_profile:
                    activityPrimeBinding.primeViewPager.setCurrentItem(1);
                    return true;
                case R.id.tv_moviebox:
                    activityPrimeBinding.primeViewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };


    public void disableProgressBar(){
        activityPrimeBinding.mainProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPrimeBinding = DataBindingUtil.setContentView(this, R.layout.activity_prime);
        getSupportActionBar().hide();
        checkingSignInStatus();
        setupFm(getSupportFragmentManager(), activityPrimeBinding.primeViewPager);
        activityPrimeBinding.primeViewPager.setOnPageChangeListener(new PageChange());
        activityPrimeBinding.primeBottomNavBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
        adapter.add(cwTvProfileFragment, "Tv Profile");
        adapter.add(movieBoxFragment, "Tv MovieBox");
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
            String serviceName = data.getStringExtra("serviceName");
            int port = data.getIntExtra("port", 0);
            cwTvRemoteFragment.setNewDeviceForCommunication(address, port, serviceName);
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
                case 2:
                    activityPrimeBinding.primeBottomNavBar.setSelectedItemId(R.id.tv_moviebox);
                    break;
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
