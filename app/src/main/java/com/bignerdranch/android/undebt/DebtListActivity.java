package com.bignerdranch.android.undebt;

import android.support.v4.app.Fragment;

public class DebtListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new DebtListFragment();
    }
}
