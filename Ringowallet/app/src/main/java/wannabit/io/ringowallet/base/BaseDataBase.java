package wannabit.io.ringowallet.base;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import wannabit.io.ringowallet.utils.WLog;

public class BaseDataBase extends SQLiteOpenHelper {

    private static BaseDataBase instance;

    static public synchronized BaseDataBase getInstance(Context context) {
        if (instance == null) {
            instance = new BaseDataBase(context, BaseConstant.DB_NAME, null, BaseConstant.DB_VERSION);
        }
        return instance;
    }

    public BaseDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE [" + BaseConstant.DB_TABLE_PASSWORD +
                "] ([id] INTEGER PRIMARY KEY AUTOINCREMENT, [resource] TEXT, [spec] TEXT, [bio] INTEGER DEFAULT 0)");


        sqLiteDatabase.execSQL("CREATE TABLE [" + BaseConstant.DB_TABLE_MNEMONIC +
                "] ([id] INTEGER PRIMARY KEY AUTOINCREMENT, [uuid] TEXT, [resource] TEXT, [spec] TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE [" + BaseConstant.DB_TABLE_KEY +
                "] ([id] INTEGER PRIMARY KEY AUTOINCREMENT, [uuid] TEXT, [type] TEXT, [symbol] TEXT, [path] INTEGER, [isRaw] INTEGER DEFAULT 0, [resource] TEXT, [spec] TEXT, [lastBalance] TEXT, [address] TEXT, [dateX] INTEGER, [isFavo] INTEGER DEFAULT 0)");

        sqLiteDatabase.execSQL("CREATE TABLE [" + BaseConstant.DB_TABLE_TOKEN +
                "] ([id] INTEGER PRIMARY KEY AUTOINCREMENT, [type] TEXT, [name] TEXT, [symbol] TEXT, [contractAddr] TEXT, [decimals] INTEGER, [iconUrl] TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE [" + BaseConstant.DB_TABLE_PRICE +
                "] ([id] INTEGER PRIMARY KEY AUTOINCREMENT, [symbol] TEXT, [usd] TEXT, [krw] TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE [" + BaseConstant.DB_TABLE_TRANSACTION +
                "] ([id] INTEGER PRIMARY KEY AUTOINCREMENT, [puuid] TEXT, [hash] TEXT, [dateX] INTEGER, [fromX] TEXT, [toX] TEXT, [amount] TEXT, [fee] TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE [" + BaseConstant.DB_TABLE_ERC_TRANSACTION +
                "] ([id] INTEGER PRIMARY KEY AUTOINCREMENT,[hash] TEXT, [dateX] INTEGER, [fromX] TEXT, [toX] TEXT, [amount] TEXT, [fee] TEXT, [address] TEXT, [tokenAddr] TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
