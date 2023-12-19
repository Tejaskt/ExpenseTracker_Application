package com.tejaskt.expensetracker.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tejaskt.expensetracker.R
import com.tejaskt.expensetracker.model.Data
import java.text.DateFormat
import java.util.Date


class IncomeFragment : Fragment() {

    //Firebase DB
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mIncomeDatabase: DatabaseReference

    //RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FirebaseRecyclerAdapter<*, *>

    // Text view
    private lateinit var incomeTotalSum:TextView

    // Update Edit Text
    private lateinit var edtAmmount:EditText
    private lateinit var edtType:EditText
    private lateinit var edtNote:EditText

    // button for update and delete

    private lateinit var btnUpdate:Button
    private lateinit var btnDelete:Button

    // Data item value
    private  var  type : String? = null
    private  var note : String? = null
    private var amount: Int? = null

    private var postKey : String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val myview = inflater.inflate(R.layout.fragment_income, container, false)

        mAuth = FirebaseAuth.getInstance()

        val mUser = mAuth.currentUser
        val uid = mUser!!.uid

        mIncomeDatabase = FirebaseDatabase.getInstance().reference.child("IncomeData").child(uid)

        incomeTotalSum = myview.findViewById(R.id.income_txt_result)
        recyclerView = myview.findViewById(R.id.recycler_id_income)

        val layoutManager = LinearLayoutManager(activity)

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager

        // Read from the database
        mIncomeDatabase.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(datasnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                var totalatvalue = 0

                for (snapshot in datasnapshot.children) {
                    val data = snapshot.getValue(Data::class.java)
                    totalatvalue+= data!!.amount

                    val stTotalValue: String = totalatvalue.toString()
                    incomeTotalSum.text = "$stTotalValue.00"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to read value.", Toast.LENGTH_SHORT).show()
            }

        })

        return myview

    }

    override fun onStart() {
        super.onStart()

        val options: FirebaseRecyclerOptions<Data> = FirebaseRecyclerOptions.Builder<Data>()
            .setQuery(mIncomeDatabase, Data::class.java)
            .setLifecycleOwner(this)   // get solution from stack overflow the data is not showing cause of this line of code
            .build()

        adapter = object : FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

                return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.income_recycler_data, parent, false))
            }

            override fun onBindViewHolder(
                holder: MyViewHolder,
                position: Int,
                model: Data
            ) {
                holder.setAmmount(model.amount)
                holder.setType(model.type)
                holder.setNote(model.note)
                holder.setDate(model.date)

                holder.mView.setOnClickListener {

                    postKey = getRef(position).key

                    type = model.type
                    note = model.note
                    amount = model.amount

                    updateDataItem()
                }
            }
        }
        recyclerView.adapter = adapter
    }

    internal class MyViewHolder(var mView: View) : RecyclerView.ViewHolder(mView) {

        fun setType(type: String?) {
            val mType = mView.findViewById<TextView>(R.id.type_txt_income)
            mType.text = type
        }

        fun setNote(note: String?) {
            val mNote = mView.findViewById<TextView>(R.id.note_txt_income)
            mNote.text = note
        }

        fun setDate(date: String?) {
            val mDate = mView.findViewById<TextView>(R.id.date_txt_income)
            mDate.text = date
        }

        fun setAmmount(ammount: Int) {
            val mAmmount = mView.findViewById<TextView>(R.id.ammount_txt_income)
            val stammount = ammount.toString()
            mAmmount.text = stammount
        }
    }

    fun updateDataItem(){
        val myDialog : AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater : LayoutInflater = LayoutInflater.from(activity)
        val myView : View = inflater.inflate(R.layout.update_data_item,null)
        myDialog.setView(myView)

        edtAmmount=myView.findViewById(R.id.ammount_edt)
        edtType=myView.findViewById(R.id.type_edt)
        edtNote=myView.findViewById(R.id.note_edt)

        // set data to edit text
        edtType.setText(type)
        type?.let { edtType.setSelection(it.length) }

        edtNote.setText(note)
        note?.let { edtNote.setSelection(it.length) }

        edtAmmount.setText(amount.toString())
        edtAmmount.setSelection(amount.toString().length)

        btnUpdate= myView.findViewById(R.id.btnUpdate)
        btnDelete = myView.findViewById(R.id.btnDelete)

        val dialog :AlertDialog = myDialog.create()


        btnUpdate.setOnClickListener {
            type = edtType.text.toString().trim()
            note = edtNote.text.toString().trim()

            val sumAmmount : String = edtAmmount.text.toString().trim()
            val myAmmount:Int = sumAmmount.toInt()

            val mDate : String = DateFormat.getDateInstance().format(Date())
            val data  = Data(myAmmount, type, note, postKey, mDate)

            postKey?.let { it1 -> mIncomeDatabase.child(it1).setValue(data) }
            dialog.dismiss()

            Toast.makeText(requireContext(),"Income Updated", Toast.LENGTH_SHORT ).show()

        }

        btnDelete.setOnClickListener {
            postKey?.let { it1 -> mIncomeDatabase.child(it1).removeValue() }
            dialog.dismiss()
            Toast.makeText(requireContext(),"Income Deleted", Toast.LENGTH_SHORT ).show()
        }

        dialog.show()
    }
}


