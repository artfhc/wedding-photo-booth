package com.arthurtimberly.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arthurtimberly.LaunchActivity;
import com.arthurtimberly.MyPreferenceActivity;
import com.arthurtimberly.R;
import com.arthurtimberly.client.ServiceClient;
import com.groundupworks.lib.photobooth.helpers.CameraHelper;
import com.groundupworks.lib.photobooth.helpers.ImageHelper;
import com.groundupworks.lib.photobooth.views.CenteredPreview;

public class SelectFilterFragment extends Fragment {
    //
    // Fragment bundle keys.
    //

    private static final String[] FRAGMENT_BUNDLE_KEY_JPEG_DATA = {"jpegData0", "jpegData1", "jpegData2", "jpegData3"};

    private static final String FRAGMENT_BUNDLE_KEY_ROTATION = "rotation";

    private static final String FRAGMENT_BUNDLE_KEY_REFLECTION = "reflection";

    /**
     * Jpeg frames in byte arrays. The first index is the frame count.
     */
    private byte[][] mFramesData = null;

    /**
     * The preview display orientation.
     */
    private int mPreviewDisplayOrientation = CameraHelper.CAMERA_SCREEN_ORIENTATION_0;

    /**
     * Flag to indicate whether the camera image is reflected.
     */
    private boolean mIsReflected = false;


    private TextView mTitleTextView = null;
    private RadioGroup mFilterRadioGroup = null;
    private Button mNextButton = null;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final LaunchActivity activity = (LaunchActivity) getActivity();

        /*
         * Get params.
         */
        Bundle args = getArguments();

        int jpegDataLength = 0;
        for (int i = 0; i < FRAGMENT_BUNDLE_KEY_JPEG_DATA.length; i++) {
            if (args.containsKey(FRAGMENT_BUNDLE_KEY_JPEG_DATA[i])) {
                jpegDataLength++;
            } else {
                break;
            }
        }

        byte[][] jpegData = new byte[jpegDataLength][];
        for (int i = 0; i < jpegDataLength; i++) {
            jpegData[i] = args.getByteArray(FRAGMENT_BUNDLE_KEY_JPEG_DATA[i]);
        }
        mFramesData = jpegData;

        float rotation = args.getFloat(FRAGMENT_BUNDLE_KEY_ROTATION);
        mPreviewDisplayOrientation = (int)rotation;

        boolean reflection = args.getBoolean(FRAGMENT_BUNDLE_KEY_REFLECTION);
        mIsReflected = reflection;


        /*
         * Functionalize views.
         */
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectFilterFragment.this.nextFragment();
            }
        });

        mFilterRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();

                if (isChecked) {
                    Resources res = getResources();
                    String[] options = res.getStringArray(R.array.pref__filter_options);
                    if (checkedId >= 0 && checkedId < options.length) {
                        String filter = options[checkedId];
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(getString(R.string.pref__filter_key),filter);
                        editor.commit();
                    }
                }
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*
         * Inflate views from XML.
         */
        View view = inflater.inflate(R.layout.fragment_select_filter, container, false);
        mTitleTextView = (TextView) view.findViewById(R.id.title_text_view);
        mFilterRadioGroup = (RadioGroup) view.findViewById(R.id.filter_radio_group);
        mNextButton = (Button) view.findViewById(R.id.next_button);

        createRadioButtons();

        return view;
    }

    private void createRadioButtons() {
        Resources res = getResources();
        String[] options = res.getStringArray(R.array.pref__filter_options);
        for(int i=0; i<options.length; i++) {
            RadioButton rb = (RadioButton)getActivity().getLayoutInflater().inflate(R.layout.filter_raido_button, null);
            rb.setText(options[i]);
            rb.setId(i);
            mFilterRadioGroup.addView(rb);
        }
    }
    
    public static SelectFilterFragment newInstance(byte[][] jpegData, float rotation, boolean reflection) {
        SelectFilterFragment fragment = new SelectFilterFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);
        for (int i = 0; i < jpegData.length; i++) {
            args.putByteArray(FRAGMENT_BUNDLE_KEY_JPEG_DATA[i], jpegData[i]);
        }
        args.putFloat(FRAGMENT_BUNDLE_KEY_ROTATION, rotation);
        args.putBoolean(FRAGMENT_BUNDLE_KEY_REFLECTION, reflection);

        return fragment;
    }

    private void nextFragment() {
        ((LaunchActivity) getActivity()).replaceFragment(
                ShareFragment.newInstance(mFramesData, mPreviewDisplayOrientation, mIsReflected), true, false);
    }
}
