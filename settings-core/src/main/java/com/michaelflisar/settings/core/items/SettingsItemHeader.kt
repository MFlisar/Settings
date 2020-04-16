package com.michaelflisar.settings.core.items

import android.view.LayoutInflater
import android.view.ViewGroup
import com.michaelflisar.settings.core.R
import com.michaelflisar.settings.core.SettingsDisplaySetup
import com.michaelflisar.settings.core.databinding.SettingsItemHeaderBinding
import com.michaelflisar.settings.core.interfaces.IBaseSetting
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IParentItem
import com.mikepenz.fastadapter.binding.BindingViewHolder

class SettingsItemHeader(
        override var parentSetting: BaseSettingsItem<*>?,
        override var index: Int,
        override val item: IBaseSetting,
        override val setup: SettingsDisplaySetup
) : BaseSettingsItem<SettingsItemHeaderBinding>(), IExpandable<BindingViewHolder<SettingsItemHeaderBinding>> {

    init {
        init()
    }

    override val type: Int = R.id.settings_item_header

    override fun bindView(binding: SettingsItemHeaderBinding, payloads: List<Any>) {
        var text = item.label.text
        if (setup.showNumbers) {
            text = "${getNumberingInfo(index)} $text"
        }
        binding.tvTitle.text = text
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): SettingsItemHeaderBinding {
        return SettingsItemHeaderBinding.inflate(inflater, parent, false)
    }
}