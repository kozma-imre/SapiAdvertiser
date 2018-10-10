package ro.sapientia.ms.sapiadvertiser.models;
import com.google.firebase.database.PropertyName;


public class User {
    //using property name for mapping class properties with firebase fields,
    // if i dont use property names firebase will use my class fields name and i don`t want to use in firebase PascalCase
    @PropertyName("first_name")
    public String FirstName;

    @PropertyName("last_name")
    public String LastName;

    @PropertyName("email")
    public String Email;

    @PropertyName("phone_number")
    public String PhoneNumber;

    @PropertyName("address")
    public String Address;

    @PropertyName("image_url")
    public String ImageUrl;

    @PropertyName("id")
    public String Id;

}
