package com.example.mediachallenger

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediachallenger.databinding.AdapterTrackListBinding


class TrackListAdapter(var dataSet: ArrayList<String>) :
    RecyclerView.Adapter<TrackListAdapter.ViewHolder>() {

    // Callback para notificar a activity quando um usuário for clicado.
    var onItemClick: ((String) -> Unit)? = null

    // Infla o layout para cada item da lista e retorna um novo ViewHolder.
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterTrackListBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ViewHolder(binding)
    }

    // Vincula os dados do usuário ao ViewHolder correspondente.
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]
        viewHolder.bind(currentItem)   // Chama a função bind para vincular os dados.
    }

    // Retorna o número total de itens na lista de usuários.
    override fun getItemCount() = dataSet.size

    // Classe ViewHolder para manter a vinculação da view.
    inner class ViewHolder(val binding: AdapterTrackListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            // Define o listener de clique para cada item da lista.
            itemView.setOnClickListener {
                // Chama o callback onItemClick, passando o objeto UserModel do item clicado.
                onItemClick?.invoke(dataSet[adapterPosition])
            }
        }

        fun bind(music: String) {
            binding.txtTitleMusic.text = music
        }
    }
}
