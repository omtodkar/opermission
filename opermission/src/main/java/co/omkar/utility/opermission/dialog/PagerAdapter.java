/*
    Copyright (C) 2016 Omkar Todkar

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

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
