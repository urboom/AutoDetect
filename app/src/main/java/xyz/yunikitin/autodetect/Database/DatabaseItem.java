package xyz.yunikitin.autodetect.Database;


public class DatabaseItem  {

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("plateNumber")
    private String mNumberPlate;

    @com.google.gson.annotations.SerializedName("brandAuto")
    private String mBrandAuto;

    @com.google.gson.annotations.SerializedName("colorAuto")
    private String mColorAuto;

    @com.google.gson.annotations.SerializedName("city")
    private String mCity;

    @com.google.gson.annotations.SerializedName("phone")
    private String mPhone;

    @com.google.gson.annotations.SerializedName("email")
    private String mEmail;

    @com.google.gson.annotations.SerializedName("event")
    private String mEvent;

    @com.google.gson.annotations.SerializedName("complete")
    private boolean mComplete;

    public DatabaseItem() {

    }

    @Override
    public String toString() {
        return getNumberPlate();
    }

    public DatabaseItem(String id, String numberPlate, String brandAuto) {
        this.setId(id);
        this.setNumberPlate(numberPlate);
        this.setBrandAuto(brandAuto);
    }

    public String getNumberPlate() {
        return mNumberPlate;
    }

    public void setNumberPlate(String mNumberPlate) {
        this.mNumberPlate = mNumberPlate;
    }

    public String getBrandAuto() {
        return mBrandAuto;
    }

    public void setBrandAuto(String mBrandAuto) {
        this.mBrandAuto = mBrandAuto;
    }

    public String getColorAuto() {
        return mColorAuto;
    }

    public void setColorAuto(String mColorAuto) {
        this.mColorAuto = mColorAuto;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String mCity) {
        this.mCity = mCity;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getEvent() {
        return mEvent;
    }

    public void setEvent(String mEvent) {
        this.mEvent = mEvent;
    }

    public String getId() {
        return mId;
    }

    public final void setId(String id) {
        mId = id;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DatabaseItem && ((DatabaseItem) o).mId == mId;
    }
}