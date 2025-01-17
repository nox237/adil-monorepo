package com.path_studio.adil.ui.main.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.path_studio.adil.databinding.FragmentBookmarkBinding
import com.path_studio.adil.viewModel.ViewModelFactory

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding as FragmentBookmarkBinding
    private val bookmarkAdapter = BookmarkAdapter(this)
    private val factory by lazy { ViewModelFactory.getInstance(requireActivity()) }
    private val viewModel by lazy { ViewModelProvider(this, factory)[BookmarkViewModel::class.java] }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //set binding
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {
            Transformations.switchMap(
                viewModel.allBookmarks
            ) { bookmarks ->
                viewModel.getBookmarkedLegislation(bookmarks)
            }.observe(requireActivity()) {
                if(it.size != 0){
                    bookmarkAdapter.setBookmark(it)
                    bookmarkAdapter.notifyDataSetChanged()
                }else{
                   notFound(true)
                }

            }
            with(binding.rvListBookmark) {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = bookmarkAdapter
            }
        }
    }

    private fun notFound(flag: Boolean) {
        binding.imgNotFound.isVisible = flag
        binding.tvNotFound.isVisible = flag
        binding.rvListBookmark.isVisible = !flag
    }

}