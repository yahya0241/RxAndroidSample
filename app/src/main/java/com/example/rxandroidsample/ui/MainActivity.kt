package com.example.rxandroidsample.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.rxandroidsample.R
import com.google.android.material.navigation.NavigationView
import io.reactivex.disposables.CompositeDisposable


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val disposable = CompositeDisposable()
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        initNavigationView()
    }

    private fun initNavigationView() {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(navigationView, navController)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_filter -> {
                val navOptions = NavOptions.Builder().setPopUpTo(R.id.main, true).build()
                Navigation.findNavController(this, R.id.nav_host_fragment)
                    .navigate(R.id.filterScreen, null, navOptions)
            }
            R.id.nav_observable -> {
                if (isValidDestination(R.id.nav_observable)) {
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.observableScreen)
                }
            }

            R.id.nav_search -> {
                if (isValidDestination(R.id.nav_search)) {
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.searchScreen)
                }
            }
            R.id.nav_livedata -> {
                if (isValidDestination(R.id.nav_livedata)) {
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.liveDataScreen)
                }
            }
            R.id.nav_buffers -> {
                if (isValidDestination(R.id.nav_buffers)) {
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.buffersScreen)
                }
            }


            R.id.nav_flatMap -> {
                if (isValidDestination(R.id.nav_flatMap)) {
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.flatScreen)
                }
            }
            R.id.nav_switchMap -> {
                if (isValidDestination(R.id.nav_switchMap)) {
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.switchScreen)
                }
            }
            R.id.nav_concatMap -> {
                if (isValidDestination(R.id.nav_concatMap)) {
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.concatMapScreen)
                }
            }
            R.id.nav_continue -> {
                Toast.makeText(this, "New operator will be add :))", Toast.LENGTH_SHORT).show()

            }
        }

        menuItem.isChecked = true
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                return if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                } else {
                    false
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isValidDestination(destination: Int): Boolean {
        return destination != Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        ).currentDestination!!.id
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.nav_host_fragment),
            drawerLayout
        )
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

}
