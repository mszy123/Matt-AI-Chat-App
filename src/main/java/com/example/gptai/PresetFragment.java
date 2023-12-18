package com.example.gptai;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PresetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PresetFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SpecificPresetFragment specificPresetFragment;
    private MainActivity mainActivity;

    public PresetFragment() {
        // Required empty public constructor
    }

    public PresetFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PresetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PresetFragment newInstance(String param1, String param2) {
        PresetFragment fragment = new PresetFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preset, container, false);

        CardView writeEssayCardView = view.findViewById(R.id.write_essay_cardview);
        CardView essayGradeCardView = view.findViewById(R.id.essay_grade_cardview);
        CardView conceptCardView = view.findViewById(R.id.concept_cardview);
        CardView songGenerateCardView = view.findViewById(R.id.song_generate_cardview);
        CardView poemGenerateCardView = view.findViewById(R.id.poem_generate_cardview);
        CardView storyGenerateCardView = view.findViewById(R.id.story_generate_cardview);
        CardView movieGenerateCardView = view.findViewById(R.id.movie_generate_cardview);
        CardView nameGenerateCardView = view.findViewById(R.id.name_generate_cardview);
        CardView sloganGenerateCardView = view.findViewById(R.id.slogan_generate_cardview);
        CardView emailGenerateCardView = view.findViewById(R.id.email_generate_cardview);
        CardView jokeGenerateCardView = view.findViewById(R.id.joke_generate_cardview);
        CardView movieEmojiCardView = view.findViewById(R.id.movie_emoji_cardview);
        CardView socialMediaCaptionCardView = view.findViewById(R.id.caption_generate_cardview);




        writeEssayCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specificPresetFragment = new SpecificPresetFragment("Write Essay", "Enter essay prompt here", "Write an essay for this prompt:", mainActivity);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, specificPresetFragment)
                        .commit();
            }
        });

        essayGradeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specificPresetFragment = new SpecificPresetFragment("Grade Essay", "Enter essay and rubric (optional) here", "Grade this essay as accurately as possible. Provide specific and descriptive feedback:", mainActivity);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, specificPresetFragment)
                        .commit();
            }
        });

        conceptCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specificPresetFragment = new SpecificPresetFragment("Explain a Concept", "Enter topic", "Explain this concept in an easy to understand manner:", mainActivity);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, specificPresetFragment)
                        .commit();
            }
        });

        songGenerateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specificPresetFragment = new SpecificPresetFragment("Song Generator", "Enter song topic (optional)", "Write the lyrics to a song about:", mainActivity);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, specificPresetFragment)
                        .commit();
            }
        });

        poemGenerateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specificPresetFragment = new SpecificPresetFragment("Poem Generator", "Enter poem topic (optional)", "Write a poem about this topic:", mainActivity);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, specificPresetFragment)
                        .commit();
            }
        });

        storyGenerateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specificPresetFragment = new SpecificPresetFragment("Write a Story", "Enter story topic (optional)", "Write an interesting story about:", mainActivity);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, specificPresetFragment)
                        .commit();
            }
        });

        movieGenerateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specificPresetFragment = new SpecificPresetFragment("Movie Maker", "Enter movie topic here:", "Write an interesting movie script about this topic:", mainActivity);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, specificPresetFragment)
                        .commit();
            }
        });

        nameGenerateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specificPresetFragment = new SpecificPresetFragment("Name Generator", "Enter what name is for", "Think of 10 unique names for something that:", mainActivity);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, specificPresetFragment)
                        .commit();
            }
        });

        sloganGenerateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specificPresetFragment = new SpecificPresetFragment("Slogan Generator", "Enter what slogan is for", "Write a catchy slogan for:", mainActivity);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, specificPresetFragment)
                        .commit();
            }
        });

        emailGenerateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specificPresetFragment = new SpecificPresetFragment("Email Generator", "Enter email topic", "Write a email for: ", mainActivity);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, specificPresetFragment)
                        .commit();
            }
        });

        jokeGenerateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specificPresetFragment = new SpecificPresetFragment("Joke Generator", "Joke topic (optional)", "Write a really funny joke about: ", mainActivity);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, specificPresetFragment)
                        .commit();
            }
        });

        movieEmojiCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specificPresetFragment = new SpecificPresetFragment("Movie to Emoji", "Enter movie title", "Convert this movie to emoji. Only respond in emoji: ", mainActivity);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, specificPresetFragment)
                        .commit();
            }
        });

        socialMediaCaptionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specificPresetFragment = new SpecificPresetFragment("Social Media Caption Maker", "Enter what your post is about (optional)", "Make a social media post caption: ", mainActivity);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, specificPresetFragment)
                        .commit();
            }
        });




        // Inflate the layout for this fragment
        return view;
    }
}