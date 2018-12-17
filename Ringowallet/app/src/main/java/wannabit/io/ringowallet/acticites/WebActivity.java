package wannabit.io.ringowallet.acticites;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseActivity;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;

public class WebActivity extends BaseActivity {

    private WebView         mWebview;
    private String          mType;
    private String          mTxid;
    private boolean         mGoMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ((TextView)findViewById(R.id.toolbar_title)).setTypeface(WUtils.getTypefaceRegular(this));
        mWebview = findViewById(R.id.webView);

        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setDomStorageEnabled(true);

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });


        mType  = getIntent().getStringExtra("type");
        mTxid  = getIntent().getStringExtra("txid");
        mGoMain = getIntent().getBooleanExtra("goMain", true);

        if(mType.equals(BaseConstant.COIN_BTC)) {
            mWebview.loadUrl("https://www.blockchain.com/btc/tx/"+mTxid);

        } else if (mType.equals(BaseConstant.COIN_ETH)) {
            mWebview.loadUrl("https://etherscan.io/tx/"+mTxid);

        } else if (mType.equals(BaseConstant.COIN_LTC)) {
            mWebview.loadUrl("https://live.blockcypher.com/ltc/tx/"+mTxid);

        } else if (mType.equals(BaseConstant.COIN_ETC)) {
            mWebview.loadUrl("http://gastracker.io/tx/"+mTxid);

        } else if (mType.equals(BaseConstant.COIN_ERC20)) {
            mWebview.loadUrl("https://etherscan.io/tx/"+mTxid);

        } else if (mType.equals(BaseConstant.COIN_BCH)) {
            mWebview.loadUrl("https://explorer.bitcoin.com/bch/tx/"+mTxid);

        } else if (mType.equals(BaseConstant.COIN_BSV)) {


        } else if (mType.equals(BaseConstant.COIN_QTUM)) {
            mWebview.loadUrl("https://explorer.qtum.org/tx/"+mTxid);

        } else {
            WLog.w("" + mType + "   " + mTxid);
        }
    }

    @Override
    public void onBackPressed() {

        if(mGoMain) {
            onStartMainActivity();
        } else {
            super.onBackPressed();
        }

    }
}
