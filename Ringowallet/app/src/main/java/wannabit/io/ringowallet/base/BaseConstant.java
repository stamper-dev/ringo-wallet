package wannabit.io.ringowallet.base;

public class BaseConstant {

    public final static boolean                 IS_SHOWLOG 					            = true;
    public final static String                  LOG_TAG                                 = "WannaBit";


    public final static String					DB_NAME			                        = "WannaBit";
    public final static int					    DB_VERSION			                    = 1;
    public final static String					DB_TABLE_PASSWORD			            = "password";
    public final static String					DB_TABLE_MNEMONIC			            = "mnemonic";
    public final static String					DB_TABLE_KEY			                = "key";
    public final static String					DB_TABLE_TOKEN			                = "token";
    public final static String					DB_TABLE_PRICE			                = "price";
    public final static String					DB_TABLE_TRANSACTION			        = "txX";
    public final static String					DB_TABLE_ERC_TRANSACTION			    = "erc_txX";


    public final static String					PATH_BTC_PARENTS			            = "M/44H/0H/0H/0";
    public final static String					PATH_BTC_FIRST			                = "M/44H/0H/0H/0/0H";
    public final static String					PATH_BCH_PARENTS			            = "M/44H/145H/0H/0";
    public final static String					PATH_BCH_FIRST			                = "M/44H/145H/0H/0/0H";
    public final static String					PATH_LTC_PARENTS			            = "M/44H/2H/0H/0";
    public final static String					PATH_LTC_FIRST			                = "M/44H/2H/0H/0/0H";



    public final static String					CONST_PW_PURPOSE		                = "CONST_PW_PURPOSE";
    public final static int					    CONST_PW_INIT		                    = 5001;
    public final static int					    CONST_PW_UNLOUCK		                = 5002;
    public final static int					    CONST_PW_SEND		                    = 5003;
    public final static int					    CONST_PW_CHECK_MNEMONIC		            = 5004;
    public final static int					    CONST_PW_CHECK_KEY		                = 5005;

    public final static String				    CONST_CREATE_PURPOSE		            = "CONST_CREATE_PURPOSE";
    public final static int					    CONST_CREATE_PURPOSE_INIT		        = 0;
    public final static int					    CONST_CREATE_PURPOSE_ADD_KEY		    = 1;
    public final static int					    CONST_CREATE_PURPOSE_MNEMONIC		    = 2;


    public final static int					    TASK_INIT_PW		                    = 2000;
    public final static int					    TASK_INIT_MN		                    = 2001;
    public final static int					    TASK_INIT_WALLET		                = 2002;
    public final static int					    TASK_INSERT_RAW_KEY		                = 2003;
    public final static int					    TASK_INSERT_GENERATE_WITH_MNEMONIC	    = 2004;
    public final static int					    TASK_UNLOCK		                        = 2005;


    public final static int					    TASK_BALANCE		                    = 3005;
    public final static int					    TASK_SEND		                        = 3006;
    public final static int					    TASK_GAS_PRICE		                    = 3007;
    public final static int					    TASK_GAS_LIMIT		                    = 3008;
    public final static int					    TASK_MOTHER_BALANCE		                = 3009;


    public static final String                  PASSWORD_KEY                            = "PASSWORD_KEY";
    public static final String                  BIO_KEY                                 = "BIO_KEY";
    public static final String                  MNEMONIC_KEY                            = "MNEMONIC_KEY";


    public final static String					PREFER_STEP			                    = "PREFER_STEP";
    public final static String					PREFER_DECIMAL			                = "PREFER_DECIMAL";
    public final static String					PREFER_CURRENCY			                = "PREFER_CURRENCY";
    public final static String					PREFER_SUPPORT			                = "PREFER_SUPPORT";
    public final static String					PREFER_LEAVE_TIME			            = "PREFER_LEAVE_TIME";


    public final static String					SET_USE_FINGERPRINT			            = "SET_USE_FINGERPRINT";
    public final static String					SET_USE_LOCK_TIME			            = "SET_USE_LOCK_TIME";



    public final static String					COIN_BTC			                    = "BTC";
    public final static String					COIN_BCH			                    = "BCH";
    public final static String					COIN_LTC			                    = "LTC";
    public final static String					COIN_ETH			                    = "ETH";
    public final static String					COIN_ETC			                    = "ETC";
    public final static String					COIN_QTUM			                    = "QTUM";
    public final static String					COIN_BSV			                    = "BSV";
    public final static String					COIN_ERC20			                    = "ERC20";
    public final static String					COIN_QRC20			                    = "QRC20";


    public final static String					COIN_BTC_NAME			                = "Bitcoin";
    public final static String					COIN_BCH_NAME			                = "Bitcoin Cash";
    public final static String					COIN_LTC_NAME			                = "Litecoin";
    public final static String					COIN_ETH_NAME			                = "Ethereum";
    public final static String					COIN_ETC_NAME			                = "Ethereum Classic";
    public final static String					COIN_QTUM_NAME			                = "Qtum";
    public final static String					COIN_BSV_NAME			                = "Bitcoin SV";
    public final static String					COIN_ERC20_NAME			                = "ERC20 Tokens";
    public final static String					COIN_QRC20_NAME			                = "QRC20 Tokens";

    public final static String					CONFIG_MIN_VERSION			            = "min_version_code";
    public final static String					CONFIG_LAST_VERSION			            = "last_version_code";
    public final static String					CONFIG_ERC20_VERSION			        = "erc20_tokens_version";
    public final static String					CONFIG_QRC20_VERSION			        = "qrc20_tokens_version";
    public final static String					CONFIG_SUPPORT_COINS			        = "support_coin_type";
    public final static String					WANNABIT_TEST_TOKEN			            = "X-Auth-Token: tester@wannabit.io=30335081442472=d3a275d23d96fba61af0e6529a495214";



    public final static Double					CONSTANT_US			                    = 1d;
    public final static Double					CONSTANT_MS			                    = CONSTANT_US * 1000;
    public final static Double					CONSTANT_S			                    = CONSTANT_MS * 1000;
    public final static Double					CONSTANT_10S			                = CONSTANT_S * 10;
    public final static Double					CONSTANT_30S			                = CONSTANT_S * 30;
    public final static Double					CONSTANT_M			                    = CONSTANT_S * 60;
    public final static Double					CONSTANT_H			                    = CONSTANT_M * 60;
}

