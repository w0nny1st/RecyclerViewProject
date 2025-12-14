package com.example.recyclerviewproject.data

import com.example.recyclerviewproject.model.ListItem

class ItemsRepository {

    private val items = mutableListOf<ListItem>()
    private var itemCounter = 0

    init {
        loadInitialItems()
    }

    fun getItems(): List<ListItem> = items.toList()

    fun addItem(item: ListItem) {
        items.add(item)
    }

    fun removeItem(position: Int) {
        if (position in items.indices) {
            items.removeAt(position)
        }
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition in items.indices && toPosition in items.indices && fromPosition != toPosition) {
            val item = items.removeAt(fromPosition)
            items.add(toPosition, item)
        }
    }

    fun updateItems(newItems: List<ListItem>) {
        items.clear()
        items.addAll(newItems)
    }

    fun getNextId(): Int {
        return ++itemCounter
    }

    private fun loadInitialItems() {
        items.addAll(listOf(
            ListItem.Header("Раздел элементов"),
            ListItem.ItemType1(1, "Первый элемент", "Описание первого элемента", android.R.drawable.ic_dialog_email),
            ListItem.ItemType1(2, "Второй элемент", "Описание второго элемента", android.R.drawable.ic_dialog_info),
            ListItem.ItemType1(3, "Третий элемент", "Описание третьего элемента", android.R.drawable.ic_dialog_map),

            ListItem.Header("Раздел Товаров"),
            ListItem.ItemType2(4, "Смартфон", "4999 ₽", 4.5f, android.R.drawable.ic_dialog_alert),
            ListItem.ItemType2(5, "Ноутбук", "8999 ₽", 4.8f, android.R.drawable.ic_dialog_dialer),
            ListItem.ItemType2(6, "Наушники", "1999 ₽", 4.2f, android.R.drawable.ic_dialog_email),

            ListItem.Header("Добавленнный элемент"),
            ListItem.ItemType1(7, "Четвертый элемент", "Описание четвертого элемента", android.R.drawable.ic_dialog_info),
            ListItem.ItemType2(8, "Планшет", "6999 ₽", 4.7f, android.R.drawable.ic_dialog_map)
        ))
        itemCounter = 8
    }
}