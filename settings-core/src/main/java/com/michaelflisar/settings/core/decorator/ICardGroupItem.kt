package com.michaelflisar.settings.core.decorator

interface ICardGroupItem {

    var isExpanded: Boolean

    fun getLevel(): Int

    fun getCardGroupParentItem(): ICardGroupItem?
    fun getSubCardGroupItems(): List<ICardGroupItem>

    fun isCardGroupHeader(): Boolean
}