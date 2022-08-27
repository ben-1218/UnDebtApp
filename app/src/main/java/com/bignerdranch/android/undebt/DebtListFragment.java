package com.bignerdranch.android.undebt;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import java.util.List;
import  com.bignerdranch.android.undebt.DebtFragment;
import com.bignerdranch.android.undebt.database.DebtBaseHelper;

public class DebtListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mDebtRecyclerView;
    private DebtAdapter mAdapter;
    private boolean mSubtitleVisible;
    private double mSum = 0 ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        calcSum();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_debt_list, container, false);


        mDebtRecyclerView = (RecyclerView) view.findViewById(R.id.debt_recycler_view);
        mDebtRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        updateUI();
        return view;
    }

    //override the onResume to trigger updateUI
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_debt_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }

    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.new_debt:
                Debt debt = new Debt();
                DebtLab.get(getActivity()).addDebt(debt);
                Intent intent = DebtPagerActivity
                        .newIntent(getActivity(), debt.getId());
                startActivity(intent);
                return true;

                //show total amount in a toast based on unsettled debt
            case R.id.total_amount:
                String x = String.valueOf(mSum);
                Toast.makeText(getActivity(),"Total Debt Amount : RM " + String.valueOf(mSum) , Toast.LENGTH_LONG).show();
                return true;

                //show available debt count
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;

                //when click it will delete debt with settled checked
            case R.id.clear_settled:
                DebtLab.get(getActivity()).ClearSettledDebt();
                updateUI();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }



    private void updateSubtitle(){
        DebtLab debtLab = DebtLab.get(getActivity());
        int debtCount = debtLab.getDebts().size();
        String subtitle = getString(R.string.subtitle_format, debtCount);

        if(!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    //update the crimelistfragment UI
    private void updateUI(){
        DebtLab debtLab = DebtLab.get(getActivity());
        List<Debt> debts = debtLab.getDebts();

        if(mAdapter == null){
            mAdapter = new DebtAdapter(debts);
            mDebtRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setDebts(debts);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }


    private void calcSum() {
        DebtLab debtLab = DebtLab.get(getActivity());
        List<Debt> debts = debtLab.getDebts();
        // loop thru all recorded debt, if the debt is settled it will not included in the calculation of total amount
        for (int i = 0; i < debts.size(); i++) {
            if (!debts.get(i).isSettled()) {
            mSum += debts.get(i).getAmount();}
        }

        mSum = Math.round(mSum*100.0)/100.0;


    }


    //this private inner class will let the viewHolder inflate and own the layout
    private class DebtHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mAmountTextView;
        private ImageView mSettledImageView;
        private Debt mDebt;
        private MenuItem mTotalAmount;

        public DebtHolder (LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_debt, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.debt_title);
            mAmountTextView = (TextView) itemView.findViewById(R.id.debt_amount);
            mDateTextView = (TextView) itemView.findViewById(R.id.debt_date);
            mSettledImageView = (ImageView) itemView.findViewById(R.id.debt_settled);

        }
        //once clicked, will display debt activity as an intent
        @Override
        public void onClick(View view){
            Intent intent = DebtPagerActivity.newIntent(getActivity(), mDebt.getId());
            startActivity(intent);
        }

        public void bind(Debt debt){
            mDebt = debt;
            mTitleTextView.setText(mDebt.getTitle());
           mAmountTextView.setText("RM" +String.valueOf(mDebt.getAmount()));
            mDateTextView.setText(mDebt.getDate().toString());
            mSettledImageView.setVisibility(debt.isSettled() ? View.VISIBLE : View.GONE);

        }
    }

    private class DebtAdapter extends RecyclerView.Adapter<DebtHolder> {
        private List<Debt> mDebts;

        public DebtAdapter(List<Debt> debts) {
            mDebts = debts;
        }
        @Override
        public DebtHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new DebtHolder(layoutInflater, parent);
        }

        // bind method will be called each time the RecyclerView requests that a given
        //CrimeHolder be bound to a particular crime.
        @Override
        public void onBindViewHolder(DebtHolder holder, int position) {
            Debt debt = mDebts.get(position);
            holder.bind(debt);
        }
        @Override
        public int getItemCount() {
            return mDebts.size();
        }

        public void  setDebts(List<Debt> debts) {
            mDebts = debts;
        }

    }


}
