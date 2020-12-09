package com.michaelflisar.settings.image

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.michaelflisar.settings.core.SettingsIntentHelper
import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.core.classes.*
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.interfaces.ISettingsItem
import com.michaelflisar.settings.core.items.base.BaseSettingsItemDialog
import com.michaelflisar.settings.core.settings.base.BaseSetting
import com.michaelflisar.settings.image.databinding.SettingsItemImageBinding

class SettingsItemImage(
        override var parentSetting: ISettingsItem<*, *, *>?,
        override var index: Int,
        override var item: BaseSetting<String, *, Unit>,
        override var itemData: SettingsMetaData,
        override var settingsData: ISettingsData,
        setup: SettingsDisplaySetup
) : BaseSettingsItemDialog<String, SettingsItemImageBinding, Unit, BaseSetting<String, *, Unit>>(setup) {

    override val type: Int = R.id.settings_item_image
    override val dialogHandler = DIALOG_HANDLER

    override fun bindSubView(binding: SettingsItemImageBinding, payloads: List<Any>, value: String, topBinding: Boolean) {
        binding.ivImage.setImageURI(Uri.parse(value))
    }

    override fun createSubBinding(inflater: LayoutInflater, parent: ViewGroup?, topBinding: Boolean): SettingsItemImageBinding {
        val binding = SettingsItemImageBinding.inflate(inflater, parent, false)
        // set tag for event hook
        setViewTag(binding.ivImage, topBinding)
        return binding
    }

    companion object {

        var REQUEST_CODE_IMAGE = 123

        val DIALOG_HANDLER = object : BaseSettingsStartActivityEventHandler<String, SettingsActivityResultEvent, BaseSetting<String, *, Unit>>() {

            override val dialogType: Int = R.id.settings_dialog_type_item_image

            override fun showDialog(view: View, dialogContext: DialogContext, settingsItem: ISettingsItem<String, *, BaseSetting<String, *, Unit>>, settingsData: ISettingsData) {

                val item = settingsItem.item

                // we do use a trick to support SystemIntents in the settings library, that's why following is necessary
                // not very beautiful but it works
                SettingsIntentHelper.rememberIntentSetting(this, item, settingsData)

                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_OPEN_DOCUMENT
//                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                SettingsIntentHelper.startHelperActivity(dialogContext.context, intent, REQUEST_CODE_IMAGE)
            }

            override fun onDialogEvent(event: SettingsActivityResultEvent, dialogContext: DialogContext) {
                // we do use a trick to support SystemIntents in the settings library, that's why following is necessary
                // not very beautiful but it works
                val settingAndData = SettingsIntentHelper.getIntentSetting<String>(this)

                if (event.requestCode == REQUEST_CODE_IMAGE && event.resultCode == Activity.RESULT_OK) {

                    val selectedItem = event.resultData?.data
                    selectedItem?.let {

                        // take persistant read access
                        val contentResolver = SettingsManager.context.contentResolver
                        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        contentResolver.takePersistableUriPermission(it, takeFlags)

                        // 1) get new value
                        val newValue = selectedItem.toString()

                        // 2) save new value -> this will automatically notify callbacks if setting has changed
                        settingAndData.first.write(settingAndData.second, newValue)
                    }
                }
            }
        }
    }
}