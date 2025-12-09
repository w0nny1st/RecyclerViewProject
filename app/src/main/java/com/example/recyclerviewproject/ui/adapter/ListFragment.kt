package com.example.recyclerviewproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recyclerviewproject.databinding.FragmentListBinding
import com.example.recyclerviewproject.model.ListItem
import com.example.recyclerviewproject.ui.adapter.MultiTypeAdapter

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MultiTypeAdapter
    private var itemCounter = 0

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
    }

    private fun setupRecyclerView() {
        val items = generateItems().toMutableList()

        adapter = MultiTypeAdapter(items) { item ->
            when (item) {
                is ListItem.Header -> {
                    Toast.makeText(requireContext(), "Заголовок: ${item.title}", Toast.LENGTH_SHORT).show()
                }
                is ListItem.ItemType1 -> {
                    Toast.makeText(requireContext(), "Нажали на: ${item.title}", Toast.LENGTH_SHORT).show()
                }
                is ListItem.ItemType2 -> {
                    Toast.makeText(requireContext(), "Товар: ${item.name}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            addNewItem()
        }
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

    private fun addNewItem() {
        itemCounter++

        val newItem = if (itemCounter % 2 == 0) {
            ListItem.ItemType1(
                id = 100 + itemCounter,
                title = "Новый элемент $itemCounter",
                description = "Добавлен через FAB",
                iconRes = android.R.drawable.ic_input_add
            )
        } else {
            ListItem.ItemType2(
                id = 200 + itemCounter,
                name = "Новый товар $itemCounter",
                price = "${itemCounter * 1000} ₽",
                rating = 4.0f,
                imageRes = android.R.drawable.ic_input_get
            )
        }

        adapter.addItem(newItem)

        binding.recyclerView.smoothScrollToPosition(adapter.itemCount - 1)

        Toast.makeText(requireContext(), "Добавлен новый элемент", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}