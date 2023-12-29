package com.example.expensetrackerr

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.get
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.expensetrackerr.database.TransactionObj
import com.example.expensetrackerr.databinding.ActivityMainBinding
import com.example.expensetrackerr.ui.home.HomeFragmentDirections
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val bottomAppBar : BottomAppBar = binding.bottomAppBar

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

        navView.background = null
        navView.menu[1].isEnabled = false

        val btnAddTransaction : FloatingActionButton = binding.fabAdd

        btnAddTransaction.setOnClickListener{

            if (navController.currentDestination!!.id == R.id.navigation_home ){
                val action = HomeFragmentDirections.navigateHomeToAddTransaction(transactionObj = TransactionObj())
                navController.navigate(action)
            }

        }
        navController.addOnDestinationChangedListener{ _, nd: NavDestination, _ ->
            when (nd.id) {
                R.id.addTransactionFragment, R.id.categoryFragment, R.id.navigation_receipts -> {

                    bottomAppBar.visibility = View.GONE
                    btnAddTransaction.visibility = View.GONE
                }
                else -> {
                    bottomAppBar.visibility = View.VISIBLE
                    btnAddTransaction.visibility = View.VISIBLE
                }
            }

        }


    }

    fun hideBottomAppBar(){

        val bottomAppBar : BottomAppBar = binding.bottomAppBar

        val btnAddTransaction : FloatingActionButton = binding.fabAdd

        val layoutParams = bottomAppBar.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior as BottomAppBar.Behavior

        behavior.slideDown(bottomAppBar)
        btnAddTransaction.hide()
    }

    fun showBottomAppBar(){

        val bottomAppBar : BottomAppBar = binding.bottomAppBar

        val btnAddTransaction : FloatingActionButton = binding.fabAdd

        val layoutParams = bottomAppBar.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior as BottomAppBar.Behavior

        behavior.slideUp(bottomAppBar)
        btnAddTransaction.show()
    }
}