package com.balves42.simplekeystoredemo

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder

class MainActivity : AppCompatActivity() {

    @BindView(R.id.etAliasName)
    lateinit var etAliasName: EditText

    @BindView(R.id.etCipheredText)
    lateinit var etCipheredText: EditText

    @BindView(R.id.etDecipheredText)
    lateinit var etDecipheredText: EditText

    @BindView(R.id.etPlainText)
    lateinit var etPlainText: EditText

    @BindView(R.id.spinnerCipherAliases)
    lateinit var spinnerCipherAlias: Spinner

    @BindView(R.id.spinnerDecipherAliases)
    lateinit var spinnerDecipherAlias: Spinner

    @BindView(R.id.spinnerDeleteAlias)
    lateinit var spinnerDeleteAlias: Spinner

    @BindView(R.id.constraintCipherText)
    lateinit var constraintCipherText: ConstraintLayout

    @BindView(R.id.constraintDecipherText)
    lateinit var constraintDecipherText: ConstraintLayout

    @BindView(R.id.constraintDeleteAlias)
    lateinit var constraintDeleteAlias: ConstraintLayout

    private var mUnbinder: Unbinder? = null
    private lateinit var mKeystoreUtil: KeystoreUtil

    @OnClick(R.id.btnCreate)
    fun createAlias() {
        val text = etAliasName.text.toString()
        if (text != "") {
            val oldAliases = mKeystoreUtil.aliases.toList()
            val success = mKeystoreUtil.generateKeys(this, text)
            if (success) {
                if (oldAliases.isEmpty()) {
                    constraintDeleteAlias.visibility = View.VISIBLE
                    constraintCipherText.visibility = View.VISIBLE
                }
                setSpinners(mKeystoreUtil.aliases.toList())
            } else {
                showToast("Something went wrong creating the alias")
            }
        } else {
            showToast("You need to write something first!")
        }
    }

    @OnClick(R.id.btnCipher)
    fun cipherText() {
        val text = etPlainText.text.toString()
        if (text != "") {
            val selectedAlias = spinnerCipherAlias.selectedItem.toString()
            val cipheredText = mKeystoreUtil.cipherText(text, selectedAlias)
            if (cipheredText != null) {
                etCipheredText.setText(cipheredText)
                constraintDecipherText.visibility = View.VISIBLE
            } else {
                showToast("something went wrong when ciphering!")
            }
        } else {
            showToast("You need to write something first!")
        }
    }

    @OnClick(R.id.btnDecipher)
    fun decipherText() {
        val text = etCipheredText.text.toString()
        if (text != "") {
            val selectedAlias = spinnerDecipherAlias.selectedItem.toString()
            val decipheredText = mKeystoreUtil.decipherText(text, selectedAlias)
            if (decipheredText != null) {
                etDecipheredText.setText(decipheredText)
            } else {
                showToast("something went wrong when deciphering!")
            }
        } else {
            showToast("Ciphered text needs to exist first!")
        }
    }

    @OnClick(R.id.btnDeleteAlias)
    fun deleteAlias() {
        val selectedAlias = spinnerDeleteAlias.selectedItem.toString()
        mKeystoreUtil.deleteKey(selectedAlias)
        val currentAliases = mKeystoreUtil.aliases.toList()
        if (currentAliases.isEmpty()) {
            etCipheredText.setText("")
            etDecipheredText.setText("")
            etPlainText.setText("")
            constraintDeleteAlias.visibility = View.INVISIBLE
            constraintCipherText.visibility = View.INVISIBLE
            constraintDecipherText.visibility = View.INVISIBLE
        } else {
            setSpinners(currentAliases)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mUnbinder = ButterKnife.bind(this)

        mKeystoreUtil = KeystoreUtil()
        val aliases = mKeystoreUtil.aliases.toList()
        if (aliases.isEmpty()) {
            constraintCipherText.visibility = View.INVISIBLE
            constraintDeleteAlias.visibility = View.INVISIBLE
        } else {
            val list = mKeystoreUtil.aliases.toList()
            setSpinners(list)
        }
        constraintDecipherText.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        mUnbinder?.unbind()
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setSpinners(list: List<String>) {
        val adapter =
                ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        list
                )

        spinnerCipherAlias.adapter = adapter
        spinnerDecipherAlias.adapter = adapter
        spinnerDeleteAlias.adapter = adapter
    }
}
