package wannabit.io.ringowallet.task.balance;

import android.os.Bundle;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.http.HttpService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.task.TaskCallback;
import wannabit.io.ringowallet.task.TaskResult;
import wannabit.io.ringowallet.utils.WLog;

public class ErcBalanceTask extends BalanceCheckByKeyTask {
    private BaseApplication     mApp;
    private TaskCallback        mCallback;
    private TaskResult          mResult;
    private ArrayList<Key>      mKeys;

    public ErcBalanceTask(BaseApplication app, TaskCallback mCallback, ArrayList<Key> keys) {
        this.mApp       = app;
        this.mCallback  = mCallback;
        this.mKeys      = keys;

        this.mResult = new TaskResult();
        this.mResult.taskType = BaseConstant.TASK_BALANCE;
        this.mResult.resultData3 = mKeys.get(0).type;
    }

    @Override
    protected TaskResult doInBackground(Bundle... bundles) {
        Web3j web3 = Web3jFactory.build(new HttpService("https://mainnet.infura.io/"));
        try {
            for(Key key : mKeys) {
                Function function = new Function(
                        "balanceOf",
                        Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(key.address)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));

                String encodedFunction = FunctionEncoder.encode(function);
                EthCall response = web3.ethCall(Transaction.createEthCallTransaction(key.address, mApp.getBaseDao().onSelectTokenBySymbol(key.symbol).contractAddr , encodedFunction), DefaultBlockParameterName.LATEST).sendAsync().get();
                List<Type> someTypes = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());

                BigDecimal balance = BigDecimal.ZERO;
                if(someTypes != null && someTypes.size() > 0) {
                    for(Type type: someTypes) {
                        balance = balance.add(new BigDecimal(type.getValue().toString()));
                    }
                }
                mApp.getBaseDao().onUpdateBalance(key.uuid, balance.toPlainString());
            }
            mResult.isSuccess = true;

        } catch (Exception e) {
            WLog.w("ErcBalanceTask Exception : "  + e.getMessage());
            mResult.resultMsg = e.getMessage();

        }

        return mResult;
    }

    @Override
    protected void onPostExecute(TaskResult result) {
        super.onPostExecute(result);
        mCallback.onTaskResponse(result);
    }
}
