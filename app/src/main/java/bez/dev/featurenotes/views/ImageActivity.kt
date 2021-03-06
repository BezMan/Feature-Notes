package bez.dev.featurenotes.views

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
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.android.synthetic.main.main_activity_toolbar.*

class ImageActivity : AppCompatActivity() {

    private lateinit var bitmap: Bitmap

    companion object {
        const val REQUEST_IMAGE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        //TOOLBAR
        setSupportActionBar(main_list_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar_main_text?.text = resources.getText(R.string.load_image)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    fun imageAdd(v: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            imageToText_image.setImageURI(data!!.data)
            startRecognizing()
        }
    }

    private fun startRecognizing() {
        if (imageToText_image.drawable != null) {
            imageToText_text.setText("")
            bitmap = (imageToText_image.drawable as BitmapDrawable).bitmap
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

            detector.processImage(image)
                    .addOnSuccessListener { firebaseVisionText ->
                        processResultText(firebaseVisionText)
                        imageToText_rotate_left.visibility = View.VISIBLE
                        imageToText_rotate_right.visibility = View.VISIBLE
                    }
                    .addOnFailureListener {
                        imageToText_text.hint = "Failed"
                    }
        } else {
            Toast.makeText(this, "Select an Image First", Toast.LENGTH_LONG).show()
        }

    }


    private fun processResultText(resultText: FirebaseVisionText) {
        if (resultText.textBlocks.size == 0) {
            imageToText_text.hint = "No Text Found"
            return
        }
        for (block in resultText.textBlocks) {
            val blockText = block.text
            imageToText_text.append(blockText + "\n")
        }
    }

    fun clickCopyText(view: View) {
        copyTextToClipboard(imageToText_text.text.toString())
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
        imageToText_image.setImageBitmap(bitmap)
        startRecognizing()
    }

    fun imageRotateLeft(view: View) {
        bitmap = bitmap.rotate(270F) // value must be float
        imageToText_image.setImageBitmap(bitmap)
        startRecognizing()
    }


}
