package com.michaelflisar.settings.demo.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.michaelflisar.settings.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        listOf(
                Demo("Simple demo with predefined view", Demo1Activity::class.java),
                Demo("Simple demo with custom view", Demo2Activity::class.java),
        )
                .forEach { demo ->
                    val bt = Button(this).apply {
                        text = demo.label
                    }
                    bt.setOnClickListener {
                        startActivity(Intent(this, demo.clazz))
                    }
                    binding.llList.addView(bt, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
                }
    }

    class Demo(
            val label: String,
            val clazz: Class<*>
    )
}