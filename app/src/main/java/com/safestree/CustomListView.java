package com.safestree;

public class CustomListView {
    private String contactName;
    private String contactNumber;

    private int contactImage;

//    private boolean emergency;

    public CustomListView(int ic_person_background, String contactName, String contactNumber) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.contactImage=ic_person_background;
    }


    public String getContactName() {
        return this.contactName;
    }

    public String getContactNumber() {
        return this.contactNumber;
    }

    public int getContactImage(){return this.contactImage;}
//    public boolean isEmergency(){return this.emergency;}
}
