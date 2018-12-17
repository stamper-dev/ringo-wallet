package wannabit.io.ringowallet.base;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.math.BigDecimal;
import java.util.ArrayList;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.crypto.CryptoHelper;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.model.Mnemonic;
import wannabit.io.ringowallet.model.Password;
import wannabit.io.ringowallet.model.Price;
import wannabit.io.ringowallet.model.Support;
import wannabit.io.ringowallet.model.Token;
import wannabit.io.ringowallet.model.Tx;
import wannabit.io.ringowallet.model.WalletItem;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;

public class BaseDao {

    private BaseApplication     mBaseApplication;
    private SharedPreferences   mSharedPreferences;
    private SQLiteDatabase      mSQLiteDatabase;

    public BaseDao(BaseApplication apps) {
        this.mBaseApplication = apps;
        this.mSharedPreferences = getSharedPreferences();
        SQLiteDatabase.loadLibs(mBaseApplication);
    }

    private SharedPreferences getSharedPreferences() {
        if(mSharedPreferences == null)
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mBaseApplication);
        return mSharedPreferences;
    }

    public SQLiteDatabase getUserDB() {
        if(mSQLiteDatabase == null) {
            mSQLiteDatabase = BaseDataBase.getInstance(mBaseApplication).getWritableDatabase(mBaseApplication.getString(R.string.db_pw));
        }
        return mSQLiteDatabase;
    }


    public boolean hasPw() {
        boolean result = false;
        try {
            result = onHasPassword() && CryptoHelper.isKeystoreContainAlias(BaseConstant.PASSWORD_KEY);
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public boolean hasAnyData(){
        return  onHasMnemonic() || hasAnyKey();
    }

    public void setStep(int step) {
        getSharedPreferences().edit().putInt(BaseConstant.PREFER_STEP, step).commit();
    }

    public int getStep() {
        return getSharedPreferences().getInt(BaseConstant.PREFER_STEP, 0);
    }

    public void setUsingFingerprint(boolean using) {
        getSharedPreferences().edit().putBoolean(BaseConstant.SET_USE_FINGERPRINT, using).commit();
    }

    public boolean getUsingFingerprint() {
        return getSharedPreferences().getBoolean(BaseConstant.SET_USE_FINGERPRINT, false);
    }

    public void setLockTime(int time) {
        getSharedPreferences().edit().putInt(BaseConstant.SET_USE_LOCK_TIME, time).commit();
    }

    public int getLockTime() {
        return getSharedPreferences().getInt(BaseConstant.SET_USE_LOCK_TIME, 1);
    }

    public void setErcVersion(int value) {
        getSharedPreferences().edit().putInt(BaseConstant.CONFIG_ERC20_VERSION, value).commit();
    }

    public int getErcVersion() {
        return getSharedPreferences().getInt(BaseConstant.CONFIG_ERC20_VERSION, 1);
    }

    public void setQrcVersion(int value) {
        getSharedPreferences().edit().putInt(BaseConstant.CONFIG_QRC20_VERSION, value).commit();
    }

    public int getQrcVersion() {
        return getSharedPreferences().getInt(BaseConstant.CONFIG_QRC20_VERSION, 1);
    }

    public void setDecimal(int value) {
        getSharedPreferences().edit().putInt(BaseConstant.PREFER_DECIMAL, value).commit();
    }

    public int getDecimal() {
        return getSharedPreferences().getInt(BaseConstant.PREFER_DECIMAL, 1);
    }

    public void setCurrency(int value) {
        getSharedPreferences().edit().putInt(BaseConstant.PREFER_CURRENCY, value).commit();
    }

    public int getCurrency() {
        return getSharedPreferences().getInt(BaseConstant.PREFER_CURRENCY, 0);
    }

    public void setSupport(Support support) {
        String jsonText = new Gson().toJson(support);
        getSharedPreferences().edit().putString(BaseConstant.PREFER_SUPPORT, jsonText).commit();
    }

    public Support getSupport(){
        Support result = null;
        try {
            String jsonText = getSharedPreferences().getString(BaseConstant.PREFER_SUPPORT, null);
            result = new Gson().fromJson(jsonText, Support.class);
        }catch (Exception e) {
        }
        return result;
    }

    public void setLeaveTime(long time) {
        getSharedPreferences().edit().putLong(BaseConstant.PREFER_LEAVE_TIME, time).commit();
    }

    public long getLeaveTime() {
        return getSharedPreferences().getLong(BaseConstant.PREFER_LEAVE_TIME, 0l);
    }



    public Password onSelectPassword() {
        Password result = null;
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_PASSWORD, new String[]{"resource", "spec", "bio"}, null, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            result = new Password(cursor.getString(0), cursor.getString(1), cursor.getInt(2) == 1 ? true : false);
        }
        cursor.close();
        return result;
    }

    public boolean onHasPassword() {
        boolean existed = false;
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_PASSWORD, new String[]{"resource", "spec", "bio"}, null, null, null, null, null);
        if(cursor != null && cursor.getCount() > 0) {
            existed = true;
        }
        cursor.close();
        return existed;
    }

    public long onInsertPassword(Password password) {
        long result = -1;
        if(onHasMnemonic()) return result;

        ContentValues values = new ContentValues();
        values.put("resource",  password.getResource());
        values.put("spec",      password.getSpec());
        values.put("bio",       password.isUsingBio() == true ? 1: 0);
        return getUserDB().insertOrThrow(BaseConstant.DB_TABLE_PASSWORD, null, values);
    }



    public Mnemonic onSelectMnemonic() {
        Mnemonic result = null;
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_MNEMONIC, new String[]{"id", "uuid", "resource", "spec"}, null, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            result = new Mnemonic(cursor.getLong(0), cursor.getString(1) , cursor.getString(2), cursor.getString(3));
        }
        cursor.close();
        return result;
    }

    public boolean onHasMnemonic() {
        boolean existed = false;
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_MNEMONIC, new String[]{"id", "uuid", "resource", "spec"}, null, null, null, null, null);
        if(cursor != null && cursor.getCount() > 0) {
            existed = true;
        }
        cursor.close();
        return existed;
    }

    public long onInsertMnemonic(Mnemonic mnemonic) {
        long result = -1;
        if(onHasMnemonic()) return result;

        ContentValues values = new ContentValues();
        values.put("uuid",  mnemonic.getUuid());
        values.put("resource",  mnemonic.getResource());
        values.put("spec",  mnemonic.getSpec());
        return getUserDB().insertOrThrow(BaseConstant.DB_TABLE_MNEMONIC, null, values);
    }


    public Key onSelectKey(String uuid) {
        Key result = null;
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_KEY, new String[]{"id", "uuid", "type", "symbol", "path", "isRaw", "resource", "spec", "address", "isFavo", "lastBalance", "dateX"}, "uuid == ?", new String[]{"" + uuid}, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            result = new Key(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5) > 0 ? true: false,
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getInt(9) > 0 ? true: false,
                    new BigDecimal(cursor.getString(10)),
                    cursor.getLong(11));
        }
        cursor.close();
        return result;
    }

    public ArrayList<Key> onSelectAllKeys() {
        ArrayList<Key> result = new ArrayList<>();
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_KEY, new String[]{"id", "uuid", "type", "symbol", "path", "isRaw", "resource", "spec", "address", "isFavo", "lastBalance", "dateX"}, null, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            do {
                Key key = new Key(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getInt(5) > 0 ? true: false,
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getInt(9) > 0 ? true: false,
                        new BigDecimal(cursor.getString(10)),
                        cursor.getLong(11));
                result.add(key);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public ArrayList<Key> onSelectAllFavoKeys() {
        ArrayList<Key> result = new ArrayList<>();
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_KEY, new String[]{"id", "uuid", "type", "symbol", "path", "isRaw", "resource", "spec", "address", "isFavo", "lastBalance", "dateX"}, "isFavo > ?", new String[]{"0"}, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            do {
                Key key = new Key(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getInt(5) > 0 ? true: false,
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getInt(9) > 0 ? true: false,
                        new BigDecimal(cursor.getString(10)),
                        cursor.getLong(11));
                result.add(key);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public ArrayList<Key> onSelectAllRawKeys() {
        ArrayList<Key> result = new ArrayList<>();
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_KEY, new String[]{"id", "uuid", "type", "symbol", "path", "isRaw", "resource", "spec", "address", "isFavo", "lastBalance", "dateX"}, "isRaw > ?", new String[]{"0"}, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            do {
                Key key = new Key(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getInt(5) > 0 ? true: false,
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getInt(9) > 0 ? true: false,
                        new BigDecimal(cursor.getString(10)),
                        cursor.getLong(11));
                result.add(key);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public ArrayList<Key> onSelectAllDeterministicKeys() {
        ArrayList<Key> result = new ArrayList<>();
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_KEY, new String[]{"id", "uuid", "type", "symbol", "path", "isRaw", "resource", "spec", "address", "isFavo", "lastBalance", "dateX"}, "isRaw == ?", new String[]{"0"}, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            do {
                Key key = new Key(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getInt(5) > 0 ? true: false,
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getInt(9) > 0 ? true: false,
                        new BigDecimal(cursor.getString(10)),
                        cursor.getLong(11));
                result.add(key);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public ArrayList<Key> onSelectKeysByTypeNext(String type, String symbol) {
        ArrayList<Key> result = new ArrayList<>();
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_KEY, new String[]{"id", "uuid", "type", "symbol", "path", "isRaw", "resource", "spec", "address", "isFavo", "lastBalance", "dateX"}, "type == ? AND symbol == ? AND isRaw == ?", new String[]{type, symbol , ""+0}, null, null, "path DESC");
        if(cursor != null && cursor.moveToFirst()) {
            do {
                Key key = new Key(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getInt(5) > 0 ? true: false,
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getInt(9) > 0 ? true: false,
                        new BigDecimal(cursor.getString(10)),
                        cursor.getLong(11));
                result.add(key);
            }while (cursor.moveToNext());
        }
        cursor.close();
        WLog.w("onSelectKeysByTypeNext : " + result.size());
        return result;
    }

    public ArrayList<Key> onSelectKeysByType(String type, String symbol) {
        ArrayList<Key> result = new ArrayList<>();
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_KEY, new String[]{"id", "uuid", "type", "symbol", "path", "isRaw", "resource", "spec", "address", "isFavo", "lastBalance", "dateX"}, "type == ? AND symbol == ?", new String[]{type, symbol}, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            do {
                Key key = new Key(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getInt(5) > 0 ? true: false,
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getInt(9) > 0 ? true: false,
                        new BigDecimal(cursor.getString(10)),
                        cursor.getLong(11));
                result.add(key);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public boolean hasAnyKey() {
        if(onSelectAllKeys().size() > 0) return true;
        else return false;
    }

    public long onInsertKey(Key key) {
        ContentValues values = new ContentValues();
        values.put("uuid",          key.uuid);
        values.put("type",          key.type);
        values.put("symbol",        key.symbol);
        values.put("path",          key.path);
        values.put("isRaw",         key.isRaw ? 1 : 0);
        values.put("resource",      key.resource);
        values.put("spec",          key.spec);
        values.put("address",       key.address);
        values.put("isFavo",        key.isFavo ? 1: 0);
        values.put("lastBalance",   key.lastBalance.toPlainString());
        values.put("dateX",         key.genDate);
        return getUserDB().insertOrThrow(BaseConstant.DB_TABLE_KEY, null, values);
    }

    public long onToggleFavo(String uuid, boolean favo) {
        ContentValues values = new ContentValues();
        values.put("isFavo",    favo ? 1: 0);
        return getUserDB().update(BaseConstant.DB_TABLE_KEY, values, "uuid = ?", new String[]{""+uuid} );
    }

    public long onUpdateKey(Key key) {
        ContentValues values = new ContentValues();
        values.put("uuid",          key.uuid);
        values.put("type",          key.type);
        values.put("symbol",        key.symbol);
        values.put("path",          key.path);
        values.put("isRaw",         key.isRaw ? 1 : 0);
        values.put("resource",      key.resource);
        values.put("spec",          key.spec);
        values.put("address",       key.address);
        values.put("isFavo",        key.isFavo ? 1: 0);
        values.put("lastBalance",   key.lastBalance.toPlainString());
        values.put("dateX",         key.genDate);
        return getUserDB().update(BaseConstant.DB_TABLE_KEY, values, "uuid = ?", new String[]{""+key.uuid} );
    }

    public long onUpdateBalance(String uuid, String balance) {
//        WLog.w("onUpdateBalance : " + uuid + "  " + balance);
        ContentValues values = new ContentValues();
        values.put("lastBalance",   balance);
        return getUserDB().update(BaseConstant.DB_TABLE_KEY, values, "uuid = ?", new String[]{""+uuid} );
    }

    public boolean isExistingKey(String type, String symbol, String address) {
        boolean existed = false;
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_KEY, new String[]{"id", "uuid", "type", "symbol", "path", "isRaw", "resource", "spec", "address", "isFavo", "lastBalance"}, "type == ? AND symbol == ? AND address == ?", new String[]{type, symbol, address}, null, null, null);
        if(cursor != null && cursor.getCount() > 0) {
            existed = true;
        }
        cursor.close();
        return existed;
    }



    public int getNextPath(String type, String symbol) {
        int result = 0;
        ArrayList<Key> list = onSelectKeysByTypeNext(type, symbol);
        if(list.size() > 0) {
            return list.get(0).path + 1;
        }
        return result;
    }


    public long onDeletePrivateKey(String uuid) {
        onDeleteTx(uuid);
        return getUserDB().delete(BaseConstant.DB_TABLE_KEY, "uuid = ?", new String[]{uuid});
    }

    public ArrayList<WalletItem> getInitWalletItems() {
        ArrayList<WalletItem>  result  = new ArrayList<>();
        ArrayList<Key>         keys    = onSelectAllKeys();
        for(Key key:keys) {
            boolean already = false;
            for(WalletItem item: result) {
                if(item.symbol.equals(key.symbol)) {
                    already = true;
                    item.addKey(key);
                    break;
                }
            }
            if(!already) {
                if(WUtils.isMainCoin(key.symbol)) {
                    WalletItem newItem = WUtils.getBaseWalletItem(key.symbol);
                    newItem.addKey(key);
                    result.add(newItem);

                } else {
                    Token token   = onSelectTokenBySymbol(key.symbol);
                    WalletItem newItem = new WalletItem(token.name, token.symbol, token.type, token.decimals, token.iconUrl, -1, token.contractAddr);
                    newItem.addKey(key);
                    result.add(newItem);
                }

            }
        }
        return result;
    }





    public ArrayList<Token> onSelectAllTokens() {
        ArrayList<Token> result = new ArrayList<>();
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_TOKEN, new String[]{ "name", "symbol","type", "decimals", "contractAddr", "iconUrl"}, null, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            do {
                Token temp = new Token(cursor.getString(0),
                                        cursor.getString(1),
                                        cursor.getString(2),
                                        cursor.getInt(3),
                                        cursor.getString(4),
                                        cursor.getString(5));
                result.add(temp);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public ArrayList<Token> onSelectErcTokens() {
        ArrayList<Token> result = new ArrayList<>();
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_TOKEN, new String[]{ "name", "symbol","type", "decimals", "contractAddr", "iconUrl"}, "type == ?", new String[]{""+BaseConstant.COIN_ERC20}, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            do {
                Token temp = new Token(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5));
                result.add(temp);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public ArrayList<Token> onSelectQrcTokens() {
        ArrayList<Token> result = new ArrayList<>();
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_TOKEN, new String[]{ "name", "symbol","type", "decimals", "contractAddr", "iconUrl"}, "type == ?", new String[]{""+BaseConstant.COIN_QRC20}, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            do {
                Token temp = new Token(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5));
                result.add(temp);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public Token onSelectTokenBySymbol(String symbol) {
        Token result = new Token();
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_TOKEN, new String[]{"name", "symbol", "type", "decimals", "contractAddr", "iconUrl"}, "symbol == ?", new String[]{"" + symbol}, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            result = new Token(cursor.getString(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getInt(3),
                                cursor.getString(4),
                                cursor.getString(5));
        }
        cursor.close();
        return result;
    }

    public long onInsertToken(Token token) {
        ContentValues values = new ContentValues();
        values.put("name",              token.name);
        values.put("symbol",            token.symbol);
        if (token.type.equals(BaseConstant.COIN_ETH) && !token.symbol.equals(BaseConstant.COIN_ETH)) {
            values.put("type",    BaseConstant.COIN_ERC20);
        } else if (token.type.equals(BaseConstant.COIN_QTUM) && !token.symbol.equals(BaseConstant.COIN_QTUM)) {
            values.put("type",    BaseConstant.COIN_QRC20);
        } else {
            values.put("type",    token.type);
        }
        values.put("decimals",          token.decimals);
        values.put("contractAddr",      token.contractAddr);
        values.put("iconUrl",           token.iconUrl);
        return getUserDB().insertOrThrow(BaseConstant.DB_TABLE_TOKEN, null, values);
    }

    public boolean onReInsertTokens(ArrayList<Token> tokens) {
        boolean result = true;
        try {
            onDeleteAllToken();
            for(Token token:tokens) {
                onInsertToken(token);
            }
        } catch (Exception e) {
            result = false;
        }
        return  result;
    }


    public long onDeleteAllToken() {
        return getUserDB().delete(BaseConstant.DB_TABLE_TOKEN, null, null);
    }

    public long onDeleteErcTokens() {
        return getUserDB().delete(BaseConstant.DB_TABLE_TOKEN, "type == ?", new String[]{""+BaseConstant.COIN_ERC20});
    }

    public long onDeleteQrcTokens() {
        return getUserDB().delete(BaseConstant.DB_TABLE_TOKEN, "type == ?", new String[]{""+BaseConstant.COIN_QRC20});
    }

    public boolean onReInsertErcTokens(ArrayList<Token> tokens) {
        boolean result = true;
        try {
            onDeleteErcTokens();
            for(Token token:tokens) {
                onInsertToken(token);
            }
        } catch (Exception e) {
            result = false;
        }
        return  result;
    }

    public boolean onReInsertQrcTokens(ArrayList<Token> tokens) {
        boolean result = true;
        try {
            onDeleteQrcTokens();
            for(Token token:tokens) {
                onInsertToken(token);
            }
        } catch (Exception e) {
            result = false;
        }
        return  result;
    }




    public ArrayList<Price> onSelectAllPrice() {
        ArrayList<Price> result = new ArrayList<>();
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_PRICE, new String[]{"id", "symbol", "usd", "krw"}, null, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            do {
                Price temp = new Price(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3));
                result.add(temp);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public Price onSelectPriceBySymbol(String symbol) {
        Price result = new Price();
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_PRICE, new String[]{"id", "symbol", "usd", "krw"}, "symbol == ?", new String[]{"" + symbol}, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            result = new Price(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3));
        }
        cursor.close();
        return result;
    }

    public boolean isExistingPrice(String symbol) {
        boolean existed = false;
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_PRICE, new String[]{"id", "symbol", "usd", "krw"}, "symbol == ?", new String[]{"" + symbol}, null, null, null);
        if(cursor != null && cursor.getCount() > 0) {
            existed = true;
        }
        cursor.close();
        return existed;
    }

    public long onInsertPrice(Price price) {
        if(isExistingPrice(price.getSymbol())) {
             return onUpdatePrice(price);
        } else {
            ContentValues values = new ContentValues();
            values.put("symbol",    price.getSymbol());
            values.put("usd",       price.getUsd());
            values.put("krw",       price.getKrw());
            return getUserDB().insertOrThrow(BaseConstant.DB_TABLE_PRICE, null, values);
        }
    }


    public long onUpdatePrice(Price price) {
        ContentValues values = new ContentValues();
        values.put("usd",       price.getUsd());
        values.put("krw",       price.getKrw());
        return getUserDB().update(BaseConstant.DB_TABLE_PRICE, values, "symbol = ?", new String[]{""+price.getSymbol()} );
    }







    public ArrayList<Tx> onSelectAllTx() {
        ArrayList<Tx> result = new ArrayList<>();
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_TRANSACTION, new String[]{"id", "puuid", "hash",
                "dateX", "fromX", "toX", "amount", "fee"}, null, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            do {
                Tx tx = new Tx(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getLong(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7));
                result.add(tx);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public ArrayList<Tx> onSelectAllTxByUuid(String uuid) {
        ArrayList<Tx> result = new ArrayList<>();
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_TRANSACTION, new String[]{"id", "puuid", "hash",
                "dateX", "fromX", "toX", "amount", "fee"}, "puuid == ?", new String[]{uuid}, null, null, "dateX DESC");
        if(cursor != null && cursor.moveToFirst()) {
            do {
                Tx tx = new Tx(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getLong(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7));
                result.add(tx);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public boolean isExistingTx(String uuid, String hash) {
        boolean existed = false;
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_TRANSACTION, new String[]{"id", "puuid", "hash",
                "dateX", "fromX", "toX", "amount", "fee"}, "puuid == ? AND hash == ?", new String[]{uuid, hash}, null, null, null);
        if(cursor != null && cursor.getCount() > 0) {
            existed = true;
        }
        cursor.close();
        return existed;
    }

    public long onInsertTx(Tx tx) {
        ContentValues values = new ContentValues();
        values.put("puuid",         tx.uuid);
        values.put("hash",          tx.hash);
        values.put("dateX",         tx.date);
        values.put("fromX",         tx.from);
        values.put("toX",           tx.to);
        values.put("amount",        tx.amount);
        values.put("fee",           tx.fee);
        return getUserDB().insertOrThrow(BaseConstant.DB_TABLE_TRANSACTION, null, values);
    }

    public void onInsertTxs(ArrayList<Tx> txs) {
        for(Tx tx:txs) {
            if(!isExistingTx(tx.uuid, tx.hash)) {
                onInsertTx(tx);
            }
        }
    }

    public long onDeleteTx(String uuid) {
        return getUserDB().delete(BaseConstant.DB_TABLE_TRANSACTION, "puuid == ?", new String[]{""+uuid});
    }









    public long onInsertERCTx(Tx tx) {
        ContentValues values = new ContentValues();
        values.put("hash",          tx.hash);
        values.put("dateX",         tx.date);
        values.put("fromX",         tx.from);
        values.put("toX",           tx.to);
        values.put("amount",        tx.amount);
        values.put("fee",           tx.fee);
        values.put("address",       tx.address);
        values.put("tokenAddr",     tx.tokenAddr);
        return getUserDB().insertOrThrow(BaseConstant.DB_TABLE_ERC_TRANSACTION, null, values);
    }

    public void onInsertERCTxs(ArrayList<Tx> txs) {
        for(Tx tx:txs) {
            if(!isExistingErcTx(tx.address, tx.hash)) {
                onInsertERCTx(tx);
            }
        }
    }

    public boolean isExistingErcTx(String address, String hash) {
        boolean existed = false;
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_ERC_TRANSACTION, new String[]{"address","hash"}, "address == ? AND hash == ?", new String[]{address, hash}, null, null, null);
        if(cursor != null && cursor.getCount() > 0) {
            existed = true;
        }
        cursor.close();
        return existed;
    }

    public ArrayList<Tx> onSelectAllERCTxByToken(String address, String tokenAddr) {
        ArrayList<Tx> result = new ArrayList<>();
        Cursor cursor 	= getUserDB().query(BaseConstant.DB_TABLE_ERC_TRANSACTION, new String[]{"id",  "hash",
                "dateX", "fromX", "toX", "amount", "fee", "address", "tokenAddr"}, "address == ? AND tokenAddr == ?", new String[]{address, tokenAddr}, null, null, "dateX DESC");
        if(cursor != null && cursor.moveToFirst()) {
            do {
                Tx tx = new Tx(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getLong(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8));
                result.add(tx);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

}
