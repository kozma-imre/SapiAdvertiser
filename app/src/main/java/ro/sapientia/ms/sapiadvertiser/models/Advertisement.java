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
