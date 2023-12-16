package com.tejaskt.expensetracker

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
import com.tejaskt.expensetracker.model.Data
import java.text.DateFormat
import java.util.Date


class DashboardFragment : Fragment() {

    //Floating Button
    private lateinit var fab_main_btn: FloatingActionButton
    private lateinit var fab_income_btn: FloatingActionButton
    private lateinit var fab_expense_btn: FloatingActionButton

    //Floating button textview..
    private lateinit var fab_income_txt: TextView
    private lateinit var fab_expense_txt: TextView

    //boolean
    private var isOpen = false

    //Animation
    private lateinit var FadOpen: Animation  //Animation
    private lateinit var FadeClose: Animation

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

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            // nothing here
//        }
//    }

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
        fab_main_btn = myview.findViewById(R.id.fb_main_plus_btn)
        fab_income_btn = myview.findViewById(R.id.income_Ft_btn)
        fab_expense_btn = myview.findViewById(R.id.expense_Ft_btn)

        //Connect floating text.
        fab_income_txt = myview.findViewById(R.id.income_ft_text)
        fab_expense_txt = myview.findViewById(R.id.expense_ft_text)

        // Total income and expense result set..
        totalIncomeResult = myview.findViewById(R.id.income_set_result)
        totalExpenseResult = myview.findViewById(R.id.expense_set_result)

        // connect Recycler view
        mRecyclerIncome = myview.findViewById(R.id.recyclerIncome)
        mRecyclerExpense = myview.findViewById(R.id.recycleExpense)


        //Animation connect.
        FadOpen = AnimationUtils.loadAnimation(activity, R.anim.fade_open)
        FadeClose = AnimationUtils.loadAnimation(activity, R.anim.fade_close)

        fab_main_btn.setOnClickListener {
            addData()
            isOpen = if (isOpen) {
                fab_income_btn.startAnimation(FadeClose)
                fab_expense_btn.startAnimation(FadeClose)
                fab_income_btn.isClickable = false
                fab_expense_btn.isClickable = false
                fab_income_txt.startAnimation(FadeClose)
                fab_expense_txt.startAnimation(FadeClose)
                fab_income_txt.isClickable = false
                fab_expense_txt.isClickable = false
                false
            } else {
                fab_income_btn.startAnimation(FadOpen)
                fab_expense_btn.startAnimation(FadOpen)
                fab_income_btn.isClickable = true
                fab_expense_btn.isClickable = true
                fab_income_txt.startAnimation(FadOpen)
                fab_expense_txt.startAnimation(FadOpen)
                fab_income_txt.isClickable = true
                fab_expense_txt.isClickable = true
                true
            }
        }

        // Calculate Total Income

        mIncomeDatabase?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                var totalatvalue = 0

                for (snapshot in datasnapshot.children) {
                    val data = snapshot.getValue(Data::class.java)
                    totalatvalue+= data!!.amount

                    val stTotalValue: String = totalatvalue.toString()
                    totalIncomeResult.text = "$stTotalValue.00"
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "can't calculate Income",Toast.LENGTH_SHORT).show()
            }
        })

        // Calculate total Expense

        mExpenseDatabase?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                var totalsum = 0

                for (snapshot in datasnapshot.children) {
                    val data = snapshot.getValue(Data::class.java)
                    totalsum+= data!!.amount

                    val stTotalValue: String = totalsum.toString()
                    totalExpenseResult.text = "$stTotalValue.00"
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
            fab_income_btn.startAnimation(FadeClose)
            fab_expense_btn.startAnimation(FadeClose)
            fab_income_btn.isClickable = false
            fab_expense_btn.isClickable = false
            fab_income_txt.startAnimation(FadeClose)
            fab_expense_txt.startAnimation(FadeClose)
            fab_income_txt.isClickable = false
            fab_expense_txt.isClickable = false
            isOpen = false
        } else {
            fab_income_btn.startAnimation(FadOpen)
            fab_expense_btn.startAnimation(FadOpen)
            fab_income_btn.isClickable = true
            fab_expense_btn.isClickable = true
            fab_income_txt.startAnimation(FadOpen)
            fab_expense_txt.startAnimation(FadOpen)
            fab_income_txt.isClickable = true
            fab_expense_txt.isClickable = true
            isOpen = true
        }
    }

    private fun addData() {
        fab_income_btn.setOnClickListener { incomeDataInsert() }
        fab_expense_btn.setOnClickListener { expenseDataInsert() }
    }

    private fun incomeDataInsert() {
        val mydialog = AlertDialog.Builder(
            requireActivity()
        )
        val inflater = LayoutInflater.from(activity)
        val myview: View = inflater.inflate(R.layout.custom_layout_for_insertdata, null)
        mydialog.setView(myview)
        val dialog = mydialog.create()
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
            val ourammontint = ammount.toInt()

            if (TextUtils.isEmpty(note)) {
                edtNote.error = "Required Field.."
                return@OnClickListener
            }
            val id: String? = mIncomeDatabase?.push()?.key
            val mDate = DateFormat.getDateInstance().format(Date())
            val data = Data(ourammontint, type, note, id, mDate)

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

        val mydialog = AlertDialog.Builder(
            requireActivity()
        )

        val inflater = LayoutInflater.from(activity)
        val myview: View = inflater.inflate(R.layout.custom_layout_for_insertdata, null)

        mydialog.setView(myview)

        val dialog = mydialog.create()
        dialog.setCancelable(false)

        val ammount = myview.findViewById<EditText>(R.id.ammount_edt)
        val type = myview.findViewById<EditText>(R.id.type_edt)
        val note = myview.findViewById<EditText>(R.id.note_edt)
        val btnSave = myview.findViewById<Button>(R.id.btnSave)
        val btnCancel = myview.findViewById<Button>(R.id.btnCancel)

        btnSave.setOnClickListener(View.OnClickListener {

            val tmAmmount = ammount.text.toString().trim { it <= ' ' }
            val tmtype = type.text.toString().trim { it <= ' ' }
            val tmnote = note.text.toString().trim { it <= ' ' }

            if (TextUtils.isEmpty(tmAmmount)) {
                ammount.error = "Requires Fields..."
                return@OnClickListener
            }

            val inamount = tmAmmount.toInt()
            if (TextUtils.isEmpty(tmtype)) {
                type.error = "Requires Fields..."
                return@OnClickListener
            }

            if (TextUtils.isEmpty(tmnote)) {
                note.error = "Requires Fields..."
                return@OnClickListener
            }

            val id: String = mExpenseDatabase?.push()?.key.toString()
            val mDate = DateFormat.getDateInstance().format(Date())
            val data = Data(inamount, tmtype, tmnote, id, mDate)

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

        val Ioptions: FirebaseRecyclerOptions<Data> = FirebaseRecyclerOptions.Builder<Data>()
            .setQuery(mIncomeDatabase!!, Data::class.java)
            .setLifecycleOwner(this)
            .build()

       val incomeAdapter = object : FirebaseRecyclerAdapter<Data, IncomeViewHolder>(Ioptions) {

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

        val Eoptions: FirebaseRecyclerOptions<Data> = FirebaseRecyclerOptions.Builder<Data>()
            .setQuery(mExpenseDatabase!!, Data::class.java)
            .setLifecycleOwner(this)
            .build()

        val expenseAdapter = object : FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(Eoptions) {

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
                val mammount : TextView = itemView.findViewById(R.id.ammountIncomeDs)
                mammount.text = ammount.toString()
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