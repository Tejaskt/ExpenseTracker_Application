package com.tejaskt.expensetracker.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
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


class DashboardFragment : Fragment() {

    //Floating Button
    private lateinit var fabMainBtn: FloatingActionButton
    private lateinit var fabIncomeBtn: FloatingActionButton
    private lateinit var fabExpenseBtn: FloatingActionButton

    //Floating button textview..
    private lateinit var fabIncomeTxt: TextView
    private lateinit var fabExpenseTxt: TextView

    //boolean
    private var isOpen = false

    //Animation
    private lateinit var fadOpen: Animation
    private lateinit var fadeClose: Animation

    //Firebase
    private lateinit var mAuth: FirebaseAuth
    private var mIncomeDatabase: DatabaseReference? = null
    private var mExpenseDatabase: DatabaseReference? = null

    // Dashboard Income and Expense Result...
    private lateinit var totalIncomeResult : TextView
    private lateinit var totalExpenseResult : TextView

    // Recycle view
    private lateinit var mRecyclerIncome : RecyclerView
    private lateinit var mRecyclerExpense : RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val myview = inflater.inflate(R.layout.fragment_dashboard, container, false)

        mAuth = FirebaseAuth.getInstance()
        val mUser = mAuth.currentUser
        val uid = mUser?.uid

        mIncomeDatabase =
            uid?.let { FirebaseDatabase.getInstance().reference.child("IncomeData").child(it) }
        mExpenseDatabase =
            uid?.let { FirebaseDatabase.getInstance().reference.child("ExpenseDatabase").child(it) }

        mIncomeDatabase?.keepSynced(true)
        mExpenseDatabase?.keepSynced(true)

        //Connect floating button to layout
        fabMainBtn = myview.findViewById(R.id.fb_main_plus_btn)
        fabIncomeBtn = myview.findViewById(R.id.income_Ft_btn)
        fabExpenseBtn = myview.findViewById(R.id.expense_Ft_btn)

        //Connect floating text.
        fabIncomeTxt = myview.findViewById(R.id.income_ft_text)
        fabExpenseTxt = myview.findViewById(R.id.expense_ft_text)

        // Total income and expense result set..
        totalIncomeResult = myview.findViewById(R.id.income_set_result)
        totalExpenseResult = myview.findViewById(R.id.expense_set_result)

        // connect Recycler view
        mRecyclerIncome = myview.findViewById(R.id.recyclerIncome)
        mRecyclerExpense = myview.findViewById(R.id.recycleExpense)


        //Animation connect.
        fadOpen = AnimationUtils.loadAnimation(activity, R.anim.fade_open)
        fadeClose = AnimationUtils.loadAnimation(activity, R.anim.fade_close)

        fabMainBtn.setOnClickListener {
            addData()
            isOpen = if (isOpen) {
                fabIncomeBtn.startAnimation(fadeClose)
                fabExpenseBtn.startAnimation(fadeClose)
                fabIncomeBtn.isClickable = false
                fabExpenseBtn.isClickable = false
                fabIncomeTxt.startAnimation(fadeClose)
                fabExpenseTxt.startAnimation(fadeClose)
                fabIncomeTxt.isClickable = false
                fabExpenseTxt.isClickable = false
                false
            } else {
                fabIncomeBtn.startAnimation(fadOpen)
                fabExpenseBtn.startAnimation(fadOpen)
                fabIncomeBtn.isClickable = true
                fabExpenseBtn.isClickable = true
                fabIncomeTxt.startAnimation(fadOpen)
                fabExpenseTxt.startAnimation(fadOpen)
                fabIncomeTxt.isClickable = true
                fabExpenseTxt.isClickable = true
                true
            }
        }

        // Calculate Total Income

        mIncomeDatabase?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                var totalValue = 0

