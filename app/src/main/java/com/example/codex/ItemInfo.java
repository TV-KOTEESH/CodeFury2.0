package com.example.codex;

public class ItemInfo {

    String mItemName;
    String mItemDesc;
    String mItemPrice;
    String mItemId;
    String mContact;

    public ItemInfo(String itemName, String itemDesc, String itemPrice, String itemId, String contact) {
        mItemName = itemName;
        mItemDesc = itemDesc;
        mItemPrice = itemPrice;
        mItemId = itemId;
        mContact = contact;
    }

    public String getItemName(){
        return mItemName;
    }

    public String getItemDesc(){
        return mItemDesc;
    }

    public String getmItemPrice(){
        return mItemPrice;
    }

    public String getmContact(){
        return mContact;
    }
}
