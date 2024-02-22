package com.example.sda_a4_2024_bradleyweibel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/*
 * viewPager adapter.
 * @author Chris Coughlan 2019
 */
public class ViewPageAdapter extends FragmentPagerAdapter {

    private Context context;

    /**
     *
     * @param fm
     * @param behavior
     * @param nContext
     */
    ViewPageAdapter(FragmentManager fm, int behavior, Context nContext)
    {
        super(fm, behavior);
        context = nContext;
    }

    /**
     *
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Fragment getItem(int position)
    {

        Fragment fragment = new Fragment();

        //finds the tab position (note array starts at 0)
        position = position+1;

        //finds the fragment
        switch (position)
        {
            case 1:
                //code
                fragment = new Welcome();
                break;
            case 2:
                //code
                fragment = new BookList();
                break;
            case 3:
                //code
                fragment = new Settings();
                break;
        }

        return fragment;
    }

    /**
     *
     * @return 3 for how many tab headers there are.
     */
    @Override
    public int getCount()
    {
        return 3;
    }

    /**
     *
     * @param position The position of the title requested
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position)
    {
        position = position+1;

        CharSequence tabTitle = "";

        //finds the fragment
        switch (position)
        {
            case 1:
                //code
                tabTitle = "HOME";
                break;
            case 2:
                //code
                tabTitle = "BOOKS";
                break;
            case 3:
                //code
                tabTitle = "SETTINGS";
                break;
        }

        return tabTitle;
    }
}
