package co.za.wedwise.Models;

public class ReviewModel {
    String name,message;
    float review;

    public ReviewModel(String name, String message, float review) {
        this.name = name;
        this.message = message;
        this.review = review;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public float getReview() {
        return review;
    }

    public void setReview(float review) {
        this.review = review;
    }
}
