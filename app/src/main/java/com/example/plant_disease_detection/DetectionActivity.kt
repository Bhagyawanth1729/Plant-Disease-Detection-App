package com.example.plant_disease_detection
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.plant_disease_detection.databinding.ActivityDetectionBinding
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class DetectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetectionBinding
    private lateinit var tflite: Interpreter
    private lateinit var labels: List<String>
    private val inputSize = 256// Your model's input size
    private val pixelSize = 3 // RGB
    private val modelPath = "model.tflite"
    private val labelPath = "labels.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize TFLite model and labels
        try {
            tflite = Interpreter(loadModelFile(assets))
            labels = loadLabelList(assets)

            // Print labels to log for debugging
            for ((index, label) in labels.withIndex()) {
                Log.d("Labels", "Label $index: $label")
            }

            // OPTIONAL: Display all labels in the UI for testing
            binding.resultText.text = "All Labels:\n" + labels.joinToString("\n")
            binding.resultText.visibility = View.VISIBLE

        } catch (e: Exception) {
            binding.detectionStatus.text = "Failed to load model"
            e.printStackTrace()
            return
        }

        // Get the image path or URI from intent
        val imagePath = intent.getStringExtra(EXTRA_IMAGE_PATH)
        if (imagePath != null) {
            val bitmap = if (imagePath.startsWith("content://")) {
                val uri = Uri.parse(imagePath)
                val inputStream = contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            } else {
                BitmapFactory.decodeFile(imagePath)
            }

            if (bitmap != null) {
                binding.capturedImage.setImageBitmap(bitmap)
                startDetection(bitmap)
            } else {
                binding.detectionStatus.text = "Failed to load image"
            }
        } else {
            binding.detectionStatus.text = "No image provided"
        }
    }

    private fun startDetection(bitmap: Bitmap) {
        binding.progressBar.visibility = View.VISIBLE
        binding.detectionStatus.text = "Analyzing plant..."
        binding.resultText.visibility = View.GONE

        Thread {
            try {
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
                val byteBuffer = convertBitmapToByteBuffer(resizedBitmap)

                val output = Array(1) { FloatArray(labels.size) }
                tflite.run(byteBuffer, output)

                val results = output[0]
                val maxIndex = results.indices.maxByOrNull { results[it] } ?: -1
                val confidence = results[maxIndex] * 100
                val diseaseName = if (maxIndex in labels.indices) labels[maxIndex] else "Unknown"

                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.detectionStatus.text = "Analysis Complete"
                    binding.resultText.text = "Detection Result: $diseaseName\n\nConfidence: ${"%.2f".format(confidence)}%"
                    binding.resultText.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.detectionStatus.text = "Analysis Failed"
                    binding.resultText.text = "Error during detection"
                    binding.resultText.visibility = View.VISIBLE
                }
            }
        }.start()
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * pixelSize)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var pixel = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val value = intValues[pixel++]
                byteBuffer.putFloat(((value shr 16) and 0xFF) / 255.0f) // R
                byteBuffer.putFloat(((value shr 8) and 0xFF) / 255.0f)  // G
                byteBuffer.putFloat((value and 0xFF) / 255.0f)          // B
            }
        }
        return byteBuffer
    }

    @Throws(IOException::class)
    private fun loadModelFile(assetManager: AssetManager): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    @Throws(IOException::class)
    private fun loadLabelList(assetManager: AssetManager): List<String> {
        return assetManager.open(labelPath).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.readLines()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tflite.close()
    }

    companion object {
        const val EXTRA_IMAGE_PATH = "image_path"
    }
}
