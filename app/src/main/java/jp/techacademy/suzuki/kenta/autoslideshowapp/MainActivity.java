package jp.techacademy.suzuki.kenta.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    //タイマー設定
    Timer timer;
    //タイマー用の時間設定の為の変数
    double timesecond = 0.0f;
    //スレッド作成
    Handler handler = new Handler();

    //パーミッション確認用コードの設定
    public static final int Permission_Request_Code = 100;

    // cursorの設定
        Cursor cursor = null;

    //再生/停止のフラグ
    int a = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //エラーが出ないように例外処理を設定
        try{




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ボタン3つの設定
        Button buttoN = (Button)findViewById(R.id.go);
        buttoN.setOnClickListener(this);

        Button buttoN2 = (Button) findViewById(R.id.back);
        buttoN2.setOnClickListener(this);

        Button buttoN3 = (Button) findViewById(R.id.start_stop);
        buttoN3.setOnClickListener(this);

            // Android 6.0以降の場合
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // パーミッションの許可状態を確認する
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // 許可されている場合
                    //カーソルの取得
                    getContentsInfo();

                        showImage();
                        Log.d("Androidv", "hyouji");



                } else {
                    // 許可されていないので許可ダイアログを表示する
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Permission_Request_Code);
                }
                // Android 5系以下の場合

            }else{
                getContentsInfo();
                //最初の時点で画像を表示できるようにしておく

                    showImage();


            }
    }catch (Exception e){
        //エラーが出た時、文字列を赤く表示
        Log.d("Androidv", "あかん");
    }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //エラーが出ないように例外処理を設定
        try {
        switch (requestCode) {
            case Permission_Request_Code:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getContentsInfo();
                    //最初の時点で画像を表示できるようにしておく

                        showImage();
                        Log.d("Androidv", "表示");

                }else{

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},Permission_Request_Code);
                }
                break;
            default:
                break;

        }
        }catch (Exception e){
            //エラーが出た時、文字列を赤く表示
            Log.d("Androidv", "やばい");
        }
    }

    @Override
    public void onClick(View v) {
        //エラーが出ないように例外処理を設定
        try {



            //「進む」ボタンを押したとき
            if (v.getId() == R.id.go) {
                Log.d("Androidv", "進む");
                cursor.moveToNext();

                if (!cursor.moveToNext()){
                    cursor.moveToFirst();

                    Log.d("Androidv", "一周回った");
                }

                showImage();

                //「戻る」ボタンを押したとき
            } else if (v.getId() == R.id.back) {
                Log.d("Androidv", "戻る");
                cursor.moveToPrevious();

                if (!cursor.moveToPrevious()){
                    cursor.moveToLast();
                    Log.d("Androidv", "一周逆に回った");
                }

                showImage();

                //「再生」ボタンを押した時
            } else if (v.getId() == R.id.start_stop) {

                Button button1 = (Button)findViewById(R.id.go);
                Button button2 = (Button)findViewById(R.id.back);
                Button button5 = (Button)findViewById(R.id.start_stop);
                if (a == 0){
                    //「進む」「戻る」ボタンを使用不可にする
                    a = 30;
                    button1.setEnabled(false);
                    button2.setEnabled(false);
                    button5.setText("停止");
                    Log.d("Androidv", "再生");

                    //タイマー起動、2秒ごとに画像を映す
                    if (timer == null){
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                timesecond += 0.1f;

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (timesecond >= 2.0){
                                            //画像を自動表示
                                            Log.d("Androidv", "自動で進む");
                                            cursor.moveToNext();

                                            if (!cursor.moveToNext()){
                                                cursor.moveToFirst();

                                                Log.d("Androidv", "自動で一周回った");
                                            }
                                            showImage();
                                            //時間を0に初期化
                                            timesecond = 0.0f;
                                        }
                                    }
                                });
                            }
                        }, 2000 ,100);
                    }
                }else if (a == 30){
                    //「進む」「戻る」ボタンを使えるようにする
                    a = 0;
                    button1.setEnabled(true);
                    button2.setEnabled(true);
                    button5.setText("再生");
                    Log.d("Androidv", "停止");

                    //タイマーを止める、そして一度破棄（次に押すとまた2秒後からタイマー動く）
                    if (timer != null){
                        timer.cancel();
                        timer = null;
                    }
                }
            }
        }catch (Exception e){
            //エラーが出た時、文字列を赤く表示
            Log.d("Androidv", "ダメだ");

        }
    }

    //cursor取得用
    private void getContentsInfo() {
        //画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );
        cursor.moveToFirst();
    }

    //画像表示用
    private void showImage() {
        //画像を表示する
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageURI(imageUri);
    }

    //cursorを終了する
    public void  onDestroy() {
        super.onDestroy();
        cursor.close();

    }
}
