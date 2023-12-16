package com.tejaskt.expensetracker

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener  {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var frameLayout: FrameLayout

    //Fragment
    private lateinit var dashBoardFragment: DashboardFragment
    private lateinit var incomeFragment: IncomeFragment
    private lateinit var expenseFragment: ExpenseFragment

    // Firebase Auth
    private lateinit var mAuth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        toolbar.title = "Expense Tracker"
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()

        bottomNavigationView = findViewById(R.id.bottomNavigationbar)
        frameLayout = findViewById(R.id.main_frame)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.naView)
        navigationView.setNavigationItemSelectedListener(this)

        dashBoardFragment = DashboardFragment()
        incomeFragment = IncomeFragment()
        expenseFragment = ExpenseFragment()

        setFragment(dashBoardFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashboard -> {
                    setFragment(dashBoardFragment)
                    true
                }

                R.id.income -> {
                    setFragment(incomeFragment)
                    true
                }

                R.id.expense -> {
                    setFragment(expenseFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, fragment)
        fragmentTransaction.commit()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    private fun displaySelectedListner(itemId: Int) {
        var fragment: Fragment? = null
        when (itemId) {
            R.id.dashboard -> {
                fragment = DashboardFragment()
                val item: MenuItem = bottomNavigationView.menu.findItem(itemId)
                item.isChecked = true
            }

            R.id.income -> {
                fragment = IncomeFragment()
                val item: MenuItem = bottomNavigationView.menu.findItem(itemId)
                item.isChecked = true
            }

            R.id.expense -> {
                fragment = ExpenseFragment()
                val item: MenuItem = bottomNavigationView.menu.findItem(itemId)
                item.isChecked = true
            }

            R.id.logout -> {
                mAuth.signOut()
                val intent = Intent(this, MainActivity::class.java )
                startActivity(intent)
                finish()
            }
        }
        if (fragment != null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.main_frame, fragment)
            ft.commit()
        }
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        displaySelectedListner(item.itemId)
        return true
    }
}