package com.vultuc.facenow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vultuc.facenow.matrix.Matrix;
import com.vultuc.facenow.matrix.MatrixUtils;
import com.vultuc.facenow.neural.NeuralNetwork;

public class LoadNeuralNetwork extends AsyncTask<Void, Void, NeuralResult> {
    private static final String TAG=LoadNeuralNetwork.class.getSimpleName();
    private Bitmap bitmap;
    private MainActivity activity;
    private ProgressBar progressBar;
    private Button button;

    public LoadNeuralNetwork(MainActivity activity, ProgressBar progressBar, Button startButton, Bitmap bitmap) {
        this.activity=activity;
        this.progressBar=progressBar;
        this.button=button;
        this.bitmap=bitmap;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected NeuralResult doInBackground(Void... voids) {
        Matrix predictions1;
        int[] predictedClasses1;
        int[] pix = MatrixUtils.readImgB(bitmap);
        double[][] mat_db = new double[1][10000];
        for (int j = 0; j < pix.length; j++) {
            double tmp = (pix[j]);
            mat_db[0][j] = tmp;
        }
        Matrix matrix = new Matrix(mat_db);
        Matrix xTest = matrix.getColumns(0, -1);
        try {
            NeuralNetwork nnA = activity.loadGSON("testA");
            NeuralNetwork nnB = activity.loadGSON("testB");
            predictions1 = nnA.getPredictions(xTest);
            predictedClasses1 = nnB.getPredictedClasses(xTest);
        }
        catch(Exception ex) {
            Log.e(TAG, "Something went wrong", ex);
            return null;
        }
        int otvetAge = (int) Math.round(predictions1.get(0, 0));
        int otvetGen = predictedClasses1[0];
        NeuralResult neuralResult=new NeuralResult(otvetAge, otvetGen);
        return neuralResult;
    }

    @Override
    protected void onPostExecute(NeuralResult result) {
        super.onPostExecute(result);
        progressBar.setVisibility(View.GONE);
        String sA;
        String sG;

        switch (result.age) {

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

        switch (result.gender) {

            case (0):
                sG = "female";
                break;

            case (1):
                sG = "male";
                break;

            default:
                sG = "Sorry. No gender prediction";
                break;

        }
        String resultString="Age:" + sA + ", Gender:" + sG;
        Toast.makeText(activity, resultString, Toast.LENGTH_LONG).show();
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(resultString);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialogBuilder.show();
    }
}

class NeuralResult {
    public int age;
    public int gender;
    public NeuralResult(int age, int gender) {
        this.age=age;
        this.gender=gender;
    }
}