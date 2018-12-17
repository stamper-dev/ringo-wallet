package wannabit.io.ringowallet.acticites;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.OutputStream;
import java.util.ArrayList;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseActivity;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.utils.WUtils;

public class ReceiveActivity extends BaseActivity implements View.OnClickListener{

    private TextView    mTitle;
    private ImageView   mAddressQr;
    private TextView    mAddressTxt;
    private Button      mShare, mCopy;


    private Key         mKey;
    private Bitmap      mBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        mTitle          = findViewById(R.id.toolbar_title);
        mAddressQr      = findViewById(R.id.receive_address_qr);
        mAddressTxt     = findViewById(R.id.receive_address_tv);
        mShare          = findViewById(R.id.btn_share);
        mCopy           = findViewById(R.id.btn_copy);

        mTitle.setTypeface(WUtils.getTypefaceRegular(this));
        mAddressTxt.setTypeface(WUtils.getTypefaceRegular(this));
        mShare.setTypeface(WUtils.getTypefaceRegular(this));
        mCopy.setTypeface(WUtils.getTypefaceRegular(this));


        mShare.setOnClickListener(this);
        mCopy.setOnClickListener(this);

        onInitView();
    }


    private void onInitView() {
        mKey    = getBaseDao().onSelectKey(getIntent().getStringExtra("uuid"));
        mTitle.setText(getString(R.string.str_receive) + " " + mKey.symbol);
        mAddressTxt.setText(mKey.address);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            mBitmap = toBitmap(qrCodeWriter.encode(mKey.address, BarcodeFormat.QR_CODE, 480, 480));
            mAddressQr.setImageBitmap(mBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.equals(mShare)) {
            new TedPermission(this)
                    .setPermissionListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            try {
                                ContentValues values = new ContentValues();
                                values.put(MediaStore.Images.Media.TITLE, mKey.address);
                                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                OutputStream outstream = getContentResolver().openOutputStream(uri);
                                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                                outstream.close();

                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, mKey.address);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                shareIntent.setType("image/jpeg");
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(Intent.createChooser(shareIntent, "send"));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                            Toast.makeText(getBaseContext(), R.string.msg_error_permission, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .setRationaleMessage("need permission for sd card")
                    .check();




        } else if (v.equals(mCopy)) {
            ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("address", mKey.address);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.msg_copied, Toast.LENGTH_SHORT).show();
        }

    }

    private static Bitmap toBitmap(BitMatrix matrix) {
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }
}
