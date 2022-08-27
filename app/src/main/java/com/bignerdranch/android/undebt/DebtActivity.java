package com.bignerdranch.android.undebt;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class DebtActivity extends SingleFragmentActivity{

    public static final String EXTRA_DEBT_ID =
            "com.bignerdranch.android.undebt.debt_id";


    // assigning which debt to display by passing debt ID when debtActivity started
    public static Intent newIntent(Context packageContext, UUID debtId) {
        Intent intent = new Intent(packageContext, DebtActivity.class);
        intent.putExtra(EXTRA_DEBT_ID, debtId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID debtId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_DEBT_ID);
        return  DebtFragment.newInstance(debtId);
    }
}
