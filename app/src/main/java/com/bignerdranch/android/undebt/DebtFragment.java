package com.bignerdranch.android.undebt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.bignerdranch.android.undebt.database.DebtBaseHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DebtFragment extends Fragment {

    private static final String ARG_DEBT_ID = "debt_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String ARG_DEBT_AMOUNT = "debt_amount";

    //create a constant for request code to receive the date back from debtfragment
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_PHOTO = 2;

    private Debt mDebt;
    private File mPhotoFile;
    private EditText mTitleField;
    private EditText mAmountField;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mDeleteButton;
    private CheckBox mSettledCheckBox;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Button mRemindButton;
    private Button mPayButton;
    private String mPayInput;

    private SimpleDateFormat mTimeFormat;
    private SimpleDateFormat mDateFormat;

    public static DebtFragment newInstance(UUID debtId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DEBT_ID, debtId);

        DebtFragment fragment = new DebtFragment();
        fragment.setArguments(args);
        return fragment;
    }


    //retrieving the extra and fetching the debt
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID debtId = (UUID) getArguments().getSerializable(ARG_DEBT_ID);
        mDebt = DebtLab.get(getActivity()).getDebt(debtId);
        mPhotoFile = DebtLab.get(getActivity()).getPhotoFile(mDebt);
    }

    @Override
    public void onPause(){
        super.onPause();
        DebtLab.get(getActivity())
                .updateDebt(mDebt);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_debt,container,false);

        mTitleField = (EditText) v.findViewById(R.id.debt_title);
        mTitleField.setText(mDebt.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDebt.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

       mAmountField = (EditText) v.findViewById(R.id.debt_amount);
       mAmountField.setText(String.valueOf(mDebt.getAmount()));
       mAmountField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (mAmountField.getText().length() == 0){
                    mDebt.setAmount(0.00);
                    mAmountField.setHint("RM" + mDebt.getAmount());
                }else{
                    Double y = Math.round(Double.parseDouble(s.toString()) * 100.0)/100.0;
                    mDebt.setAmount(y);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });
        //this indicate that it only show the date within the text field
        mDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy");
        mDateButton = (Button) v.findViewById(R.id.debt_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mDebt.getDate());
                dialog.setTargetFragment(DebtFragment.this,REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        //same as date
        mTimeFormat = new SimpleDateFormat("hh:mm a ");
        mTimeButton = (Button) v.findViewById(R.id.debt_time);
       updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mDebt.getDate());
                dialog.setTargetFragment(DebtFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        mSettledCheckBox = (CheckBox) v.findViewById(R.id.debt_settled);
        mSettledCheckBox.setChecked(mDebt.isSettled());
        mSettledCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                System.out.println(mDebt);
                mDebt.setSettled(isChecked);

            }

        });

        PackageManager packageManager = getActivity().getPackageManager();


        //photo code part
        mPhotoButton = (ImageButton) v.findViewById(R.id.debt_camera);
        //implicit intent to ask for a new pic to be taken into the location saved in mphotofile
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //if no camera app or no location to save the photo thn button will be disabled
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(), "com.bignerdranch.android.undebt.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                        .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
                for(ResolveInfo activity : cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        //delete button to delete item
        mDeleteButton = (Button)v.findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when this button is click, an alert dialog will show up to ask user are they really wanna delete the the current item
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Please Make sure:");
                builder.setMessage("Are you sure you want to remove this debt?");
                builder.setIcon(android.R.drawable.ic_delete);//set the icon of the alert dialog
                builder.setCancelable(false); //user can only press either the positive button or negative button to close the popup message
                builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if positive is clicked, then item will be remove from database and the list, and return to the PetListActivity
                        DebtLab.get(getActivity()).deleteDebt(mDebt);
                        Intent i = new Intent(getActivity(), DebtListActivity.class);
                        startActivity(i);
                    }
                });
                builder.setNegativeButton("Cancel", null);//cancel will return to the petFragment
                builder.show();


            }
        });
        mPayButton = (Button) v.findViewById(R.id.debt_pay);
        mPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
                mydialog.setTitle("Pay Amount: ");

                final EditText PayInput = new EditText(getActivity());

                PayInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                mydialog.setView(PayInput);
                PayInput.setText("0");

                mydialog.setPositiveButton("settle up", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        mPayInput = PayInput.getText().toString();
                        Toast.makeText(getActivity(), "RM " + mPayInput + " deducted from current debt amount", Toast.LENGTH_LONG).show();

                            double x = Double.parseDouble(mPayInput);
                            double y = mDebt.getAmount() - x;
                            mAmountField.setText(String.valueOf(y));
                            mDebt.setAmount(y);

                    }
                });

                mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
                mydialog.show();

            }
        });

        mRemindButton = (Button) v.findViewById(R.id.debt_report);
        mRemindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getDebtReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.debt_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_remind));
                startActivity(i);
            }
        });

        mPhotoView = (ImageView)v.findViewById(R.id.debt_photo);
        updatePhotoView();
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mDebt.setDate(date);
            updateDate();}

        else if (requestCode == REQUEST_TIME){
            Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mDebt.setDate(time);
            updateTime(); }

        else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.bignerdranch.android.undebt.fileprovider", mPhotoFile);

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }


    }


    private void updateTime() {
        mTimeButton.setText(mTimeFormat.format(mDebt.getDate()));
    }

    private void updateDate() {
        mDateButton.setText(mDateFormat.format(mDebt.getDate()));
    }



    //get report
    private String getDebtReport() {
        String settledstring = null;
        if (mDebt.isSettled()) {
            settledstring = getString(R.string.debt_report_settled);
        }else {
            settledstring = getString(R.string.debt_report_unsettled);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mDebt.getDate()).toString();

        String report = getString(R.string.debt_report, mDebt.getTitle(),mDebt.getAmount().toString() ,dateString, settledstring);
        return report;
    }


    //update photoview, check if the mphotofile is empty or not, then show in the photoview
    private void updatePhotoView(){
        if(mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);

        }else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }



}
