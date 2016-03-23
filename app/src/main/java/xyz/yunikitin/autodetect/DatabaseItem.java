package xyz.yunikitin.autodetect;


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

@com.google.gson.annotations.SerializedName("complete")
private boolean mComplete;

    public DatabaseItem() {

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

    public String getId() {
        return mId;
    }

    public final void setId(String id) {
        mId = id;
    }

    public boolean isComplete() {
        return mComplete;
    }

    public void setComplete(boolean complete) {
        mComplete = complete;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DatabaseItem && ((DatabaseItem) o).mId == mId;
    }
}