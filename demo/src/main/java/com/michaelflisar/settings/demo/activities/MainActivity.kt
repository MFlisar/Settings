package com.michaelflisar.settings.demo.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.demo.advanced.AdvancedStorageManager
import com.michaelflisar.settings.demo.databinding.ActivityMainBinding
import com.michaelflisar.settings.demo.databinding.ViewMainButtonBinding
import com.michaelflisar.settings.demo.simple.SettingsDefs
import com.michaelflisar.settings.storage.datastorepreferences.DataStorePrefSettings

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val layoutInflater = LayoutInflater.from(this)
        val demos = listOf(
                Demo("Simple demo", "Demo with predefined view", SimpleDemo1Activity::class.java, false),
                Demo("Simple demo", "Demo with custom view", SimpleDemo2Activity::class.java, false),
                Demo("Advanced demo", "Desktop with folders (and nested folders) and custom colors per item (with room database for custom items and preferences for global settings)", AdvancedDemo1Activity::class.java, true),
        )
        demos.forEachIndexed { index, demo ->
            val itemBinding = ViewMainButtonBinding.inflate(layoutInflater)
            itemBinding.tvTitle.text = demo.label
            itemBinding.tvInfo.text = demo.description
            itemBinding.vSeparator.visibility = if (index == demos.size - 1) View.GONE else View.VISIBLE
            itemBinding.btStart.setOnClickListener {
                demo.start(this)
            }
            binding.llList.addView(itemBinding.root, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        }
    }

    class Demo(
            val label: String,
            val description: String,
            val clazz: Class<*>,
            val isAdvanced: Boolean
    ) {
        fun start(activity: AppCompatActivity) {
            // set up settings manager with the correct storage manger
            SettingsManager.reset()
            if (isAdvanced) {
                SettingsManager.init(activity, AdvancedStorageManager)
            } else {
                // 1) our StorageManager is the DataStorePrefsSetting - so we create one here...
                val storageManager = DataStorePrefSettings(activity, "demo_data_store")
                // 2) ... and then we initialise the SettingsManager with the StorageManager it should use
                SettingsManager.init(activity, storageManager)
                // 3) init or update some data
                SettingsDefs.onAppStart()
            }

            activity.startActivity(Intent(activity, clazz))
        }
    }
}