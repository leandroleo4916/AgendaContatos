package com.example.agendadecontatos.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agendadecontatos.R
import com.example.agendadecontatos.adapter.AdapterContact
import com.example.agendadecontatos.entity.Contacts
import com.example.agendadecontatos.interfaces.ItemClickEdit
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs

class MainActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener, ItemClickEdit,
    View.OnClickListener {

    private lateinit var coordinator: CoordinatorLayout
    private lateinit var contactAdapter: AdapterContact
    private val showTitleToolBar = 0.9f
    private val animationDuration = 200
    private var titleVisible = false
    private var textTitle: TextView? = null
    private var appBarLayout: AppBarLayout? = null
    private val request = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_CONTACTS), request) }
        else { getContacts() }

        instanceView()
        listener()
        startAlphaAnimation(textTitle!!, 0, View.INVISIBLE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == request) getContacts()
    }

    private fun getContacts() {

        val listContacts: ArrayList<Contacts> = arrayListOf()

        val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null)

        if (cursor != null) {
            while (cursor.moveToNext()){

                val id = cursor.getLong(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                val lookupKey = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY))
                val name = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phone = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER))

                listContacts.add(Contacts(id, lookupKey, name, phone))
            }
            cursor.close()
        }
        val list = removeNameDuplicate(listContacts)
        createAdapter(list)
        setTotalContacts(list)
    }

    private fun removeNameDuplicate(listContact: ArrayList<Contacts>): ArrayList<Contacts> {
        return listContact.distinctBy { it.name } as ArrayList<Contacts>
    }

    private fun listener() {
        filter_contact.setOnClickListener(this)
        add_contact.setOnClickListener(this)
        option_contact.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            filter_contact -> showMenuFilter()
            add_contact -> intentContact()
            option_contact -> {}
        }
    }

    private fun intentContact(){
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
        }
        intent.putExtra("finishActivityOnSaveCompleted", true)
        startActivity(intent)
    }

    private fun updateContact(contact: Contacts){
        val cursor: Cursor? = null
        var selectedUri: Uri?

        cursor.apply {
            selectedUri = ContactsContract.Contacts.getLookupUri(contact.id, contact.lookupKey)
        }
        val intent = Intent(Intent.ACTION_EDIT).apply{
            setDataAndType(selectedUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE)
        }
        intent.putExtra("finishActivityOnSaveCompleted", true)
        startActivity(intent)
    }

    private fun showMenuFilter(){
        val menuOption = PopupMenu(this, filter_contact)
        menuOption.menuInflater.inflate(R.menu.menu, menuOption.menu)
        menuOption.setOnMenuItemClickListener { item ->
            when (item!!.itemId) {
                R.id.order_az -> contactAdapter.upDateList()
                R.id.order_za -> contactAdapter.upDateListReverse()
                R.id.recent -> contactAdapter.upDateListByIdReverse()
                R.id.old -> contactAdapter.upDateListById()
            }
            true
        }
        menuOption.show()
    }

    private fun createAdapter(listContacts: ArrayList<Contacts>) {
        val recycler = findViewById<RecyclerView>(R.id.recycler_contacts)
        recycler.layoutManager = LinearLayoutManager(this)
        contactAdapter = AdapterContact(listContacts, this)
        recycler.adapter = contactAdapter
    }

    private fun setTotalContacts(listContacts: ArrayList<Contacts>) {
        val totalContacts = findViewById<TextView>(R.id.total_contacts)
        val size = listContacts.size.toString()
        totalContacts.text = "$size contatos nesta lista."
    }

    private fun instanceView(){
        coordinator = findViewById(R.id.container_contact)
        appBarLayout = findViewById(R.id.appbar)
        textTitle = findViewById(R.id.text_title)
        appBarLayout!!.addOnOffsetChangedListener(this)
    }

    private fun showSnackBar(message: Int) {
        Snackbar.make(coordinator,
            message, Snackbar.LENGTH_LONG)
            .setTextColor(Color.WHITE)
            .setActionTextColor(Color.WHITE)
            .setBackgroundTint(Color.BLACK)
            .setAction("Ok") {}
            .show()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val maxScroll = appBarLayout!!.totalScrollRange
        val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()

        handleToolbarTitleVisibility(percentage)
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= showTitleToolBar) {
            if (!titleVisible) {
                startAlphaAnimation(textTitle!!, animationDuration.toLong(), View.VISIBLE)
                titleVisible = true
            }
        } else {
            if (titleVisible) {
                startAlphaAnimation(textTitle!!, animationDuration.toLong(), View.INVISIBLE)
                titleVisible = false
            }
        }
    }

    private fun startAlphaAnimation(v: View, duration: Long, visibility: Int) {
        val alphaAnimation = if (visibility == View.VISIBLE) AlphaAnimation(0f, 1f)
        else AlphaAnimation(1f, 0f)
        alphaAnimation.duration = duration
        alphaAnimation.fillAfter = true
        v.startAnimation(alphaAnimation)
    }

    override fun clickEdit(contact: Contacts) {
        updateContact(contact)
    }
}