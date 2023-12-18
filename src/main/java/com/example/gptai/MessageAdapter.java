package com.example.gptai;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder>{

    List<Message> messageList;
    private ChipClickListener chipClickListener;
    int viewType;


    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.viewType = viewType;
        View chatView;
        if (viewType == 0) {
            chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_chat_item, parent, false);
        } else {
            chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ai_chat_item, parent, false);
        }
        MyViewHolder myViewHolder = new MyViewHolder(chatView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Message message = messageList.get(position);
        if(message.getSentBy().equals(Message.SENT_BY_ME)){
            //holder.leftChatView.setVisibility(View.GONE);
            //holder.rightChatView.setVisibility(View.VISIBLE);
            holder.userPrompt.setText(message.getMessage());
        }
        else{
            //holder.leftChatView.setVisibility(View.VISIBLE);
           // holder.rightChatView.setVisibility(View.GONE);
            holder.response.setText(message.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if(message.getSentBy().equals(Message.SENT_BY_ME)){
            return 0;
        }
        else{
            return 1;
        }
    }

    public int getMessageType(int position) {
        Message message = messageList.get(position);
        if(message.getSentBy().equals(Message.SENT_BY_ME)){
            return 0;
        }
        else{
            return 1;
        }
    }

    public void setChipClickListener(ChipClickListener listener) {
        this.chipClickListener = listener;
    }

    // Implement the getItem method to retrieve the data item at a specific position
    public String getItem(int position) {
        Message message = messageList.get(position);
        return message.getMessage();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView response, userPrompt;
        private Chip copyChip;
        private Chip shareChip;
        private ChipGroup chipActions;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            response = itemView.findViewById(R.id.ai_response);
            userPrompt = itemView.findViewById(R.id.user_prompt);

            if(viewType == 1){
                chipActions = itemView.findViewById(R.id.chip_group);
                copyChip = itemView.findViewById(R.id.copy_chip);
                shareChip = itemView.findViewById(R.id.share_chip);
                Log.d("Initalized", "Share chip initalized");

                copyChip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do something when the share chip is clicked.
                        String text = getItem(getAdapterPosition());
                        chipClickListener.onCopyClicked(text);
                    }
                });

                shareChip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do something when the share chip is clicked.
                        Log.d("Share", "Share chip clicked");
                        String text = getItem(getAdapterPosition());
                        chipClickListener.onShareClicked(text);
                    }
                });
            }





        }






    }


    public interface ChipClickListener {
        void onCopyClicked(String text);
        void onShareClicked(String text);
    }

}
