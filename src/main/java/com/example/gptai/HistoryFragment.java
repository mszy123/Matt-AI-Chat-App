package com.example.gptai;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment implements MessageAdapter.ChipClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView recyclerView;
    ConversationAdapter conversationAdapter;
    private MessageDatabaseHelper messageDbHelper;
    ArrayList<ArrayList<Message>> conversationList;
    ArrayList<Message> messageList;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MainActivity mainActivity;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public HistoryFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);



        conversationList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);
        messageDbHelper = new MessageDatabaseHelper(getActivity()); // Initialize the messageDbHelper

        // Retrieve stored messages from the database
        messageList = messageDbHelper.getAllMessages();

        ArrayList<ArrayList<Message>> conversations = groupMessagesByConversationID(messageList);


//        firstMessageList = formatList(conversationList);

        // Create an instance of the MessageAdapter
        conversationAdapter = new ConversationAdapter(conversations);



        // Set the layout manager and adapter for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(conversationAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);



        // Set spacing between items
        int spacing = 60; // Define the spacing dimension in your resources
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(spacing);
        recyclerView.addItemDecoration(itemDecoration);
        return view;
    }

    @Override
    public void onCopyClicked(String text) {
        mainActivity.onCopyClicked(text);
    }

    @Override
    public void onShareClicked(String text) {
        mainActivity.onShareClicked(text);
    }

    // Create a method to group messages by conversationID
    private ArrayList<ArrayList<Message>> groupMessagesByConversationID(ArrayList<Message> messageList) {
        ArrayList<ArrayList<Message>> conversationList = new ArrayList<>();

        // Iterate over the messageList
        for (Message message : messageList) {
            boolean conversationFound = false;

            // Iterate over existing conversations in conversationList
            for (ArrayList<Message> conversation : conversationList) {
                // Check if the current message has the same conversationID as the conversation
                if (conversation.get(0).getConversationID() == (message.getConversationID())) {
                    // Add the message to the conversation
                    conversation.add(message);
                    conversationFound = true;
                    break;
                }
            }

            // If no existing conversation found, create a new conversation and add the message
            if (!conversationFound) {
                ArrayList<Message> newConversation = new ArrayList<>();
                newConversation.add(message);
                conversationList.add(newConversation);
            }
        }
        Log.d("Conversations", conversationList.toString());
        return conversationList;
    }

    public void clearDB(){
        messageDbHelper.clearDatabase();
        conversationList.clear();
        messageList.clear();
        conversationAdapter.notifyDataSetChanged();
        conversationAdapter = new ConversationAdapter(new ArrayList<>());
        recyclerView.setAdapter(conversationAdapter);
        recyclerView.scrollToPosition(0);
    }

}