package com.example.gptai;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpecificPresetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpecificPresetFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String title;
    private String promptHint;
    TextView titleTextView;
    EditText promptEditText;

    //the prompt i input, not user
    String prompt;

    MaterialButton generateBtn;
    private MainActivity mainActivity;


    public SpecificPresetFragment() {
        // Required empty public constructor
    }

    public SpecificPresetFragment(String title, String promptHint, String prompt, MainActivity mainActivity) {
        this.title = title;
        this.promptHint = promptHint;
        this.prompt = prompt;
        this.mainActivity = mainActivity;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SpecificPresetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpecificPresetFragment newInstance(String param1, String param2) {
        SpecificPresetFragment fragment = new SpecificPresetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Handle back button press
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Replace the current fragment with the ChatFragment
                if(getActivity() instanceof MainActivity){
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, new PresetFragment((MainActivity) getActivity()))
                            .commit();
                    ((MainActivity)getActivity()).bottomNavigationView.setSelectedItemId(R.id.preset_item);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_specific_preset, container, false);

        titleTextView = view.findViewById(R.id.title_text_view);
        promptEditText = view.findViewById(R.id.first_edit_text);
        generateBtn = view.findViewById(R.id.generate_button);

        titleTextView.setText(title);
        promptEditText.setHint(promptHint);

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mainActivity.hasEnoughCredits()){
                    mainActivity.openChatActivity(prompt + " " + promptEditText.getText().toString(), promptEditText.getText().toString());



                }
            }
        });


        return view;
    }







}