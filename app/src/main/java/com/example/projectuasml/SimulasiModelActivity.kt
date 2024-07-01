package com.example.projectuasml

import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiModelActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "uas.tflite"

    private lateinit var resultText: TextView
    private lateinit var age: EditText
    private lateinit var sex: EditText
    private lateinit var cp: EditText
    private lateinit var trtbps: EditText
    private lateinit var chol: EditText
    private lateinit var fbs: EditText
    private lateinit var restecg: EditText
    private lateinit var thalachh: EditText
    private lateinit var exng: EditText
    private lateinit var oldpeak: EditText
    private lateinit var slp: EditText
    private lateinit var caa: EditText
    private lateinit var thall: EditText
    private lateinit var btncek: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasi_model)

        resultText = findViewById(R.id.txtresult)
        age = findViewById(R.id.et_age)
        sex = findViewById(R.id.et_sex)
        cp = findViewById(R.id.et_cp)
        trtbps = findViewById(R.id.et_trtbps)
        chol = findViewById(R.id.et_chol)
        fbs = findViewById(R.id.et_fbs)
        restecg = findViewById(R.id.et_restecg)
        thalachh = findViewById(R.id.et_talachh)
        exng = findViewById(R.id.et_exng)
        oldpeak = findViewById(R.id.et_oldpeak)
        slp = findViewById(R.id.et_slp)
        caa = findViewById(R.id.et_caa)
        thall = findViewById(R.id.et_thaall)
        btncek = findViewById(R.id.btn_cek)


        btncek.setOnClickListener {
            val result = doInference(
                age.text.toString(),
                sex.text.toString(),
                cp.text.toString(),
                trtbps.text.toString(),
                chol.text.toString(),
                fbs.text.toString(),
                restecg.text.toString(),
                thalachh.text.toString(),
                exng.text.toString(),
                oldpeak.text.toString(),
                slp.text.toString(),
                caa.text.toString(),
                thall.text.toString()
            )
            runOnUiThread {
                resultText.text = if (result == 0) "beresiko terkena serangan jantung" else "tidak beresiko"
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = Interpreter.Options().apply {
            setNumThreads(4)
            setUseNNAPI(true)
        }
        interpreter = Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(
        input1: String, input2: String, input3: String, input4: String, input5: String,
        input6: String, input7: String, input8: String, input9: String, input10: String, input11: String, input12: String, input13: String
    ): Int {
        val inputVal = FloatArray(13)
        try {
            inputVal[0] = input1.toFloat()
            inputVal[1] = input2.toFloat()
            inputVal[2] = input3.toFloat()
            inputVal[3] = input4.toFloat()
            inputVal[4] = input5.toFloat()
            inputVal[5] = input6.toFloat()
            inputVal[6] = input7.toFloat()
            inputVal[7] = input8.toFloat()
            inputVal[8] = input9.toFloat()
            inputVal[9] = input10.toFloat()
            inputVal[10] = input11.toFloat()
            inputVal[11] = input12.toFloat()
            inputVal[12] = input13.toFloat()

        } catch (e: NumberFormatException) {
            Log.e("Inference Error", "Invalid input format", e)
            return 0
        }

        Log.d("Input Values", inputVal.joinToString())

        val output = Array(1) { FloatArray(1) }
        interpreter.run(inputVal, output)

        Log.d("Model Output", output[0].toList().toString())

        return if (output[0][0] >= 0.04f) 1 else 0
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}