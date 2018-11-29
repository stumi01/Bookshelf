package com.bencestumpf.bookshelf.ui.addbook

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bencestumpf.bookshelf.R
import com.bencestumpf.bookshelf.ui.common.LCEStatus
import com.bencestumpf.bookshelf.ui.utils.bindView
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModelByClass

class AddBookActivity : AppCompatActivity() {

    private val viewModel by viewModelByClass(AddBookViewModel::class)

    private val title: EditText by bindView(R.id.book_add_title)
    private val author: EditText by bindView(R.id.book_add_author)
    private val price: EditText by bindView(R.id.book_add_price)
    private val coverURL: EditText by bindView(R.id.book_add_cover_url)
    private val cover: ImageView by bindView(R.id.book_add_cover_preview)
    private val upload: Button by bindView(R.id.book_add_upload)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addbook)
        setupTitle()
        observeUpload()
        observeCoverURL()
        setupView()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupTitle() {
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_navigate_before_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_book)
    }

    private fun setupView() {
        coverURL.setOnEditorActionListener { textView, actionId, event ->
            if (isUserDoneTyping(actionId, event)) {
                viewModel.coverUrlArrived(textView.text)
            }
            return@setOnEditorActionListener false
        }
        upload.setOnClickListener { viewModel.onUploadClick(title.text, author.text, price.text, coverURL.text) }
    }

    private fun isUserDoneTyping(actionId: Int, event: KeyEvent?): Boolean {
        return (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event?.action == KeyEvent.ACTION_DOWN &&
                event?.keyCode == KeyEvent.KEYCODE_ENTER) && (event == null || !event.isShiftPressed)
    }

    private fun observeCoverURL() {
        viewModel.coverUrl.observe(this, Observer {
            when (it.status) {
                LCEStatus.ERROR -> showMessage(R.string.error_wrong_cover_url)
                LCEStatus.SUCCESS -> loadPreview(it.content!!)
            }
        })
    }

    private fun showMessage(@StringRes messageID: Int) {
        showMessage(getString(messageID))
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun loadPreview(coverUrl: String) {
        Glide.with(this).load(coverUrl).into(cover)
    }

    private fun observeUpload() {
        viewModel.content.observe(this, Observer {
            when (it.status) {
                LCEStatus.ERROR -> uploadError(it.error)
                LCEStatus.SUCCESS -> uploadSuccess(it.content!!)
                LCEStatus.LOADING -> showLoading()
            }
        })
    }

    private fun showLoading() {
        enableView(false)
    }

    private fun enableView(isEnabled: Boolean) {
        title.isEnabled = isEnabled
        author.isEnabled = isEnabled
        price.isEnabled = isEnabled
        coverURL.isEnabled = isEnabled
        upload.isEnabled = isEnabled
    }

    private fun uploadSuccess(bookId: Long) {
        showMessage(getString(R.string.upload_success, bookId))
        finish()
    }

    private fun uploadError(error: Throwable?) {
        enableView(true)
        when (error) {
            is NetworkError -> showMessage(R.string.generic_error_message)
            is AddBookValidationError -> showMessage(getString(R.string.upload_validation_error) + getFailedField(error))
            else -> showMessage(R.string.generic_error_message)
        }
    }

    private fun getFailedField(error: AddBookValidationError): String {
        val builder = StringBuilder()
        val delimiter = ", "
        if (error.titleError) {
            builder.append(getString(R.string.title))
            builder.append(delimiter)
        }
        if (error.authorError) {
            builder.append(getString(R.string.author))
            builder.append(delimiter)
        }
        if (error.priceError) {
            builder.append(getString(R.string.price))
            builder.append(delimiter)
        }
        if (error.coverUrl) {
            builder.append(getString(R.string.coverUrl))
            builder.append(delimiter)
        }
        builder.removeSuffix(delimiter)
        return builder.toString()
    }
}
