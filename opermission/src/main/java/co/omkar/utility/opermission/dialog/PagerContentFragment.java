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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.omkar.utility.opermission.R;


/**
 * Created by Omya on 10/08/16.
 */
public class PagerContentFragment extends Fragment {
    private static final String MESSAGE = "message";

    String message;

    public PagerContentFragment() {
    }

    public static PagerContentFragment get(String message) {
        Bundle extras = new Bundle(1);
        extras.putString(MESSAGE, message);
        PagerContentFragment fragment = new PagerContentFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        if (extras != null) {
            message = extras.getString(MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager_content_layout, container, false);
        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(message);
        return view;
    }
}