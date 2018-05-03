package com.sagetripp.sixpack

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        tv_login_welcome.text = "欢迎来到 Six Pack"
        arrayListOf(R.drawable.login_image1,
                R.drawable.login_image2,
                R.drawable.login_image3)

        Glide.with(this)
                .load(R.drawable.login_image3)
                .into(iv_login_background)
    }
}