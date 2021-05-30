package com.example.uscfilms.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uscfilms.MainActivity;
import com.example.uscfilms.R;
import com.example.uscfilms.SliderAdapter;
import com.example.uscfilms.SliderData;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.smarteist.autoimageslider.SliderView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    FilmsCollectionAdapter filmsCollectionAdapter;
    ViewPager2 viewPager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.show();
        }

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        return root;
    }

    @Override
    public void onViewCreated( View view, @Nullable Bundle savedInstanceState) {

        filmsCollectionAdapter = new FilmsCollectionAdapter(this);
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(filmsCollectionAdapter);
        viewPager.setUserInputEnabled(false);
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                viewPager.setCurrentItem(position,false);
            }
        });
        String[] tabTitles = new String[]{"Movies","TV Shows"};
        TabLayoutMediator tlb = new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        );
        tlb.attach();


    }






}



