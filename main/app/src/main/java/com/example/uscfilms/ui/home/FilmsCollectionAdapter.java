package com.example.uscfilms.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.uscfilms.HomeTabFragment;

public class FilmsCollectionAdapter extends FragmentStateAdapter {

    public FilmsCollectionAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return HomeTabFragment.newInstance("movie");
            case 1 :
                return HomeTabFragment.newInstance("tv");
        }
        return HomeTabFragment.newInstance("movie");
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
