package abhi.osmchat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;



class SectionsPagerAdapter extends FragmentPagerAdapter{


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case 0:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return  chatsFragment;

            case 2:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;

            default:
                return  null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){

        switch (position) {
            case 0:
                return "FRIENDS";

            case 1:
                return "CHATS";

            case 2:
                return "REQUESTS";

            default:
                return null;
        }

    }

}
