package adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter {
//        private List<Fragment> Fragment = new ArrayList<>(); //Fragment List
//        private List<String> NamePage = new ArrayList<>(); // Fragment Name List
//        public FragmentAdapter(FragmentManager manager) {
//            super(manager);
//        }
//        public void add(Fragment Frag, String Title) {
//            Fragment.add(Frag);
//            NamePage.add(Title);
//        }
//        @Override
//        public Fragment getItem(int position) {
//            return Fragment.get(position);
//        }
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return NamePage.get(position);
//        }
//        @Override
//        public int getCount() {
//            return 3;
//        }



    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public FragmentAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void add(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
