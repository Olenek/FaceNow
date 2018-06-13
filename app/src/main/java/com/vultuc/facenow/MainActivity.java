package com.vultuc.facenow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vultuc.facenow.matrix.Matrix;
import com.vultuc.facenow.matrix.MatrixUtils;
import com.vultuc.facenow.neural.NeuralNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class MainActivity extends AppCompatActivity {
    private Button btnCapture;
    private ProgressBar progressBar;
    private ImageView imgCapture;
    private static final int Image_Capture_Code = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCapture =(Button)findViewById(R.id.btnTakePicture);
        progressBar=(ProgressBar )findViewById(R.id.progressBar);
        imgCapture = (ImageView) findViewById(R.id.capturedImage);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt,Image_Capture_Code);
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                imgCapture.setImageBitmap(bp);
                try {
                    LoadNeuralNetwork lnn=new LoadNeuralNetwork(this, progressBar, btnCapture, bp);
                    lnn.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String callNeural(Bitmap bm) throws Exception {
        int[] pix = MatrixUtils.readImgB(bm);
        double[][] mat_db = new double[1][10000];
        for (int j = 0; j < pix.length; j++) {
            double tmp = (pix[j]);
            mat_db[0][j] = tmp;
        }
        Matrix matrix = new Matrix(mat_db);
        Matrix xTest = matrix.getColumns(0, -1);
        NeuralNetwork nnA = loadGSON("testA");
        NeuralNetwork nnB = loadGSON("testB");
        Matrix predictions1 = nnA.getPredictions(xTest);
        int[] predictedClasses1;
        predictedClasses1 = nnB.getPredictedClasses(xTest);
        int otvetAge = (int) Math.round(predictions1.get(0, 0));
        int otvetGen = predictedClasses1[0];

        String sA;
        String sG;

        switch (otvetAge) {

            case (0):
                sA = "0 - 2";
                break;

            case (1):
                sA = "4 - 6";
                break;

            case (2):
                sA = "8 - 12";
                break;

            case (3):
                sA = "15 - 20";
                break;

            case (4):
                sA = "25 - 38";
                break;

            case (5):
                sA = "40 - 55";
                break;

            case (6):
                sA = "60+";
                break;

            default:
                sA = "Sorry. No age prediction";
                break;
        }

        switch (otvetGen) {

            case (0):
                sG = "f";
                break;

            case (1):
                sG = "m";
                break;

            default:
                sG = "Sorry. No gender prediction";
                break;

        }

        return "Age:" +
                sA + ", Gender:" + sG;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public NeuralNetwork loadGSON(String filename) throws IOException {
        AssetManager am = this.getAssets();
        InputStream is = am.open(filename);
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (is, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        is.close();
        String s = textBuilder.toString();

        Gson gson = new Gson();
        NeuralNetwork nn = gson.fromJson(s, NeuralNetwork.class);
        return nn;
    }
}