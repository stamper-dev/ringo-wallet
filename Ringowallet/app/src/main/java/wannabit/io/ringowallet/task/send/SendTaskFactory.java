package wannabit.io.ringowallet.task.send;

import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.task.TaskCallback;

public class SendTaskFactory {

    public static SendTask getSendTask(BaseApplication app, TaskCallback callback, String type) {
        if (type.equals(BaseConstant.COIN_BTC)) {
            return new BtcSendTask(app, callback);

        } else if (type.equals(BaseConstant.COIN_LTC)) {
            return new LtcSendTask(app, callback);

        } else if (type.equals(BaseConstant.COIN_BCH)) {
            return new BchSendTask(app, callback);

        } else if (type.equals(BaseConstant.COIN_BSV)) {
            return null;

        } else if (type.equals(BaseConstant.COIN_ETH)) {
            return new EthSendTask(app, callback);

        } else if (type.equals(BaseConstant.COIN_ETC)) {
            return new EtcSendTask(app, callback);

        } else if (type.equals(BaseConstant.COIN_ERC20)) {
            return new Erc20SendTask(app, callback);

        }  else if (type.equals(BaseConstant.COIN_QTUM)) {
            return new QtumSendTask(app, callback);

        }else {
            //TODO
            return null;
        }
    }
}
