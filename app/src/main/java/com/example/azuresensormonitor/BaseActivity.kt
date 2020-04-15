package com.example.azuresensormonitor

import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

open class BaseActivity: AppCompatActivity() {

    protected fun  addFragment(fragment: Fragment ){
        val containerView = findViewById<FrameLayout>(R.id.container)
        supportFragmentManager
            .beginTransaction()
            .add(containerView.id, fragment, fragment.tag)
            .disallowAddToBackStack()
            .commit()

    }

    protected fun replaceFragment(fragment: Fragment){
        val containerView = findViewById<FrameLayout>(R.id.container)
        supportFragmentManager
            .beginTransaction()
            .replace(containerView.id, fragment, fragment.tag)
            .addToBackStack(fragment.tag)
            .commit()
    }
}