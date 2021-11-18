package com.example.agendadecontatos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agendadecontatos.R
import com.example.agendadecontatos.activity.MainActivity
import com.example.agendadecontatos.entity.Contacts
import kotlinx.android.synthetic.main.recycler_contatos.view.*

class AdapterContact(listContact: ArrayList<Contacts>, private val listener: MainActivity):
    RecyclerView.Adapter<AdapterContact.ViewHolderContact>() {

    private var listName: ArrayList<Contacts> = listContact

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderContact {

        val item = LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_contatos, parent, false)

        return ViewHolderContact(item)
    }

    override fun onBindViewHolder(holderContact: ViewHolderContact, position: Int) {

        holderContact.bind(listName[position])
    }

    override fun getItemCount(): Int {
        return listName.count()
    }

    inner class ViewHolderContact(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener{

        init { itemView.edit_contact.setOnClickListener(this) }

        fun bind(contact: Contacts){
            val textName = itemView.findViewById<TextView>(R.id.text_name)
            val textPhone = itemView.findViewById<TextView>(R.id.text_phone)
            val textLetter = itemView.findViewById<TextView>(R.id.text_first_name)

            with(contact){
                textName.text = name
                textPhone.text = phone
                textLetter.text = name.first().toString()
            }
        }

        override fun onClick(view: View?) {
            val position = bindingAdapterPosition
            when(view){
                itemView.edit_contact -> listener.clickEdit(listName[position])
            }
        }
    }
    fun upDateListById(){
        listName.sortWith(compareBy(Contacts::id))
        notifyDataSetChanged()
    }

    fun upDateListByIdReverse(){
        listName.sortWith(compareBy(Contacts::id))
        listName.reverse()
        notifyDataSetChanged()
    }

    fun upDateList(){
        listName.sortWith(compareBy(Contacts::name))
        notifyDataSetChanged()
    }

    fun upDateListReverse(){
        listName.sortWith(compareBy(Contacts::name))
        listName.reverse()
        notifyDataSetChanged()
    }
}