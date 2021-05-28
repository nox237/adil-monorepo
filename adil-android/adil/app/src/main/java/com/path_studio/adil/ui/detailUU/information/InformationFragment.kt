package com.path_studio.adil.ui.detailUU.information

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.path_studio.adil.R
import com.path_studio.adil.data.database.entity.Bookmark
import com.path_studio.adil.data.source.remote.response.LegislationResponse
import com.path_studio.adil.databinding.FragmentInformationBinding
import com.path_studio.adil.ui.detailUU.DetailUUActivity
import com.path_studio.adil.ui.main.MainActivity
import com.path_studio.adil.ui.pdfView.PdfViewActivity
import com.path_studio.adil.ui.pdfView.PdfViewerViewModel
import com.path_studio.adil.viewModel.ViewModelFactory

class InformationFragment : Fragment() {

    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding as FragmentInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //set binding
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(activity != null) {
            val factory = ViewModelFactory.getInstance(requireActivity())
            val viewModel = ViewModelProvider(this, factory)[InformationViewModel::class.java]
            val extras_id = DetailUUActivity.EXTRA_LEGISLATION_ID
            val extras = requireActivity().intent.extras?.getString(extras_id)
            var checked = false

            if(extras != null) {
                val legisId =extras

                viewModel.selectedLegislation(legisId.toString())
                viewModel.getLegislationDetail().observe(viewLifecycleOwner,{ data ->
                populateDetail(data)

                val title = "${data.jenisPeraturan} Nomor ${data.nomorPeraturan} Tahun ${data.tahunPeraturan}"
                binding.tvTitleDetail.text = title

                binding.button.setOnClickListener {
                    val intent = Intent(activity as DetailUUActivity, PdfViewActivity::class.java)
                    intent.putExtra(PdfViewActivity.EXTRA_LEGISLATION_ID, legisId)
                    intent.putExtra(PdfViewActivity.EXTRA_TITLE, title)
                    startActivity(intent)
                }

            })
                viewModel.getBookmarkById(extras).observe(requireActivity()) { bookmark ->
                    if(bookmark != null) {
                        checked = bookmark.legislationId == extras
                        isButtonChecked(checked)
                    }
                }
                binding.bookmarkButton.setOnClickListener {
                    checked = !checked
                    createOrDeleteBookmark(checked, viewModel, Bookmark(extras))
                    isButtonChecked(checked)
                }
            }
        }
    }

    private fun createOrDeleteBookmark(checked: Boolean, viewModel: InformationViewModel,
                                       bookmark: Bookmark) {
        if(checked) {
            viewModel.insertBookmark(bookmark)
            Toast.makeText(requireActivity(), "Success Add Bookmark", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.deleteBookmark(bookmark)
            Toast.makeText(requireActivity(), "Success Delete Bookmark", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isButtonChecked(checked: Boolean) {
        if(checked) {
            binding.bookmarkButton.setImageResource(R.drawable.ic_bookmark_checked)
        } else {
            binding.bookmarkButton.setImageResource(R.drawable.ic_bookmark_uncheck)
        }
    }

    private fun populateDetail(data: LegislationResponse?){
        with(binding){
            infoJenis.text = ":" +data?.jenisPeraturan

            if(data?.instansi.isNullOrBlank()) {
                   infoInstansi.text = ":Tidak ada data"
            }else{
                infoInstansi.text = ":" +data?.instansi
            }

            infoJudul.text = ":" + data?.tentang
            infoNomor.text = ":" +data?.nomorPeraturan
            infoTahun.text = ":" +data?.tahunPeraturan.toString()
            infoDitetapkan.text = ":" +data?.tglDitetapkan
            infoDiundangkan.text = ":" +data?.tglDiundangkan

            if(data?.daerahId.isNullOrBlank()){
                infoDaerah.text = ":Tidak ada data"
            } else {
                infoDaerah.text = ":" +data?.daerahId
            }

            for (i in 0 until data?.category?.size!!){
                infoKategori.append(data?.category[i].toString() + " ; ")
            }
        }
    }

}