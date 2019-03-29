package com.ricardorainha.mustache.adapter;

import com.ricardorainha.mustache.view.fragments.BarbershopsListFragment;
import com.ricardorainha.mustache.view.fragments.BarbershopsMapFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class BarbershopsPagerAdapter extends FragmentStatePagerAdapter {

    private String[] tabsTitles;

    public BarbershopsPagerAdapter(FragmentManager fm, String[] tabsTitles) {
        super(fm);
        this.tabsTitles = tabsTitles;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment activeFragment = null;

        switch (position) {
            case 0:
                activeFragment = new BarbershopsListFragment();
                break;
            case 1:
                activeFragment = new BarbershopsMapFragment();
                break;
        }

        return activeFragment;
    }

    @Override
    public int getCount() {
        if (this.tabsTitles != null) {
            return this.tabsTitles.length;
        }
        return 0;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if ((this.tabsTitles != null) && (position < this.tabsTitles.length)) {
            return this.tabsTitles[position];
        }
        return super.getPageTitle(position);
    }
}
