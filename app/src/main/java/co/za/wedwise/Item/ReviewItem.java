
package co.za.wedwise.Item;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReviewItem {

    @SerializedName("propid")
    @Expose
    private String propid;
    @SerializedName("review")
    @Expose
    private String review;
    @SerializedName("user_name")
    @Expose
    private String user_name;
    @SerializedName("imageprofile")
    @Expose
    private String imageprofile;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("rate")
    @Expose
    private String rate;
    @SerializedName("daterate")
    @Expose
    private String daterate;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("featured")
    @Expose
    private String featured;
    @SerializedName("totalrate")
    @Expose
    private String totalrate;
    @SerializedName("totalviews")
    @Expose
    private String totalviews;

    public String getPropid() {
        return propid;
    }

    public void setPropid(String propid) {
        this.propid = propid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDaterate() {
        return daterate;
    }

    public void setDaterate(String daterate) {
        this.daterate = daterate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFeatured() {
        return featured;
    }

    public void setFeatured(String featured) {
        this.featured = featured;
    }

    public String getTotalrate() {
        return totalrate;
    }

    public void setTotalrate(String totalrate) {
        this.totalrate = totalrate;
    }

    public String getTotalviews() {
        return totalviews;
    }

    public void setTotalviews(String totalviews) {
        this.totalviews = totalviews;
    }


    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getImageprofile() {
        return imageprofile;
    }

    public void setImageprofile(String imageprofile) {
        this.imageprofile = imageprofile;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
