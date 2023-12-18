package com.example.gptai;

import static com.example.gptai.MainActivity.JSON;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment implements MessageAdapter.ChipClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;


    private MessageDatabaseHelper messageDbHelper;
    static int conversationID = 1;

    private MainActivity mainActivity;
    public String prompt;

    public String model;
    public Spinner spinner;

    private SharedPreferences sharedPreferences;
    private int lastConversationID;



    public ChatFragment() {
        // Required empty public constructor
    }

    public ChatFragment(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public ChatFragment(MainActivity mainActivity, String prompt){
        this.mainActivity = mainActivity;
        this.prompt=prompt;
        //addToChat(prompt, Message.SENT_BY_ME);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ChatFragment.conversationID++;
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        if(!sharedPreferences.contains("PREF_LAST_CONVERSATION_ID")){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("PREF_LAST_CONVERSATION_ID", 1);
            editor.apply();
        }
        lastConversationID = sharedPreferences.getInt("PREF_LAST_CONVERSATION_ID", 1);
        conversationID = lastConversationID + 1;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("PREF_LAST_CONVERSATION_ID", conversationID);
        editor.apply();




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);




        messageList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);
        welcomeTextView = view.findViewById(R.id.welcome_text);
        messageEditText = view.findViewById(R.id.message_edit_text);
        sendButton = view.findViewById(R.id.send_button);
        spinner = view.findViewById(R.id.spinner_models);

        //setup recycler view
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        messageAdapter.setChipClickListener(this); // "this" refers to the Activity or Fragment implementing the ChipClickListener


        // Set spacing between items
        int spacing = 40; // Define the spacing dimension in your resources
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(spacing);
        recyclerView.addItemDecoration(itemDecoration);

        spinner.setSelection(0);
        mainActivity.setModel("gpt-3.5-turbo");
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getContext(), R.array.models, R.layout.spinner);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner);
        spinner.setAdapter(adapter);



        messageDbHelper = new MessageDatabaseHelper(getActivity());

        sendButton.setOnClickListener((v)->{

            if(mainActivity.hasEnoughCredits() && !messageEditText.getText().toString().isEmpty()) {
                String prompt = messageEditText.getText().toString().trim();
                //Toast.makeText(this, prompt, Toast.LENGTH_LONG).show();
                addToChat(prompt, Message.SENT_BY_ME);
                messageEditText.setText("");
                this.callAPI(prompt);
                //TODO: undo
                //addToChat("Example Response", Message.SENT_BY_BOT);

                welcomeTextView.setVisibility(View.GONE);
                Log.d("Model", mainActivity.getModel());
                //modify credits
                mainActivity.decrementCredits();



            }
        });

        //click out of messageEditText
        messageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Drawable shapeDrawable = getResources().getDrawable(R.drawable.rounded_corner);
                GradientDrawable gradientShapeDrawable = (GradientDrawable) shapeDrawable;
                Resources resources = getResources();
                int clickStrokeColor;
                int strokeWidth;
                if (hasFocus) {
                    if(mainActivity.hasEnoughCredits()){
                        clickStrokeColor = resources.getColor(R.color.blue);
                    }
                    else if (!mainActivity.hasEnoughCredits()){
                        clickStrokeColor = resources.getColor(R.color.red);
                    }
                    else{
                        clickStrokeColor = resources.getColor(R.color.gray);
                    }
                    strokeWidth = 8;

                }
                else{
                    clickStrokeColor = resources.getColor(R.color.gray);
                    strokeWidth = 2;
                }
                gradientShapeDrawable.setStroke(strokeWidth,clickStrokeColor);
                messageEditText.setBackground(shapeDrawable);
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle the selected item
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals("GPT-4")){
                    mainActivity.setModel("gpt-4");
                }
                else{
                    mainActivity.setModel("gpt-3.5-turbo");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where no item is selected
                mainActivity.setModel("gpt-3.5-turbo");
            }
        });



        return view;
    }

    public void addToChat(String message, String sentBy){
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy, conversationID));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });



        //Store message in DB
        SQLiteDatabase db = messageDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MessageDatabaseHelper.COLUMN_CONTENT, message);
        values.put(MessageDatabaseHelper.COLUMN_CONVERSATION_ID, conversationID);
        values.put(MessageDatabaseHelper.COLUMN_SENDER, sentBy);
        db.insert(MessageDatabaseHelper.TABLE_MESSAGES,null, values);
        db.close();

    }

    public void addResponse(String response){
        messageList.remove(messageList.size()-1);
        addToChat(response,Message.SENT_BY_BOT);
    }

    public void clearMessages(){
        messageList.clear();
        welcomeTextView.setVisibility(View.VISIBLE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("PREF_LAST_CONVERSATION_ID", conversationID+1);
        editor.apply();
        conversationID++;
    }

    public static int getConversationID(){
        return conversationID;
    }

    public void initializeChat(String prompt, String userInput) {
        welcomeTextView.setVisibility(View.GONE);
        this.prompt = prompt;
        if(!userInput.isEmpty()){
            addToChat(userInput, Message.SENT_BY_ME);
        }
        mainActivity.decrementCredits();
        this.callAPI(prompt);
    }





    public void callAPI(String prompt){
        Log.d("MESSAGES", messageList.toString());

        messageList.add(new Message("Typing...", Message.SENT_BY_BOT, getConversationID()));
        //okhttp
        JSONObject jsonBody = new JSONObject();

        try {
            // Create JSON request body
            jsonBody.put("model", "gpt-3.5-turbo");


            JSONArray messageArr = new JSONArray();

            // Add previous conversation history
            //TODO: shorten this
            for (Message msg : messageList) {
                JSONObject messageObj = new JSONObject();
                messageObj.put("role", msg.getSentBy().equals(Message.SENT_BY_ME) ? "user" : "assistant");
                messageObj.put("content", msg.getMessage());
                messageArr.put(messageObj);
            }



            JSONObject obj = new JSONObject();
            obj.put("role", "user");
            obj.put("content", prompt);

            messageArr.put(obj);

            jsonBody.put("messages",messageArr);


        } catch (JSONException e) {
            e.printStackTrace();
            // Handle the exception or show an error message
        }

        RequestBody body = RequestBody.create(jsonBody.toString(),JSON);
        Request request = new Request.Builder()
                .url("\n"+"https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer ")
                .post(body)
                .build();


        mainActivity.client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    //response works
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else{
                    String errorBody = response.body().string();
                    Log.d("API_ERROR", "Error response: " + response.code() + " " + errorBody);
                    addResponse("Failed to load. Error response: " + response.code());                }
            }
        });

    }


    @Override
    public void onCopyClicked(String text) {
        mainActivity.onCopyClicked(text);
    }

    @Override
    public void onShareClicked(String text) {
        mainActivity.onShareClicked(text);
    }




}