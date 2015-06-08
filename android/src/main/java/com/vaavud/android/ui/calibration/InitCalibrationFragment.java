package com.vaavud.android.ui.calibration;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.vaavud.android.R;
import com.vaavud.android.model.entity.Device;

public class InitCalibrationFragment extends Fragment {

    private static final String MIXPANEL_TOKEN = "757f6311d315f94cdfc8d16fb4d973c0";
    private Boolean layoutVersion;
    private Context context;

    public InitCalibrationFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = (CalibrationActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Typeface robotoBold = Typeface.createFromAsset(context.getAssets(), "Roboto-Bold.ttf");
        Typeface robotoLight = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
        Typeface robotoRegular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");

        View rootView = inflater.inflate(R.layout.fragment_init_calibration, container, false);

        TextView howto = (TextView) rootView.findViewById(R.id.calibration_howto);
        howto.setTypeface(robotoBold);
        TextView tip = (TextView) rootView.findViewById(R.id.calibration_tip);
        tip.setTypeface(robotoLight);
        TextView access = (TextView) rootView.findViewById(R.id.calibration_access);
        access.setTypeface(robotoLight);

        Button start = (Button) rootView.findViewById(R.id.calibration_start);
//        start.setTypeface(robotoRegular);

        start.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	if (((CalibrationActivity)context).getIsSleipnirPlugged()){
                  ((CalibrationActivity)context).getSupportFragmentManager().beginTransaction()
	                        .replace(R.id.container, new CalibrationFragment()).commit();
            	}else{
            		Toast.makeText(context, getResources().getString(R.string.calibration_not_sleipnir_toast),Toast.LENGTH_LONG).show();
            	}
            }
        });
        Button cancel = (Button) rootView.findViewById(R.id.calibration_cancel);
//        cancel.setTypeface(robotoRegular);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (context != null && Device.getInstance(context.getApplicationContext()).isMixpanelEnabled()) {
                    MixpanelAPI.getInstance(context.getApplicationContext(), MIXPANEL_TOKEN).track("Calibration Cancelled", null);
                }
                ((Activity)context).finish();
            }
        });

        if (((CalibrationActivity)context).isFirstTime()) {
            cancel.setText(R.string.calibration_later_text);
        } else {
            access.setVisibility(View.GONE);
            cancel.setText(R.string.calibration_cancel_text);
        }
        return rootView;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (context != null && Device.getInstance(context.getApplicationContext()).isMixpanelEnabled()) {
            MixpanelAPI.getInstance(context.getApplicationContext(), MIXPANEL_TOKEN).track("Calibration Initial Screen", null);
        }
    }
}
