package ro.sapientia.ms.sapiadvertiser.models;

import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.Date;

public class Advertisement {

    @PropertyName("image_urls")
    public ArrayList<String> ImageUrls;
    //using property name for mapping class properties with firebase fields,
    // if i dont use property names firebase will use my class fields name and i don`t want to use in firebase PascalCase
    @PropertyName("title") // firebase field name (snake_case)
    public String Title; // (PascalCase)

    @PropertyName("short_description")
    public String ShortDescription;

    @PropertyName("long_description")
    public String LongDescription;

    @PropertyName("phone_number")
    public String PhoneNumber;

    @PropertyName("location")
    public String Location;

    @PropertyName("nr_views")
    public int NumberOfViews;

    @PropertyName("is_deleted")
    public boolean IsDeleted;

    @PropertyName("is_reported")
    public boolean IsReported;

    @PropertyName("created_time")
    public Date CreatedTime;

    // including essential user data for displaying the user even if this means duplicate data and
    // adds more logic at profile update, because at advertise listing no need for more requests
    @PropertyName("creator_user")
    public UserPreview CreatorUser;

    @PropertyName("id")
    public String Id;

   /* public ArrayList<String> getImageUrls() {
        return ImageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        ImageUrls = imageUrls;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getShortDescription() {
        return ShortDescription;
    }

    public void setShortDescription(String shortDescription) {
        ShortDescription = shortDescription;
    }

    public String getLongDescription() {
        return LongDescription;
    }

    public void setLongDescription(String longDescription) {
        LongDescription = longDescription;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public int getNumberOfViews() {
        return NumberOfViews;
    }

    public void setNumberOfViews(int numberOfViews) {
        NumberOfViews = numberOfViews;
    }

    public boolean isDeleted() {
        return IsDeleted;
    }

    public void setDeleted(boolean deleted) {
        IsDeleted = deleted;
    }

    public boolean isReported() {
        return IsReported;
    }

    public void setReported(boolean reported) {
        IsReported = reported;
    }

    public Date getCreatedTime() {
        return CreatedTime;
    }

    public void setCreatedTime(Date createdTime) {
        CreatedTime = createdTime;
    }

    public UserPreview getCreatorUser() {
        return CreatorUser;
    }

    public void setCreatorUser(UserPreview creatorUser) {
        CreatorUser = creatorUser;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
    */

    public Advertisement() {
    }

    public Advertisement(ArrayList<String> imageUrls, String title, String shortDescription, String longDescription, String phoneNumber, String location, int numberOfViews, boolean isDeleted, boolean isReported, Date createdTime, UserPreview creatorUser, String id) {
        ImageUrls = imageUrls;
        Title = title;
        ShortDescription = shortDescription;
        LongDescription = longDescription;
        PhoneNumber = phoneNumber;
        Location = location;
        NumberOfViews = numberOfViews;
        IsDeleted = isDeleted;
        IsReported = isReported;
        CreatedTime = createdTime;
        CreatorUser = creatorUser;
        Id = id;
    }




}