                for (snapshot in datasnapshot.children) {
                    val data = snapshot.getValue(Data::class.java)
                    totalValue+= data!!.amount

                    val stTotalValue: String = totalValue.toString()
                    val fullString = "$stTotalValue.0"
                    totalIncomeResult.text = fullString
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "can't calculate Income",Toast.LENGTH_SHORT).show()
            }
        })

        // Calculate total Expense

        mExpenseDatabase?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                var totalSum = 0

                for (snapshot in datasnapshot.children) {
                    val data = snapshot.getValue(Data::class.java)
                    totalSum+= data!!.amount

                    val stTotalValue: String = totalSum.toString()
                    val fullString2 = "$stTotalValue.0"
                    totalExpenseResult.text =fullString2
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "can't calculate expense",Toast.LENGTH_SHORT).show()
            }
        })

        // Recycler views

        val layoutManagerIncome = LinearLayoutManager(activity , LinearLayoutManager.HORIZONTAL,false)
        layoutManagerIncome.stackFromEnd = true
        layoutManagerIncome.reverseLayout = true
        mRecyclerIncome.setHasFixedSize(true)
        mRecyclerIncome.layoutManager = layoutManagerIncome

        val layoutManagerExpense = LinearLayoutManager(activity , LinearLayoutManager.HORIZONTAL,false)
        layoutManagerExpense.stackFromEnd = true
        layoutManagerExpense.reverseLayout = true
        mRecyclerExpense.setHasFixedSize(true)
        mRecyclerExpense.layoutManager = layoutManagerExpense


        return myview
    }


    //Floating button animation
    private fun ftAnimation() {
        if (isOpen) {
            fabIncomeBtn.startAnimation(fadeClose)
            fabExpenseBtn.startAnimation(fadeClose)
            fabIncomeBtn.isClickable = false
            fabExpenseBtn.isClickable = false
            fabIncomeTxt.startAnimation(fadeClose)
            fabExpenseTxt.startAnimation(fadeClose)
            fabIncomeTxt.isClickable = false
            fabExpenseTxt.isClickable = false
            isOpen = false
        } else {
            fabIncomeBtn.startAnimation(fadOpen)
            fabExpenseBtn.startAnimation(fadOpen)
            fabIncomeBtn.isClickable = true
            fabExpenseBtn.isClickable = true
            fabIncomeTxt.startAnimation(fadOpen)
            fabExpenseTxt.startAnimation(fadOpen)
            fabIncomeTxt.isClickable = true
            fabExpenseTxt.isClickable = true
            isOpen = true
        }
    }

    private fun addData() {
        fabIncomeBtn.setOnClickListener { incomeDataInsert() }
        fabExpenseBtn.setOnClickListener { expenseDataInsert() }
    }

    private fun incomeDataInsert() {
        val myDialog = AlertDialog.Builder(
            requireActivity()
        )
        val inflater = LayoutInflater.from(activity)
        val myview: View = inflater.inflate(R.layout.custom_layout_for_insertdata, null)
        myDialog.setView(myview)
        val dialog = myDialog.create()
        dialog.setCancelable(false)
        val edtAmmount = myview.findViewById<EditText>(R.id.ammount_edt)
        val edtType = myview.findViewById<EditText>(R.id.type_edt)
        val edtNote = myview.findViewById<EditText>(R.id.note_edt)
        val btnSave = myview.findViewById<Button>(R.id.btnSave)
        val btnCansel = myview.findViewById<Button>(R.id.btnCancel)
        btnSave.setOnClickListener(View.OnClickListener {
            val type = edtType.text.toString().trim { it <= ' ' }
            val ammount = edtAmmount.text.toString().trim { it <= ' ' }
            val note = edtNote.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(type)) {
                edtType.error = "Required Field.."
                return@OnClickListener
            }
            if (TextUtils.isEmpty(ammount)) {
                edtAmmount.error = "Required Field.."
                return@OnClickListener
            }
            val ourAmmountInt = ammount.toInt()

            if (TextUtils.isEmpty(note)) {
                edtNote.error = "Required Field.."
                return@OnClickListener
            }
            val id: String? = mIncomeDatabase?.push()?.key
            val mDate = DateFormat.getDateInstance().format(Date())
            val data = Data(ourAmmountInt, type, note, id, mDate)

            id?.let { it1 -> mIncomeDatabase?.child(it1)?.setValue(data)?.addOnSuccessListener {
                Toast.makeText(activity, "Income ADDED", Toast.LENGTH_SHORT).show()
            } }

            ftAnimation()
            dialog.dismiss()
        })
        btnCansel.setOnClickListener {
            ftAnimation()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun expenseDataInsert() {

        val myDialog = AlertDialog.Builder(
            requireActivity()
        )

        val inflater = LayoutInflater.from(activity)
        val myview: View = inflater.inflate(R.layout.custom_layout_for_insertdata, null)

        myDialog.setView(myview)

        val dialog = myDialog.create()
        dialog.setCancelable(false)

        val ammount = myview.findViewById<EditText>(R.id.ammount_edt)
        val type = myview.findViewById<EditText>(R.id.type_edt)
        val note = myview.findViewById<EditText>(R.id.note_edt)
        val btnSave = myview.findViewById<Button>(R.id.btnSave)
        val btnCancel = myview.findViewById<Button>(R.id.btnCancel)

        btnSave.setOnClickListener(View.OnClickListener {

            val tmAmmount = ammount.text.toString().trim { it <= ' ' }
            val tmType = type.text.toString().trim { it <= ' ' }
            val tmNote = note.text.toString().trim { it <= ' ' }

            if (TextUtils.isEmpty(tmAmmount)) {
                ammount.error = "Requires Fields..."
                return@OnClickListener
            }

            val inAmount = tmAmmount.toInt()
            if (TextUtils.isEmpty(tmType)) {
                type.error = "Requires Fields..."
                return@OnClickListener
            }

            if (TextUtils.isEmpty(tmNote)) {
                note.error = "Requires Fields..."
                return@OnClickListener
            }

            val id: String = mExpenseDatabase?.push()?.key.toString()
            val mDate = DateFormat.getDateInstance().format(Date())
            val data = Data(inAmount, tmType, tmNote, id, mDate)

            mExpenseDatabase?.child(id)?.setValue(data)?.addOnSuccessListener {
                Toast.makeText(activity, "Expense added", Toast.LENGTH_SHORT).show()
            }

            ftAnimation()
            dialog.dismiss()
        })

        btnCancel.setOnClickListener {
            ftAnimation()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onStart() {
        super.onStart()

        // Adapter for Income card view

        val iOptions: FirebaseRecyclerOptions<Data> = FirebaseRecyclerOptions.Builder<Data>()
            .setQuery(mIncomeDatabase!!, Data::class.java)
            .setLifecycleOwner(this)
            .build()

       val incomeAdapter = object : FirebaseRecyclerAdapter<Data, IncomeViewHolder>(iOptions) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {

                return IncomeViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.dashboard_income, parent, false)
                )
            }

            override fun onBindViewHolder(holder: IncomeViewHolder, position: Int, model: Data) {
                holder.setIncomeType(model.type)
                holder.setIncomeAmmount(model.amount)
                holder.setIncomeDate(model.date)
            }
        }

        mRecyclerIncome.adapter = incomeAdapter


        // Adapter for Expense Card View

        val eOptions: FirebaseRecyclerOptions<Data> = FirebaseRecyclerOptions.Builder<Data>()
            .setQuery(mExpenseDatabase!!, Data::class.java)
            .setLifecycleOwner(this)
            .build()

        val expenseAdapter = object : FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(eOptions) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {

                return ExpenseViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.dashboard_expense, parent, false)
                )
            }

            override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int, model: Data) {
                holder.setExpenseType(model.type)
                holder.setExpenseAmmount(model.amount)
                holder.setExpenseDate(model.date)
            }
        }

        mRecyclerExpense.adapter = expenseAdapter

    }

    class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun setIncomeType(type : String?){
              val mType : TextView = itemView.findViewById(R.id.typeIncomeDs)
                mType.text = type
            }

            fun setIncomeAmmount(ammount : Int?){
                val mAmmount : TextView = itemView.findViewById(R.id.ammountIncomeDs)
                mAmmount.text = ammount.toString()
            }

            fun setIncomeDate(date : String?){
                val mDate : TextView = itemView.findViewById(R.id.dateIncomeDs)
                mDate.text = date
            }

        }

    class ExpenseViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){

        fun setExpenseType(type : String?){
            val mType : TextView = itemView.findViewById(R.id.typeExpenseDs)
            mType.text = type
        }

        fun setExpenseAmmount(ammount : Int?){
            val mAmmount : TextView = itemView.findViewById(R.id.ammountExpenseDs)
            mAmmount.text = ammount.toString()
        }

        fun setExpenseDate(date : String?){
            val mDate : TextView = itemView.findViewById(R.id.dateExpenseDs)
            mDate.text = date
        }
    }
}