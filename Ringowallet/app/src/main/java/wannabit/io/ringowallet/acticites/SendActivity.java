package wannabit.io.ringowallet.acticites;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseActivity;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.dialog.Dialog_SendConfirm;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.model.Price;
import wannabit.io.ringowallet.task.Erc20MotherBalanceCheckTask;
import wannabit.io.ringowallet.task.EthGasPriceTask;
import wannabit.io.ringowallet.task.TaskCallback;
import wannabit.io.ringowallet.task.TaskResult;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;

public class SendActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener ,
        View.OnFocusChangeListener, TextView.OnEditorActionListener, SeekBar.OnSeekBarChangeListener, TaskCallback {

    private TextView        mTitle, mTopAddress, mTopAmount, mTopValue;
    private RelativeLayout  mMotherLayer;
    private ProgressBar     mMotherProgress;
    private TextView        mMotherBalance;

    private LinearLayout    mAddressLayer;
    private EditText        mTargetAddressEt;
    private LinearLayout    mAddressCntLayer;
    private LinearLayout    mBtnPaste, mBtnQrCode, mBtnRecent;
    private TextView        mInvalidAddress;

    private LinearLayout    mContentsLayer;
    private LinearLayout    mAmountLayer;
    private EditText        mAmountEt;
    private TextView        mBtnAmountClear;
    private LinearLayout    mAmountCntLayer;
    private Button          mBtnAmount0, mBtnAmount1, mBtnAmount2, mBtnAmount3, mBtnAmount4;
    private TextView        mAmountPrice;

    private LinearLayout    mFeeLayer;
    private TextView        mFeeAmount;
    private SeekBar         mFeeSeekBar;
    private TextView        mFeeSeekTab0, mFeeSeekTab1, mFeeSeekTab2;

    private LinearLayout    mGasLayer;
    private TextView        mGasPrice;
    private SeekBar         mGasPriceSeekBar;
    private TextView        mGasPriceSeekTab0, mGasPriceSeekTab1, mGasPriceSeekTab2;
    private TextView        mGasLimit;
    private SeekBar         mGasLimitSeekBar;
    private TextView        mGasLimitSeekTab0, mGasLimitSeekTab1, mGasLimitSeekTab2;
    private TextView        mGasTotalAmount;

    private Button          mNext;


    private boolean         mIsGasNeed = false;
    private boolean         mIsMotherNeed = false;
    private Key             mKey;
    private Price           mPrice;
    private BigDecimal      mMother = BigDecimal.ZERO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        mTitle              = findViewById(R.id.toolbar_title);
        mTopAddress         = findViewById(R.id.data_address);
        mTopAmount          = findViewById(R.id.amountTv);
        mTopValue           = findViewById(R.id.priceTv);
        mMotherLayer        = findViewById(R.id.motherLayer);
        mMotherProgress     = findViewById(R.id.mother_progress);
        mMotherBalance      = findViewById(R.id.mother_balance);
        mTitle.setTypeface(WUtils.getTypefaceRegular(this));
        mTopAddress.setTypeface(WUtils.getTypefaceRegular(this));
        mTopAmount.setTypeface(WUtils.getTypefaceRegular(this));
        mTopValue.setTypeface(WUtils.getTypefaceRegular(this));
        mMotherBalance.setTypeface(WUtils.getTypefaceRegular(this));
        ((TextView)findViewById(R.id.mother_balance_Tv)).setTypeface(WUtils.getTypefaceRegular(getBaseContext()));

        mContentsLayer      = findViewById(R.id.contentLayer);
        mAddressLayer       = findViewById(R.id.sendAddressLayer);
        mTargetAddressEt    = findViewById(R.id.et_address_input);
        mAddressCntLayer    = findViewById(R.id.address_control_layer);
        mBtnPaste           = findViewById(R.id.btn_paste);
        mBtnQrCode          = findViewById(R.id.btn_qr);
        mBtnRecent          = findViewById(R.id.btn_recent);
        mInvalidAddress     = findViewById(R.id.invalid_address_tv);
        mTargetAddressEt.setTypeface(WUtils.getTypefaceRegular(this));
        mInvalidAddress.setTypeface(WUtils.getTypefaceRegular(this));
        ((TextView)findViewById(R.id.address_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseContext()));
        ((TextView)findViewById(R.id.paste_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseContext()));
        ((TextView)findViewById(R.id.qr_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseContext()));
        ((TextView)findViewById(R.id.recent_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseContext()));

        mAmountLayer        = findViewById(R.id.sendAmountLayer);
        mAmountEt           = findViewById(R.id.et_amount_input);
        mBtnAmountClear     = findViewById(R.id.et_amount_clear);
        mAmountCntLayer     = findViewById(R.id.amount_control_layer);
        mBtnAmount0         = findViewById(R.id.btn_add_amount_0);
        mBtnAmount1         = findViewById(R.id.btn_add_amount_1);
        mBtnAmount2         = findViewById(R.id.btn_add_amount_2);
        mBtnAmount3         = findViewById(R.id.btn_add_amount_3);
        mBtnAmount4         = findViewById(R.id.btn_add_amount_4);
        mAmountPrice        = findViewById(R.id.send_price_tv);
        mAmountEt.setTypeface(WUtils.getTypefaceRegular(this));
        mBtnAmountClear.setTypeface(WUtils.getTypefaceRegular(this));
        mBtnAmount0.setTypeface(WUtils.getTypefaceRegular(this));
        mBtnAmount1.setTypeface(WUtils.getTypefaceRegular(this));
        mBtnAmount2.setTypeface(WUtils.getTypefaceRegular(this));
        mBtnAmount3.setTypeface(WUtils.getTypefaceRegular(this));
        mBtnAmount4.setTypeface(WUtils.getTypefaceRegular(this));
        mAmountPrice.setTypeface(WUtils.getTypefaceLight(this));
        ((TextView)findViewById(R.id.amount_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseContext()));

        mFeeLayer           = findViewById(R.id.feeLayer);
        mFeeAmount          = findViewById(R.id.fee_amount);
        mFeeSeekBar         = findViewById(R.id.fee_seekbar);
        mFeeSeekTab0        = findViewById(R.id.feeSeekTap0);
        mFeeSeekTab1        = findViewById(R.id.feeSeekTap1);
        mFeeSeekTab2        = findViewById(R.id.feeSeekTap2);
        mFeeAmount.setTypeface(WUtils.getTypefaceLight(this));
        mFeeSeekTab0.setTypeface(WUtils.getTypefaceRegular(this));
        mFeeSeekTab1.setTypeface(WUtils.getTypefaceRegular(this));
        mFeeSeekTab2.setTypeface(WUtils.getTypefaceRegular(this));
        ((TextView)findViewById(R.id.fee_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseContext()));

        mGasLayer           = findViewById(R.id.gasLayer);
        mGasPrice           = findViewById(R.id.gas_price);
        mGasPriceSeekBar    = findViewById(R.id.gas_price_seekbar);
        mGasPriceSeekTab0   = findViewById(R.id.gasPriceSeekTap0);
        mGasPriceSeekTab1   = findViewById(R.id.gasPriceSeekTap1);
        mGasPriceSeekTab2   = findViewById(R.id.gasPriceSeekTap2);
        mGasLimit           = findViewById(R.id.gas_limit);
        mGasLimitSeekBar    = findViewById(R.id.gas_limit_seekbar);
        mGasLimitSeekTab0   = findViewById(R.id.gasLimitSeekTap0);
        mGasLimitSeekTab1   = findViewById(R.id.gasLimitSeekTap1);
        mGasLimitSeekTab2   = findViewById(R.id.gasLimitSeekTap2);
        mGasTotalAmount     = findViewById(R.id.gasTotalAmount);
        mGasPrice.setTypeface(WUtils.getTypefaceLight(this));
        mGasPriceSeekTab0.setTypeface(WUtils.getTypefaceRegular(this));
        mGasPriceSeekTab1.setTypeface(WUtils.getTypefaceRegular(this));
        mGasPriceSeekTab2.setTypeface(WUtils.getTypefaceRegular(this));
        mGasLimit.setTypeface(WUtils.getTypefaceLight(this));
        mGasLimitSeekTab0.setTypeface(WUtils.getTypefaceRegular(this));
        mGasLimitSeekTab1.setTypeface(WUtils.getTypefaceRegular(this));
        mGasLimitSeekTab2.setTypeface(WUtils.getTypefaceRegular(this));
        mGasTotalAmount.setTypeface(WUtils.getTypefaceLight(this));
        ((TextView)findViewById(R.id.gas_price_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseContext()));
        ((TextView)findViewById(R.id.gas_limit_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseContext()));

        mNext               = findViewById(R.id.btn_next);
        mNext.setTypeface(WUtils.getTypefaceRegular(this));

        mTargetAddressEt.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mTargetAddressEt.setRawInputType(InputType.TYPE_CLASS_TEXT);

        mContentsLayer.setOnTouchListener(this);
        mBtnPaste.setOnClickListener(this);
        mBtnQrCode.setOnClickListener(this);
        mBtnRecent.setOnClickListener(this);
        mBtnAmountClear.setOnClickListener(this);
        mBtnAmount0.setOnClickListener(this);
        mBtnAmount1.setOnClickListener(this);
        mBtnAmount2.setOnClickListener(this);
        mBtnAmount3.setOnClickListener(this);
        mBtnAmount4.setOnClickListener(this);
        mNext.setOnClickListener(this);

        mTargetAddressEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mAmountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(s.toString())) {
                    try {
                        BigDecimal input = new BigDecimal(s.toString());
                        mAmountPrice.setText(WUtils.getDpSendAmountValue(getBaseApplication(), input, mKey, mPrice));

                    } catch (Exception e) {

                    }
                    onCheckAmountValidate();
                } else {
                    mAmountPrice.setText("");
                }
            }
        });

        mTargetAddressEt.setOnFocusChangeListener(this);
        mAmountEt.setOnFocusChangeListener(this);

        mTargetAddressEt.setOnEditorActionListener(this);
        mAmountEt.setOnEditorActionListener(this);

        mFeeSeekBar.setOnSeekBarChangeListener(this);
        mGasPriceSeekBar.setOnSeekBarChangeListener(this);
        mGasLimitSeekBar.setOnSeekBarChangeListener(this);

        onInitView();

    }

    private void onInitView() {
        mKey    = getBaseDao().onSelectKey(getIntent().getStringExtra("uuid"));
        mPrice  = getBaseDao().onSelectPriceBySymbol(mKey.symbol);

        mTitle.setText(getString(R.string.str_send) + "(" + mKey.symbol + ")");
        mTopAddress.setText(mKey.address);
        mTopAmount.setText(WUtils.getDpKeyFullBalance(getBaseApplication(), mKey, true));
        mTopValue.setText(WUtils.getDpKeyValue(getBaseApplication(), mKey, mPrice));

        if(mKey.type.equals(BaseConstant.COIN_ETH) ||
                mKey.type.equals(BaseConstant.COIN_ERC20) ||
                mKey.type.equals(BaseConstant.COIN_ETC)) {
            mIsGasNeed = true;
            new EthGasPriceTask(getBaseApplication(), this).execute();

        } else {
            mIsGasNeed = false;
        }

        if(mKey.type.equals(BaseConstant.COIN_ERC20) || mKey.type.equals(BaseConstant.COIN_QRC20)) {
            mIsMotherNeed = true;
            mMotherLayer.setVisibility(View.VISIBLE);
            new Erc20MotherBalanceCheckTask(this).execute(mKey.address);
        } else {
            mIsMotherNeed = false;
            mMotherLayer.setVisibility(View.INVISIBLE);
        }

        onAddressEditMode();

    }

    private void onAddressEditMode() {
        mAddressCntLayer.setVisibility(View.VISIBLE);
        mAmountLayer.setVisibility(View.GONE);
        mAmountEt.setText("");
        mFeeLayer.setVisibility(View.GONE);
        mGasLayer.setVisibility(View.GONE);
    }

    private void onAmountEditMode() {
        mAddressCntLayer.setVisibility(View.GONE);
        mAmountLayer.setVisibility(View.VISIBLE);

        if(mIsGasNeed) {
            WLog.w("show gas");
            mGasLayer.setVisibility(View.VISIBLE);
            mGasPriceSeekBar.setProgress(0);
            mGasLimitSeekBar.setProgress(0);

        } else {
            mFeeLayer.setVisibility(View.VISIBLE);
            mFeeSeekBar.setProgress(1);

        }
    }


    @Override
    public void onClick(View v) {
        if (v.equals(mBtnPaste)) {
            ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            if(clipboard.getPrimaryClip() != null && clipboard.getPrimaryClip().getItemCount() > 0) {
                mTargetAddressEt.setText(clipboard.getPrimaryClip().getItemAt(0).coerceToText(this));
            } else {
                Toast.makeText(this, R.string.msg_error_clipboard, Toast.LENGTH_SHORT).show();
            }

        } else if (v.equals(mBtnQrCode)) {
            new IntentIntegrator(this).initiateScan();

        } else if (v.equals(mBtnRecent)) {

        } else if (v.equals(mBtnAmountClear)) {
            mAmountEt.setText("");
            mAmountEt.setBackground(getDrawable(R.drawable.input_box_gray2));

        } else if (v.equals(mBtnAmount0)) {
            BigDecimal existed = BigDecimal.ZERO;
            if(!TextUtils.isEmpty(mAmountEt.getText().toString())) {
                try {
                    existed = new BigDecimal(mAmountEt.getText().toString());
                } catch (Exception e) {}
            }
            mAmountEt.setText(existed.add(BigDecimal.ONE).toPlainString());

        } else if (v.equals(mBtnAmount1)) {
            BigDecimal existed = BigDecimal.ZERO;
            if(!TextUtils.isEmpty(mAmountEt.getText().toString())) {
                try {
                    existed = new BigDecimal(mAmountEt.getText().toString());
                } catch (Exception e) {}
            }
            mAmountEt.setText(existed.add(new BigDecimal("10")).toPlainString());

        } else if (v.equals(mBtnAmount2)) {
            BigDecimal result = mKey.lastBalance.divide(new BigDecimal("4"), 0, BigDecimal.ROUND_CEILING);
            mAmountEt.setText(result.movePointLeft(WUtils.getTokenDecimal(getBaseApplication(), mKey)).toPlainString());

        } else if (v.equals(mBtnAmount3)) {
            BigDecimal result = mKey.lastBalance.divide(new BigDecimal("2"), 0, BigDecimal.ROUND_CEILING);
            mAmountEt.setText(result.movePointLeft(WUtils.getTokenDecimal(getBaseApplication(), mKey)).toPlainString());

        } else if (v.equals(mBtnAmount4)) {
            if(mIsGasNeed) {
                if(mIsMotherNeed) mAmountEt.setText(mKey.lastBalance.movePointLeft(WUtils.getTokenDecimal(getBaseApplication(), mKey)).toPlainString());
                else mAmountEt.setText(mKey.lastBalance.subtract(Convert.toWei(mGasPrice.getText().toString().trim(), Convert.Unit.GWEI).multiply(new BigDecimal(mGasLimit.getText().toString().trim()))).movePointLeft(WUtils.getTokenDecimal(getBaseApplication(), mKey)).toPlainString());
            } else mAmountEt.setText(mKey.lastBalance.subtract(WUtils.getEstimateFee(getBaseApplication(), mKey.type, mFeeSeekBar.getProgress())).movePointLeft(WUtils.getTokenDecimal(getBaseApplication(), mKey)).toPlainString());

        } else if (v.equals(mNext)) {
            onInitFocus();
            if (mAddressCntLayer.getVisibility() == View.VISIBLE) {
                if(onCheckAddressValidate()) {
                    mTargetAddressEt.setBackground(getDrawable(R.drawable.input_box_gray2));
                    mInvalidAddress.setVisibility(View.GONE);
                    onAmountEditMode();
                } else {
                    mTargetAddressEt.setBackground(getDrawable(R.drawable.input_box_error));
                    mInvalidAddress.setVisibility(View.VISIBLE);
                }
            } else {
                if(onFinalCheckSend()) {
                    WLog.w("ok");
                    Bundle bundle = new Bundle();
                    BigDecimal sendAmount       = BigDecimal.ZERO;
                    BigDecimal feeAmount        = BigDecimal.ZERO;
                    BigDecimal gasPrice         = BigDecimal.ZERO;
                    BigDecimal gasLimit         = BigDecimal.ZERO;
                    BigDecimal remainAmount     = BigDecimal.ZERO;
                    sendAmount = new BigDecimal(mAmountEt.getText().toString().trim().replace(",","")).movePointRight(WUtils.getTokenDecimal(getBaseApplication(), mKey));
                    if(mIsGasNeed) {
                        gasPrice = WUtils.getEstimateGasPrice(getBaseApplication(), mKey.type, mGasPriceSeekBar.getProgress());
                        gasLimit = WUtils.getEstimateGasLimit(getBaseApplication(), mKey.type, mGasLimitSeekBar.getProgress());
                        feeAmount = Convert.toWei(gasPrice.toPlainString(), Convert.Unit.GWEI).multiply(gasLimit);
                        if(mIsMotherNeed) {
                            remainAmount = mKey.lastBalance.subtract(sendAmount);
                            bundle.putString("contractAddr",    getBaseDao().onSelectTokenBySymbol(mKey.symbol).contractAddr);
                        } else {
                            remainAmount = mKey.lastBalance.subtract(sendAmount).subtract(feeAmount);
                        }
                    } else {
                        feeAmount = WUtils.getEstimateFee(getBaseApplication(), mKey.type, mFeeSeekBar.getProgress());
                        remainAmount = mKey.lastBalance.subtract(sendAmount).subtract(feeAmount);
                    }
                    if(mKey.type.equals(BaseConstant.COIN_QTUM)) {
                        if(remainAmount.compareTo(BigDecimal.ZERO) > 0 && remainAmount.compareTo(new BigDecimal(getString(R.string.qtum_min))) <= 0) {
                            Toast.makeText(this, R.string.msg_dust_remain_error, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    bundle.putString("address",     mTargetAddressEt.getText().toString().trim());
                    bundle.putString("amount",      sendAmount.toPlainString());
                    bundle.putString("fee",         feeAmount.toPlainString());
                    bundle.putString("remain",      remainAmount.toPlainString());
                    bundle.putString("symbol",      mKey.symbol);
                    bundle.putString("type",        mKey.type);
                    bundle.putString("uuid",        mKey.uuid);
                    bundle.putInt("decimal",        WUtils.getTokenDecimal(getBaseApplication(), mKey));
                    bundle.putString("gasPrice",    gasPrice.toPlainString());
                    bundle.putString("gasLimit",    gasLimit.toPlainString());
                    Dialog_SendConfirm dialog = Dialog_SendConfirm.newInstance(bundle);
                    dialog.setCancelable(true);
                    dialog.show(getSupportFragmentManager(), "dialog");
                } else {
                    WLog.w("error");
                }

            }

        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if(view.equals(mContentsLayer)) {
            onInitFocus();
        }
        return false;
    }

    private void onInitFocus() {
        onHideKeyboard();
        mContentsLayer.requestFocus();
    }

    private void onUpdateFee() {
        if(mIsGasNeed) {
            BigDecimal gasPrice = WUtils.getEstimateGasPrice(getBaseApplication(), mKey.type, mGasPriceSeekBar.getProgress());
            BigDecimal gasLimit = WUtils.getEstimateGasLimit(getBaseApplication(), mKey.type, mGasLimitSeekBar.getProgress());
            mGasPrice.setText(gasPrice.toPlainString());
            mGasLimit.setText(gasLimit.toPlainString());
            BigDecimal totalFee = Convert.toWei(gasPrice.toPlainString(), Convert.Unit.GWEI).multiply(gasLimit);
            mGasTotalAmount.setText(getString(R.string.msg_total_fee) + "\n"+ Convert.fromWei(totalFee.toPlainString(), Convert.Unit.ETHER).toString() + " ETH");

        } else {
            BigDecimal fee = WUtils.getEstimateFee(getBaseApplication(), mKey.type, mFeeSeekBar.getProgress());
            mFeeAmount.setText(WUtils.getBigDecimalToDpFullBalance(getBaseApplication(),fee,  mKey, true));
        }
    }


    private boolean onCheckAddressValidate() {
        if(mTargetAddressEt.getText().toString().trim().equals(mKey.address)) {
            Toast.makeText(this, R.string.msg_error_self_address, Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mIsGasNeed) {
            return WUtils.validateEthereumAddress(mTargetAddressEt.getText().toString().trim());
        } else {
            if(mKey.type.equals(BaseConstant.COIN_QTUM)) {
                if(!mTargetAddressEt.getText().toString().trim().startsWith("Q")) return false;
            }
            return WUtils.validateBitcoinAddress(mTargetAddressEt.getText().toString().trim());
        }
    }



    private void onCheckAmountValidate() {
        if(mIsGasNeed) {
            if(mIsMotherNeed) {
                BigDecimal toSend = BigDecimal.ZERO;
                if(!TextUtils.isEmpty(mAmountEt.getText().toString())) {
                    toSend = new BigDecimal(mAmountEt.getText().toString()).movePointRight(WUtils.getTokenDecimal(getBaseApplication(), mKey));
                    if(mKey.lastBalance.compareTo(toSend) >= 0) {
                        mAmountEt.setBackground(getDrawable(R.drawable.input_box_gray2));
                    } else {
                        mAmountEt.setBackground(getDrawable(R.drawable.input_box_error));
                    }
                    if(toSend.compareTo(BigDecimal.ZERO) <= 0) {
                        mAmountEt.setBackground(getDrawable(R.drawable.input_box_error));
                        mAmountPrice.setText(getString(R.string.msg_invalid_amount));
                    }

                } else {
                    mAmountEt.setBackground(getDrawable(R.drawable.input_box_gray2));
                }

            } else {
                BigDecimal fee = Convert.toWei(mGasPrice.getText().toString().trim(), Convert.Unit.GWEI).multiply(new BigDecimal(mGasLimit.getText().toString().trim()));
                BigDecimal toSend = BigDecimal.ZERO;
                if(!TextUtils.isEmpty(mAmountEt.getText().toString())) {
                    toSend = Convert.toWei(mAmountEt.getText().toString().trim(), Convert.Unit.ETHER);
                    if(mKey.lastBalance.compareTo(fee.add(toSend)) >= 0) {
                        mAmountEt.setBackground(getDrawable(R.drawable.input_box_gray2));
                    } else  {
                        mAmountEt.setBackground(getDrawable(R.drawable.input_box_error));
                    }
                    if(toSend.compareTo(BigDecimal.ZERO) <= 0) {
                        mAmountEt.setBackground(getDrawable(R.drawable.input_box_error));
                        mAmountPrice.setText(getString(R.string.msg_invalid_amount));
                    }

                } else {
                    mAmountEt.setBackground(getDrawable(R.drawable.input_box_gray2));
                }
            }

        } else {
            BigDecimal fee = WUtils.getEstimateFee(getBaseApplication(), mKey.type, mFeeSeekBar.getProgress());
            BigDecimal input = BigDecimal.ZERO;
            if(!TextUtils.isEmpty(mAmountEt.getText().toString())) {
                input = new BigDecimal(mAmountEt.getText().toString()).movePointRight(WUtils.getTokenDecimal(getBaseApplication(), mKey));
                if(mKey.lastBalance.compareTo(fee.add(input)) >= 0) {
                    mAmountEt.setBackground(getDrawable(R.drawable.input_box_gray2));
                } else {
                    mAmountEt.setBackground(getDrawable(R.drawable.input_box_error));
                }

                if(input.compareTo(WUtils.getMinAmountToSend(getBaseApplication(), mKey.type)) < 0) {
                    mAmountEt.setBackground(getDrawable(R.drawable.input_box_error));
                    mAmountPrice.setText(String.format(getString(R.string.msg_under_at_min), WUtils.getBigDecimalToDpFullBalance(getBaseApplication(),WUtils.getMinAmountToSend(getBaseApplication(), mKey.type),  mKey, true)));
                }
            } else {
                mAmountEt.setBackground(getDrawable(R.drawable.input_box_gray2));
            }
        }
    }


    private boolean onFinalCheckSend() {
        if(!onCheckAddressValidate()) return false;

        if(mIsGasNeed) {
            if(mIsMotherNeed) {
                BigDecimal fee = Convert.toWei(mGasPrice.getText().toString().trim(), Convert.Unit.GWEI).multiply(new BigDecimal(mGasLimit.getText().toString().trim()));
                if(mMother.compareTo(fee) < 0) {
                    Toast.makeText(this, R.string.msg_not_enough_gas_fee, Toast.LENGTH_SHORT).show();
                    return false;
                }
                BigDecimal toSend = BigDecimal.ZERO;
                if(!TextUtils.isEmpty(mAmountEt.getText().toString())) {
                    toSend = new BigDecimal(mAmountEt.getText().toString()).movePointRight(WUtils.getTokenDecimal(getBaseApplication(), mKey));
                }
                if(mKey.lastBalance.compareTo(toSend) >= 0 && toSend.compareTo(BigDecimal.ZERO) > 0) {
                    return true;
                }


            } else {
                BigDecimal fee = Convert.toWei(mGasPrice.getText().toString().trim(), Convert.Unit.GWEI).multiply(new BigDecimal(mGasLimit.getText().toString().trim()));
                BigDecimal toSend = BigDecimal.ZERO;
                if(!TextUtils.isEmpty(mAmountEt.getText().toString())) {
                    toSend = Convert.toWei(mAmountEt.getText().toString().trim(), Convert.Unit.ETHER);
                    if(mKey.lastBalance.compareTo(fee.add(toSend)) >= 0 && toSend.compareTo(BigDecimal.ZERO) > 0) {
                        return true;
                    }
                }
            }

        } else {
            BigDecimal fee = WUtils.getEstimateFee(getBaseApplication(), mKey.type, mFeeSeekBar.getProgress());
            BigDecimal toSendAmount = BigDecimal.ZERO;
            if(!TextUtils.isEmpty(mAmountEt.getText().toString())) {
                toSendAmount = new BigDecimal(mAmountEt.getText().toString()).movePointRight(WUtils.getTokenDecimal(getBaseApplication(), mKey));
                if(toSendAmount.compareTo(BigDecimal.ZERO) <= 0 ) {
                    return false;
                } else if(mKey.lastBalance.compareTo(fee.add(toSendAmount)) < 0) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public void onStartSendSequence(Bundle bundle) {
        Intent intent = new Intent(this, PasswordActivity.class);
        intent.putExtra(BaseConstant.CONST_PW_PURPOSE, BaseConstant.CONST_PW_SEND);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }











    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.equals(mTargetAddressEt)) {
            if(hasFocus && mAddressCntLayer.getVisibility() != View.VISIBLE) {
                onAddressEditMode();
            }

        } else if (v.equals(mAmountEt)) {
            if(!hasFocus) {
                try {
                    BigDecimal inputResult = new BigDecimal(mAmountEt.getText().toString());
                    mAmountEt.setText(inputResult.setScale(WUtils.getTokenDecimal(getBaseApplication(), mKey), BigDecimal.ROUND_CEILING).toPlainString());
                } catch (Exception e) {
                    mAmountEt.setText("");
                }
            }

        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.equals(mTargetAddressEt) && actionId == EditorInfo.IME_ACTION_DONE)  {
            onInitFocus();

        } else if (v.equals(mAmountEt) && actionId == EditorInfo.IME_ACTION_DONE)  {
            onInitFocus();
        }
        return false;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.equals(mFeeSeekBar)) {
            if (progress == 0) {
                mFeeSeekTab0.setTextColor(getColor(R.color.color_font_black1));
                mFeeSeekTab1.setTextColor(getColor(R.color.color_font_gray2));
                mFeeSeekTab2.setTextColor(getColor(R.color.color_font_gray2));
            } else if (progress == 1) {
                mFeeSeekTab0.setTextColor(getColor(R.color.color_font_gray2));
                mFeeSeekTab1.setTextColor(getColor(R.color.color_font_black1));
                mFeeSeekTab2.setTextColor(getColor(R.color.color_font_gray2));
            } else if (progress == 2) {
                mFeeSeekTab0.setTextColor(getColor(R.color.color_font_gray2));
                mFeeSeekTab1.setTextColor(getColor(R.color.color_font_gray2));
                mFeeSeekTab2.setTextColor(getColor(R.color.color_font_black1));
            }
            onUpdateFee();
            onCheckAmountValidate();

        } else if (seekBar.equals(mGasPriceSeekBar)) {
            if (progress == 0) {
                mGasPriceSeekTab0.setTextColor(getColor(R.color.color_font_black1));
                mGasPriceSeekTab1.setTextColor(getColor(R.color.color_font_gray2));
                mGasPriceSeekTab2.setTextColor(getColor(R.color.color_font_gray2));
            } else if (progress == 1) {
                mGasPriceSeekTab0.setTextColor(getColor(R.color.color_font_gray2));
                mGasPriceSeekTab1.setTextColor(getColor(R.color.color_font_black1));
                mGasPriceSeekTab2.setTextColor(getColor(R.color.color_font_gray2));
            } else if (progress == 2) {
                mGasPriceSeekTab0.setTextColor(getColor(R.color.color_font_gray2));
                mGasPriceSeekTab1.setTextColor(getColor(R.color.color_font_gray2));
                mGasPriceSeekTab2.setTextColor(getColor(R.color.color_font_black1));
            }
            onUpdateFee();
            onCheckAmountValidate();

        } else if (seekBar.equals(mGasLimitSeekBar)) {
            if (progress == 0) {
                mGasLimitSeekTab0.setTextColor(getColor(R.color.color_font_black1));
                mGasLimitSeekTab1.setTextColor(getColor(R.color.color_font_gray2));
                mGasLimitSeekTab2.setTextColor(getColor(R.color.color_font_gray2));
            } else if (progress == 1) {
                mGasLimitSeekTab0.setTextColor(getColor(R.color.color_font_gray2));
                mGasLimitSeekTab1.setTextColor(getColor(R.color.color_font_black1));
                mGasLimitSeekTab2.setTextColor(getColor(R.color.color_font_gray2));
            } else if (progress == 2) {
                mGasLimitSeekTab0.setTextColor(getColor(R.color.color_font_gray2));
                mGasLimitSeekTab1.setTextColor(getColor(R.color.color_font_gray2));
                mGasLimitSeekTab2.setTextColor(getColor(R.color.color_font_black1));
            }
            onUpdateFee();
            onCheckAmountValidate();
        }
    }


    @Override
    public void onTaskResponse(TaskResult result) {
        if(isFinishing()) return;
        if (result.taskType == BaseConstant.TASK_GAS_PRICE) {
            if(result.isSuccess) {

            }


        } else if (result.taskType == BaseConstant.TASK_GAS_LIMIT) {
            if(result.isSuccess) {

            }
        } else if (result.taskType == BaseConstant.TASK_MOTHER_BALANCE) {
            if(result.isSuccess) {
                mMotherProgress.setVisibility(View.INVISIBLE);
                mMother = new BigDecimal(result.resultData3);
                mMotherBalance.setText(WUtils.getSendDpDialog(getBaseContext(), mMother, "ETH", 18));
            }
        }

    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }


    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                mTargetAddressEt.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
