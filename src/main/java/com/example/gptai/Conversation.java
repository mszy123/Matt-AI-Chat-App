package com.example.gptai;

import java.util.ArrayList;

public class Conversation {
    private ArrayList<Message> conversation;
    private int conversationID;
    private static int lastAssignedID = 0;


    public Conversation(ArrayList<Message> conversation) {
        this.conversation = conversation;
        this.conversationID = ++lastAssignedID;
    }

    public ArrayList<Message> getConversation() {
        return conversation;
    }

    public ArrayList<String> getTextMessages(){
        ArrayList<String> toReturn = new ArrayList<>();
        for(Message msg : conversation){
            toReturn.add(msg.getMessage());
        }
        return toReturn;
    }

    public void addMessage(Message msg){
        conversation.add(msg);
    }

    public int getConversationID() {
        return conversationID;
    }

    public void setConversationID(int conversationID) {
        this.conversationID = conversationID;
    }
}
