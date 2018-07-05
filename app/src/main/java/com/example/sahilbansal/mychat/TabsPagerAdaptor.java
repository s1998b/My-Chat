package com.example.sahilbansal.mychat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Sahil Bansal on 22-03-2018.
 */

class TabsPagerAdaptor extends FragmentPagerAdapter{
    public TabsPagerAdaptor(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
            case 1:
                Chatsfragment chatsfragment = new Chatsfragment();
                return chatsfragment;
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
    public CharSequence getPageTitle(int position)
    {
         switch (position)
         {
             case 0:
                 return "Requests";
             case 1:
                 return "Chats";
             case 2:
                 return "Friends";
             default:
                 return null;
         }
    }
}
