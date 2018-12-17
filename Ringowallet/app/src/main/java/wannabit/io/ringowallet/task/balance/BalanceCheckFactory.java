package wannabit.io.ringowallet.task.balance;

import java.util.ArrayList;

import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.task.TaskCallback;
import wannabit.io.ringowallet.utils.WLog;

public class BalanceCheckFactory {

    public static BalanceCheckByKeyTask getBalance(BaseApplication app, TaskCallback callback, ArrayList<Key> keys) {
        if (keys.get(0).type.equals(BaseConstant.COIN_BTC)) {
            return new BtcBalanceTask(app, callback, keys);

        } else if (keys.get(0).type.equals(BaseConstant.COIN_LTC)) {
            return new LtcBalanceTask(app, callback, keys);

        } else if (keys.get(0).type.equals(BaseConstant.COIN_BCH)) {
            return new BchBalanceTask(app, callback, keys);

        } else if (keys.get(0).type.equals(BaseConstant.COIN_BSV)) {
            return null;

        } else if (keys.get(0).type.equals(BaseConstant.COIN_ETH)) {
            return new EthBalanceTask(app, callback, keys);

        } else if (keys.get(0).type.equals(BaseConstant.COIN_ETC)) {
            return new EtcBalanceTask(app, callback, keys);

        } else if (keys.get(0).type.equals(BaseConstant.COIN_QTUM)) {
            return new QtumBalanceTask(app, callback, keys);

        } else if (keys.get(0).type.equals(BaseConstant.COIN_ERC20)) {
            return new ErcBalanceTask(app, callback, keys);

        } else if (keys.get(0).type.equals(BaseConstant.COIN_ERC20)) {
            return null;

        }  else {
            return null;
        }
    }
}
