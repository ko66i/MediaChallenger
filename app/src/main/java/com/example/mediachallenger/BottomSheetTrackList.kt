package com.example.mediachallenger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediachallenger.databinding.TrackListBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetTrackList : BottomSheetDialogFragment() {

    private var _binding: TrackListBottomSheetBinding? = null
    private val binding get() = _binding!!

    // Referências para a RecyclerView e o adaptador.  Declaradas aqui para melhor organização.
    lateinit var myRecycler: RecyclerView
    lateinit var adapterList: TrackListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TrackListBottomSheetBinding.inflate(inflater, container, false)

        // Configura a RecyclerView.
        myRecycler = binding.recyclerTrack
        val musicList = arrayListOf("Música da lhama", "Música do esqueleto")
        adapterList = TrackListAdapter(musicList)

        myRecycler.adapter = adapterList
        myRecycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        myRecycler.setHasFixedSize(true) // Otimização de performance.

        // Define o listener de clique para os itens da RecyclerView.
        adapterList.onItemClick = { music ->
            SelectedMusicSingleton.setSelectedMusic(music)
            dismiss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}
