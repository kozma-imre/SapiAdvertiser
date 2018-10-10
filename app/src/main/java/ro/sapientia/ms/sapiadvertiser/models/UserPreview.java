package ro.sapientia.ms.sapiadvertiser.models;
import com.google.firebase.database.PropertyName;

public class UserPreview {
    //using property name for mapping class properties with firebase fields,
    // if i dont use property names firebase will use my class fields name and i don`t want to use in firebase PascalCase
    @PropertyName("name")
    public String Name;

    @PropertyName("id")
    public String Id;

    @PropertyName("image_url")
    public String ImageUrl;
}
