package com.example.gptai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private ArrayList<ArrayList<Message>> conversationList;

    public ConversationAdapter(ArrayList<ArrayList<Message>> conversationList) {
        this.conversationList = conversationList;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_history, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        ArrayList<Message> messages = conversationList.get(position);
        // Update the ViewHolder with the messages for this conversationID
        holder.bind(messages);
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder {
        private TextView conversationIdTextView;
        private LinearLayout messageContainer;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            //conversationIdTextView = itemView.findViewById(R.id.conversation_id_text_view);
            messageContainer = itemView.findViewById(R.id.message_container);
        }

        public void bind(ArrayList<Message> messages) {
            // Display the conversationID
            //conversationIdTextView.setText(messages.get(0).getConversationID());

            // Clear the message container
            messageContainer.removeAllViews();

            // Add each message to the message container
            for (Message message : messages) {
                View messageView;
                if(message.getSentBy().equals(Message.SENT_BY_ME)){
                    messageView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.user_conversation_item, messageContainer, false);
                    TextView messageTextView = messageView.findViewById(R.id.message_text_view);
                    messageTextView.setText(message.getMessage());
                }
                else{
                    messageView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.bot_conversation_item, messageContainer, false);
                    TextView messageTextView = messageView.findViewById(R.id.message_text_view);
                    messageTextView.setText(message.getMessage());
                }
                messageContainer.addView(messageView);
            }
        }
    }
}

