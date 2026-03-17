package com.muhammadzain.mzkwallpaper.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public class FragmentPagerAdapter extends androidx.fragment.app.FragmentPagerAdapter {
    private final ArrayList<Fragment> fragmentArrayList=new ArrayList<>();
    private final ArrayList<String> fragmenttitlename=new ArrayList<>();
    public FragmentPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }
    public void addFragment (Fragment fragment,String title){
        fragmentArrayList.add(fragment);
        fragmenttitlename.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmenttitlename.get(position);
    }
}
