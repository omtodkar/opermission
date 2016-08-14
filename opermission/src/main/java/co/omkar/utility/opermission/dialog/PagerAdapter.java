package co.omkar.utility.opermission.dialog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Omya on 10/08/16.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    private String[] messageList;

    public PagerAdapter(FragmentManager fm, String[] message) {
        super(fm);
        this.messageList = message;
    }

    @Override
    public Fragment getItem(int position) {
        String message = messageList[position];
        return PagerContentFragment.get(message);
    }

    @Override
    public int getCount() {
        return messageList != null ? messageList.length : 0;
    }
}
