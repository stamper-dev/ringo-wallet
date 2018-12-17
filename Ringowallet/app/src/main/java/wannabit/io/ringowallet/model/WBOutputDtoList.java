package wannabit.io.ringowallet.model;

import com.google.gson.annotations.SerializedName;

public class WBOutputDtoList {

    @SerializedName("address")
    String address;

    @SerializedName("value")
    String value;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
