package com.example.recyclerviewproject.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recyclerviewproject.R
import com.example.recyclerviewproject.databinding.FragmentListBinding
import com.example.recyclerviewproject.model.ListItem
import com.example.recyclerviewproject.ui.adapter.MultiTypeAdapter
import com.example.recyclerviewproject.utils.RecyclerViewItemTouchHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MultiTypeAdapter
    private var itemCounter = 0
    private var deletedItem: Pair<Int, ListItem>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        setupSwipeAndDrag()
    }

    private fun setupRecyclerView() {
        val items = generateItems().toMutableList()

        adapter = MultiTypeAdapter(
            items,
            onItemClick = { item ->
                showItemInfo(item)
            },
            onItemLongClick = { item ->
                showContextMenu(item)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.recyclerView.layoutAnimation = android.view.animation.LayoutAnimationController(
            android.view.animation.AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.item_animation_fall_down
            ),
            0.1f
        ).apply {
            order = android.view.animation.LayoutAnimationController.ORDER_NORMAL
        }

        val divider = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.purple_200))
        val itemDecoration = object : androidx.recyclerview.widget.DividerItemDecoration(
            requireContext(),
            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        ) {
            override fun getItemOffsets(
                outRect: android.graphics.Rect,
                view: View,
                parent: androidx.recyclerview.widget.RecyclerView,
                state: androidx.recyclerview.widget.RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.set(16, 8, 16, 8)
            }
        }
        binding.recyclerView.addItemDecoration(itemDecoration)
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            showAddItemDialog()
        }
    }

    private fun setupSwipeAndDrag() {
        val touchHelper = RecyclerViewItemTouchHelper(
            adapter = adapter,
            onItemDeleted = { position, item ->
                deletedItem = Pair(position, item)
                adapter.removeItem(position)
                showUndoSnackbar()
            },
            onItemMoved = { fromPosition, toPosition ->
                adapter.moveItem(fromPosition, toPosition)
                Toast.makeText(
                    requireContext(),
                    "Элемент перемещен",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        val itemTouchHelper = ItemTouchHelper(touchHelper)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun generateItems(): List<ListItem> {
        return listOf(
            ListItem.Header("Заголовок раздела 1"),
            ListItem.ItemType1(1, "Первый элемент", "Описание первого элемента", android.R.drawable.ic_dialog_email),
            ListItem.ItemType1(2, "Второй элемент", "Описание второго элемента", android.R.drawable.ic_dialog_info),
            ListItem.ItemType1(3, "Третий элемент", "Описание третьего элемента", android.R.drawable.ic_dialog_map),

            ListItem.Header("Заголовок раздела 2 - Товары"),
            ListItem.ItemType2(4, "Смартфон", "4999 ₽", 4.5f, android.R.drawable.ic_dialog_alert),
            ListItem.ItemType2(5, "Ноутбук", "8999 ₽", 4.8f, android.R.drawable.ic_dialog_dialer),
            ListItem.ItemType2(6, "Наушники", "1999 ₽", 4.2f, android.R.drawable.ic_dialog_email),

            ListItem.Header("Заголовок раздела 3"),
            ListItem.ItemType1(7, "Четвертый элемент", "Описание четвертого элемента", android.R.drawable.ic_dialog_info),
            ListItem.ItemType2(8, "Планшет", "6999 ₽", 4.7f, android.R.drawable.ic_dialog_map)
        )
    }

    private fun showAddItemDialog() {
        val types = arrayOf("Тип 1 (с иконкой)", "Тип 2 (товар)")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Добавить новый элемент")
            .setItems(types) { _, which ->
                itemCounter++

                val newItem = when (which) {
                    0 -> ListItem.ItemType1(
                        id = 100 + itemCounter,
                        title = "Новый элемент $itemCounter",
                        description = "Добавлен через диалог",
                        iconRes = android.R.drawable.ic_input_add
                    )
                    else -> ListItem.ItemType2(
                        id = 200 + itemCounter,
                        name = "Новый товар $itemCounter",
                        price = "${itemCounter * 1000} ₽",
                        rating = 4.0f,
                        imageRes = android.R.drawable.ic_input_get
                    )
                }

                adapter.addItem(newItem)
                binding.recyclerView.smoothScrollToPosition(adapter.itemCount - 1)

                Toast.makeText(
                    requireContext(),
                    "Добавлен новый элемент типа ${which + 1}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showItemInfo(item: ListItem) {
        val message = when (item) {
            is ListItem.Header -> "Заголовок: ${item.title}"
            is ListItem.ItemType1 -> "Элемент: ${item.title}\n${item.description}"
            is ListItem.ItemType2 -> "Товар: ${item.name}\nЦена: ${item.price}\nРейтинг: ${item.rating}"
        }

        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun showContextMenu(item: ListItem) {
        val options = when (item) {
            is ListItem.Header -> arrayOf("Изменить заголовок", "Удалить раздел")
            is ListItem.ItemType1 -> arrayOf("Редактировать", "Удалить", "Дублировать")
            is ListItem.ItemType2 -> arrayOf("Редактировать", "Удалить", "В избранное", "Дублировать")
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Действия")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        showEditDialog(item)
                    }
                    1 -> {
                        val position = adapter.getItems().indexOfFirst { it == item }
                        if (position != -1) {
                            deletedItem = Pair(position, item)
                            adapter.removeItem(position)
                            showUndoSnackbar()
                        }
                    }
                    2 -> {
                        if (item is ListItem.ItemType2) {
                            Toast.makeText(requireContext(), "Добавлено в избранное", Toast.LENGTH_SHORT).show()
                        } else {
                            duplicateItem(item)
                        }
                    }
                    3 -> {
                        duplicateItem(item)
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEditDialog(item: ListItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Редактирование")
            .setMessage("Редактирование элемента: ${getItemTitle(item)}")
            .setPositiveButton("Сохранить") { _, _ ->
                Toast.makeText(requireContext(), "Изменения сохранены", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun getItemTitle(item: ListItem): String {
        return when (item) {
            is ListItem.Header -> item.title
            is ListItem.ItemType1 -> item.title
            is ListItem.ItemType2 -> item.name
        }
    }

    private fun duplicateItem(item: ListItem) {
        itemCounter++

        val duplicatedItem = when (item) {
            is ListItem.ItemType1 -> item.copy(
                id = item.id + 1000,
                title = "${item.title} (копия)"
            )
            is ListItem.ItemType2 -> item.copy(
                id = item.id + 1000,
                name = "${item.name} (копия)"
            )
            else -> return
        }

        adapter.addItem(duplicatedItem)
        binding.recyclerView.smoothScrollToPosition(adapter.itemCount - 1)

        Toast.makeText(requireContext(), "Элемент дублирован", Toast.LENGTH_SHORT).show()
    }

    private fun showUndoSnackbar() {
        val snackbar = Snackbar.make(
            binding.root,
            "Элемент удален",
            Snackbar.LENGTH_LONG
        )
            .setAction("ОТМЕНИТЬ") {
                deletedItem?.let { (position, item) ->
                    val currentItems = adapter.getItems().toMutableList()
                    currentItems.add(position, item)
                    adapter.updateItems(currentItems)
                    Toast.makeText(requireContext(), "Удаление отменено", Toast.LENGTH_SHORT).show()
                }
                deletedItem = null
            }
            .setActionTextColor(Color.YELLOW)

        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                deletedItem = null
            }
        })

        snackbar.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}