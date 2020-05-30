package com.example.rxandroidsample.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.rxandroidsample.DataSource
import com.example.rxandroidsample.R
import com.example.rxandroidsample.model.Task
import com.google.android.material.navigation.NavigationView
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscription
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val TAG = "TAG"
    val disposable = CompositeDisposable()
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        initNavigationView()

//        createFlowable()
//        createSingleObservable()
//        justObservableTest()
//        rangeRepeatObservableTest()
//        intervalObservableTest()
//        timerObservableTest()
    }

    private fun initNavigationView() {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(navigationView, navController)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_flatMap -> {
                val navOptions = NavOptions.Builder().setPopUpTo(R.id.main, true).build()
                Navigation.findNavController(this, R.id.nav_host_fragment)
                    .navigate(R.id.flatScreen, null, navOptions)
            }
            R.id.nav_switchMap -> {
                if (isValidDestination(R.id.nav_switchMap)) {
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.switchScreen)
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
            R.id.nav_filter -> {
                if (isValidDestination(R.id.nav_filter)) {
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.filterScreen)
                }
            }
            R.id.nav_observable->{
                if(isValidDestination(R.id.nav_observable)){
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.observableScreen)
                }
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



    private fun timerObservableTest() {
        //The Timer operator creates an Observable that emits one particular item after a span of time that you specify.
        Observable
            .timer(3, TimeUnit.SECONDS)
            .repeat(2)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Long> {
                var time: Long = 0 // variable for demonstating how much time has passed

                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: called")
                    time = System.currentTimeMillis() / 1000
                }

                override fun onNext(i: Long) {
                    Log.d(
                        TAG,
                        "onNext: " + ((System.currentTimeMillis() / 1000) - time) + " seconds have elapsed."
                    )
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: done...")
                }
            })
    }

    private fun intervalObservableTest() {
        // The Interval operator returns an Observable that emits an infinite sequence of ascending integers,
        // with a constant interval of time of your choosing between emissions.
        Observable
            .interval(5, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .takeWhile { aLong -> aLong <= 5 }
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: called")
                }

                override fun onNext(i: Long) {
                    Log.d(TAG, "onNext: $i")
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: done...")
                }
            })
    }

    private fun rangeRepeatObservableTest() {
        Observable.range(0, 3)
            .repeat(2) //tow time repeat 0 to 3
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: called")
                }

                override fun onNext(i: Int) {
                    Log.d(TAG, "onNext: $i")
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: done...")
                }
            })

    }

    private fun justObservableTest() {
        // just can get list with at most 10 member.
        Observable.just("first", "second", "third")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: called")
                }

                override fun onNext(s: String) {
                    Log.d(TAG, "onNext: $s")
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: done...")
                }
            })
    }

    private fun createSingleObservable() {

        val singleTaskObservable = Observable.create(
            ObservableOnSubscribe<Task> { emitter ->
                // Inside the subscribe method iterate through the list of tasks and call onNext(task)
                //list can have at most ten member
                 for (task in DataSource().getTaskList()) {
                     if (!emitter.isDisposed) {
                         emitter.onNext(task)
                     }
                 }

                /*if (!emitter.isDisposed) {
                    emitter.onNext(task)
                    emitter.onComplete()
                }*/
            }
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        singleTaskObservable.subscribe(object : Observer<Task> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: Task) {
                Log.d(TAG, "onNext: single task: " + t.description)
            }

            override fun onError(e: Throwable) {
            }
        })
    }

    private fun createFlowable() {
        val flowable = Flowable.range(0, 100)
            .onBackpressureBuffer()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : FlowableSubscriber<Int?> {

                override fun onComplete() {
                }

                override fun onNext(t: Int?) {
                    Log.d(TAG, "onNext: $t")
                }

                override fun onError(t: Throwable?) {
                    Log.e(TAG, "onError: ", t)
                }

                override fun onSubscribe(s: Subscription) {
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }


}
