package wannabit.io.ringowallet.network.req;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import wannabit.io.ringowallet.model.WBInputDtoList;
import wannabit.io.ringowallet.model.WBOutputDtoList;

public class ReqCreateRawTx {

    @SerializedName("inputDtoList")
    ArrayList<WBInputDtoList> inputDtoList;

    @SerializedName("outputDtoList")
    ArrayList<WBOutputDtoList> outputDtoList;

    public ArrayList<WBInputDtoList> getInputDtoList() {
        return inputDtoList;
    }

    public void setInputDtoList(ArrayList<WBInputDtoList> inputDtoList) {
        this.inputDtoList = inputDtoList;
    }

    public ArrayList<WBOutputDtoList> getOutputDtoList() {
        return outputDtoList;
    }

    public void setOutputDtoList(ArrayList<WBOutputDtoList> outputDtoList) {
        this.outputDtoList = outputDtoList;
    }

    public void addInputList(WBInputDtoList input) {
        if(inputDtoList == null)
            inputDtoList = new ArrayList<>();
        inputDtoList.add(input);

    }

    public void addInputListAll(ArrayList<WBInputDtoList> inputs) {
        if(inputDtoList == null)
            inputDtoList = new ArrayList<>();

        for(WBInputDtoList input : inputs) {
            addInputList(input);
        }
    }

    public void addOutputList(WBOutputDtoList output) {
        if(outputDtoList == null)
            outputDtoList = new ArrayList<>();
        outputDtoList.add(output);

    }

    public void addOutputputListAll(ArrayList<WBOutputDtoList> outputs) {
        if(outputDtoList == null)
            outputDtoList = new ArrayList<>();

        for(WBOutputDtoList output : outputs) {
            addOutputList(output);
        }
    }
}
