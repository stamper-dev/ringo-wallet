package wannabit.io.ringowallet.network;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import wannabit.io.ringowallet.R;

public class ApiClient {

    private static WannbitService service_wannabit = null;
    public static WannbitService getWannabitService(Context c) {
        if (service_wannabit == null) {
            synchronized (ApiClient.class) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(c.getString(R.string.wannabit_pro_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                service_wannabit = retrofit.create(WannbitService.class);
            }
        }
        return service_wannabit;
    }


    /**
     * Fetch BTC History
     */
    private static BlockChainInfoService service_blockchain_info = null;
    public static BlockChainInfoService getBlockChainInfoService(Context c) {
        if (service_blockchain_info == null) {
            synchronized (ApiClient.class) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(c.getString(R.string.blockchain_info_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                service_blockchain_info = retrofit.create(BlockChainInfoService.class);
            }
        }
        return service_blockchain_info;
    }

    /**
     * Fetch LTC History
     */
    private static BlockCypherService service_block_cypher = null;
    public static BlockCypherService getBlockCypherService(Context c) {
        if (service_block_cypher == null) {
            synchronized (ApiClient.class) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(c.getString(R.string.blockcypher_info_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                service_block_cypher = retrofit.create(BlockCypherService.class);
            }
        }
        return service_block_cypher;
    }


    /**
     * Fetch BCH History, Balance, UTXO, Send
     */
    private static BlockDozerService service_block_dozer = null;
    public static BlockDozerService getBlockDozerService(Context c) {
        if (service_block_dozer == null) {
            synchronized (ApiClient.class) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(c.getString(R.string.blockdozer_info_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                service_block_dozer = retrofit.create(BlockDozerService.class);
            }
        }
        return service_block_dozer;
    }


    /**
     * Fetch ETH & ERC20 History
     */
    private static EtherScanService service_ether_scan = null;
    public static EtherScanService getEtherScanService(Context c) {
        if (service_ether_scan == null) {
            synchronized (ApiClient.class) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(c.getString(R.string.ether_scan_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                service_ether_scan = retrofit.create(EtherScanService.class);
            }
        }
        return service_ether_scan;
    }

    /**
     * Fetch ETC History
     */
    private static GasTrackerService service_gas_tracker = null;
    public static GasTrackerService getGasTrackerService(Context c) {
        if (service_gas_tracker == null) {
            synchronized (ApiClient.class) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(c.getString(R.string.gastracker_info_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                service_gas_tracker = retrofit.create(GasTrackerService.class);
            }
        }
        return service_gas_tracker;
    }

    /**
     * Fetch QTUM History, Balance, UTXO, Send
     */
    private static QtumExploreService service_qtum_explore = null;
    public static QtumExploreService getQtumExploreService(Context c) {
        if (service_qtum_explore == null) {
            synchronized (ApiClient.class) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(c.getString(R.string.qtum_info_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                service_qtum_explore = retrofit.create(QtumExploreService.class);
            }
        }
        return service_qtum_explore;
    }





    /**
     * Fetch Coin Values
     */
    private static CryptoCompareService service_crypto_compare = null;
    public static CryptoCompareService getCryptoCompareService(Context c) {
        if (service_crypto_compare == null) {
            synchronized (ApiClient.class) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(c.getString(R.string.crypto_compare_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                service_crypto_compare = retrofit.create(CryptoCompareService.class);
            }
        }
        return service_crypto_compare;
    }
}
