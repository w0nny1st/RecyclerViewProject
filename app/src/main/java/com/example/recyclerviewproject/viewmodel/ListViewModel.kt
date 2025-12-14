package com.example.recyclerviewproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recyclerviewproject.data.ItemsRepository
import com.example.recyclerviewproject.model.ListItem

class ListViewModel : ViewModel() {

    private val repository = ItemsRepository()

    private val _items = MutableLiveData<List<ListItem>>()
    val items: LiveData<List<ListItem>> = _items

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _showUndoSnackbar = MutableLiveData<Pair<Int, ListItem>?>()
    val showUndoSnackbar: LiveData<Pair<Int, ListItem>?> = _showUndoSnackbar

    private var deletedItem: Pair<Int, ListItem>? = null

    init {
        loadItems()
    }

    fun loadItems() {
        _items.value = repository.getItems()
    }

    fun addItem(type: Int) {
        val newItem = when (type) {
            0 -> ListItem.ItemType1(
                id = repository.getNextId(),
                title = "Новый элемент ${repository.getNextId()}",
                description = "Добавлен через ViewModel",
                iconRes = android.R.drawable.ic_input_add
            )
            else -> ListItem.ItemType2(
                id = repository.getNextId(),
                name = "Новый товар ${repository.getNextId()}",
                price = "${repository.getNextId() * 1000} ₽",
                rating = 4.0f,
                imageRes = android.R.drawable.ic_input_get
            )
        }

        repository.addItem(newItem)
        _items.value = repository.getItems()
        _toastMessage.value = "Добавлен новый элемент типа ${type + 1}"
    }

    fun removeItem(position: Int) {
        val item = repository.getItems().getOrNull(position) ?: return
        deletedItem = Pair(position, item)
        repository.removeItem(position)
        _items.value = repository.getItems()
        _showUndoSnackbar.value = deletedItem
    }

    fun undoDelete() {
        deletedItem?.let { (position, item) ->
            val currentItems = repository.getItems().toMutableList()
            currentItems.add(position, item)
            repository.updateItems(currentItems)
            _items.value = repository.getItems()
            _toastMessage.value = "Удаление отменено"
        }
        deletedItem = null
        _showUndoSnackbar.value = null
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        repository.moveItem(fromPosition, toPosition)
        _items.value = repository.getItems()
        _toastMessage.value = "Элемент перемещен"
    }

    fun duplicateItem(item: ListItem) {
        val duplicatedItem = when (item) {
            is ListItem.ItemType1 -> item.copy(
                id = repository.getNextId(),
                title = "${item.title} (копия)"
            )
            is ListItem.ItemType2 -> item.copy(
                id = repository.getNextId(),
                name = "${item.name} (копия)"
            )
            else -> return
        }

        repository.addItem(duplicatedItem)
        _items.value = repository.getItems()
        _toastMessage.value = "Элемент дублирован"
    }

    fun clearToastMessage() {
        _toastMessage.value = ""
    }

    fun clearSnackbar() {
        _showUndoSnackbar.value = null
        deletedItem = null
    }
}