package com.example.gptai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MessageAdapter.ChipClickListener{

    TextView creditsTextView;
    SharedPreferences sharedPreferences;
    //private static final long RESET_INTERVAL = TimeUnit.DAYS.toMillis(30);
    private static final long RESET_INTERVAL = TimeUnit.SECONDS.toMillis(60);
    ImageButton resetButton;
    FrameLayout resetButtonFrame;
    ImageButton trashButton;
    FrameLayout trashFrame;




    public ChatFragment chatFragment;
    private HistoryFragment historyFragment;
    private PresetFragment presetFragment;
    private SettingsFragment settingsFragment;
    private PurchaseFragment purchaseFragment;

    private String model = "gpt-3.5-turbo";

    BottomNavigationView bottomNavigationView;

    private LinearLayout creditsLayout;





    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();




    private Handler handler = new Handler();
    private Runnable resetCreditsTask = new Runnable() {
        @Override
        public void run() {
            resetCredits();
            creditsTextView.setText("10");

            // Schedule the next reset
            scheduleNextReset();
        }
    };




//    private void scheduleNextReset() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.add(Calendar.MILLISECOND, (int) RESET_INTERVAL);
//
//        long delay = calendar.getTimeInMillis() - System.currentTimeMillis();
//        Handler handler = new Handler();
//        handler.postDelayed(resetCreditsTask, delay);
//    }


    private void scheduleNextReset() {
        Handler handler = new Handler();
        handler.removeCallbacks(resetCreditsTask); // Cancel any existing task
        handler.postDelayed(resetCreditsTask, RESET_INTERVAL); // Schedule the next reset
    }

    private void resetCredits() {
        // Set the initial credits value if it's not present
        int initialCredits = 10; // Change this to the desired initial credits value
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("credits", initialCredits);
        editor.putInt("initial_credits", initialCredits);
        editor.apply();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Tag","oncreate");
        //WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //Change color status bar
        Resources resources = getResources();
        // Access a color value by its resource ID
        int statusBarColor = resources.getColor(R.color.light_gray);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(statusBarColor);


        String key = BuildConfig.KEY;

        chatFragment = new ChatFragment(MainActivity.this);
        historyFragment = new HistoryFragment(MainActivity.this);
        presetFragment = new PresetFragment(MainActivity.this);
        settingsFragment = new SettingsFragment();
        purchaseFragment = new PurchaseFragment();

        resetButton = findViewById(R.id.reset_btn);
        resetButtonFrame = findViewById(R.id.reset_layout);
        trashButton = findViewById(R.id.trash_btn);
        trashFrame= findViewById(R.id.trash_layout);
        creditsLayout = findViewById(R.id.credit_layout);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, chatFragment)
                .commit();

        //toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(""); // set the title to an empty string
        }



        creditsTextView = findViewById(R.id.credits_textview);



        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);



        //start with 10 credits
        if (!sharedPreferences.contains("initial_credits")) {
            // Set the initial credits value if it's not present
            int initialCredits = 10; // Change this to the desired initial credits value
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("credits",initialCredits);
            editor.putInt("initial_credits", initialCredits);
            editor.apply();
        }

        // Retrieve the current number of credits from SharedPreferences
        int initialCredits = sharedPreferences.getInt("initial_credits", 0);
        AtomicInteger credits = new AtomicInteger(sharedPreferences.getInt("credits", initialCredits));
        creditsTextView.setText(String.valueOf(credits.get()));

        scheduleNextReset();








        SharedPreferences.Editor editor = sharedPreferences.edit();








        resetButton.setOnClickListener((v)->{
            showConfirmationDialog();

        });

        trashButton.setOnClickListener((v)->{
            showClearConfirmationDialog();
        });

        creditsLayout.setOnClickListener((v)->{
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, purchaseFragment)
                    .commit();
        });




        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.chat_item);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.history_item:
                        resetButtonFrame.setVisibility(View.GONE);
                        trashFrame.setVisibility(View.VISIBLE);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, historyFragment)
                                .commit();
                        return true;
                    case R.id.chat_item:
                        resetButtonFrame.setVisibility(View.VISIBLE);
                        trashFrame.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, chatFragment)
                                .commit();
                        return true;
                    case R.id.preset_item:
                        resetButtonFrame.setVisibility(View.VISIBLE);
                        trashFrame.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, presetFragment)
                                .commit();
                        return true;
                    case R.id.settings_item:
                        resetButtonFrame.setVisibility(View.GONE);
                        trashFrame.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, settingsFragment)
                                .commit();
                        return true;
                    default:
                        return false;
                }
            }
        });



    }




    public void callAPI(String prompt){

        chatFragment.messageList.add(new Message("Typing...", Message.SENT_BY_BOT, chatFragment.getConversationID()));
        //okhttp
        JSONObject jsonBody = new JSONObject();

        try {
            // Create JSON request body
            jsonBody.put("model", "gpt-3.5-turbo");


            JSONArray messageArr = new JSONArray();
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
                //TODO: Switch to actual api key
                .header("Authorization","Bearer " + env.OPENAI_KEY)
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                chatFragment.addResponse("Failed to load response: " + e.getMessage());
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
                        chatFragment.addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else{
                    String errorBody = response.body().string();
                    Log.d("API_ERROR", "Error response: " + response.code() + " " + errorBody);
                    chatFragment.addResponse("Failed to load. Error response: " + response.code());                }
            }
        });

    }





    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete this conversation? This cannot be undone");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform the action here
                SharedPreferences.Editor editor = sharedPreferences.edit();

                chatFragment.clearMessages();
                ChatFragment.conversationID++;

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel the action or do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean hasEnoughCredits(){
        int credits = sharedPreferences.getInt("credits", 0);
        Log.d("Credit", credits+"");
        if(credits <= 0){
            return false;
        }
        else{
            return true;
        }
    }

    public int getCredits(){
        return sharedPreferences.getInt("credits", 0);
    }

    public void setCredits(int newCredits){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("credits", newCredits);
        editor.apply();
        creditsTextView.setText(String.valueOf(getCredits()));
    }

    public void decrementCredits(){
        int currentCredits = sharedPreferences.getInt("credits", 0);
        if(currentCredits != 0){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("credits", currentCredits-1);
            editor.apply();
            creditsTextView.setText(String.valueOf(getCredits()));
        }

    }


    public void openChatActivity(String prompt, String userInput) {
        ChatFragment chatFragment = new ChatFragment(MainActivity.this);
        getSupportFragmentManager().beginTransaction()
                .remove(chatFragment)
                .attach(chatFragment)
                .add(R.id.fragment_container, chatFragment)
                .commit();

        // Call the initializeChat method after the fragment is attached
        getSupportFragmentManager().executePendingTransactions();
        chatFragment.initializeChat(prompt, userInput);
    }

    public String getModel(){
        return model;
    }

    public void setModel(String model){
        this.model = model;
    }

    @Override
    public void onCopyClicked(String text) {
        // Perform the desired action when the copy chip is clicked
        // For example, you can copy the text to the clipboard
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("AI Response", text);
        clipboardManager.setPrimaryClip(clipData);

        // Show a toast or perform any other action to indicate that the text has been copied
        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShareClicked(String text) {
        // Perform the desired action when the share chip is clicked
        // For example, you can share the text through other apps
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, "Share AI Response"));
    }

    public void showClearConfirmationDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Clear Conversation History");
        builder.setMessage("This action cannot be undone. Are you sure you want to clear the conversation history?");
        builder.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call the method to clear the conversation history
                historyFragment.clearDB();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }












}