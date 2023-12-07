package com.mars.marsandroid.ui.slideshow

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.mars.marsandroid.FaceList
import com.mars.marsandroid.RecycleAdapter
import com.mars.marsandroid.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val settings = requireContext().getSharedPreferences("storage", Context.MODE_PRIVATE)
        val set = Gson().fromJson(settings.getString("last", ""), FaceList::class.java)

        binding.list.layoutManager = LinearLayoutManager(activity)
        binding.list.adapter = RecycleAdapter(set.list)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}