package bez.dev.featurenotes.views.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import bez.dev.featurenotes.R
import bez.dev.featurenotes.databinding.ActivityImageBinding
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText

class ImageActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityImageBinding

    private lateinit var bitmap: Bitmap

    companion object {
        const val REQUEST_IMAGE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        //TOOLBAR
        setSupportActionBar(_binding.customToolbar.mainListToolbar)    //merges the custom TOOLBAR with the existing MENU
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        _binding.customToolbar.toolbarMainText.text = resources.getText(R.string.load_image)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }


    fun imageAdd(v: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            _binding.imageToTextImage.setImageURI(data!!.data)
            startRecognizing()
        }
    }

    private fun startRecognizing() {
        if (_binding.imageToTextImage.drawable != null) {
            _binding.imageToTextText.setText("")
            bitmap = (_binding.imageToTextImage.drawable as BitmapDrawable).bitmap
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

            detector.processImage(image)
                    .addOnSuccessListener { firebaseVisionText ->
                        processResultText(firebaseVisionText)
                        _binding.imageToTextRotateLeft.visibility = View.VISIBLE
                        _binding.imageToTextRotateRight.visibility = View.VISIBLE
                    }
                    .addOnFailureListener {
                        _binding.imageToTextText.hint = "Failed"
                    }
        } else {
            Toast.makeText(this, "Select an Image First", Toast.LENGTH_LONG).show()
        }

    }


    private fun processResultText(resultText: FirebaseVisionText) {
        if (resultText.textBlocks.size == 0) {
            _binding.imageToTextText.hint = "No Text Found"
            return
        }
        for (block in resultText.textBlocks) {
            val blockText = block.text
            _binding.imageToTextText.append(blockText + "\n")
        }
    }

    fun clickCopyText(view: View) {
        copyTextToClipboard(_binding.imageToTextText.text.toString())
    }

    private fun copyTextToClipboard(text: String) {
        if (text.trim().isNotEmpty()) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("item", text)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this, "copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }


    private fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    fun imageRotateRight(view: View) {
        bitmap = bitmap.rotate(90F) // value must be float
        _binding.imageToTextImage.setImageBitmap(bitmap)
        startRecognizing()
    }

    fun imageRotateLeft(view: View) {
        bitmap = bitmap.rotate(270F) // value must be float
        _binding.imageToTextImage.setImageBitmap(bitmap)
        startRecognizing()
    }


}
