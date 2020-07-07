package pl.arsonproject.koleodistancer.ui

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import pl.arsonproject.koleodistancer.R
import pl.arsonproject.koleodistancer.databinding.ActivityMainBinding
import pl.arsonproject.koleodistancer.viewmodel.MainActivityViewModel

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        setObservables()
        checkPermission()
    }

    override fun onResume() {
        super.onResume()
        setObservables()
        Snackbar.make(
            mainView,
            getString(R.string.snakbar_startlabel),
            Snackbar.LENGTH_LONG
        )
            .setAction("OK", object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    if (p0 is Snackbar)
                        p0.dismiss()
                }
            })
            .show()
    }

    override fun onPause() {
        super.onPause()
        viewModel.errorMessage.removeObservers(this)
        viewModel.listStation.removeObservers(this)
    }

    private fun setObservables() {
        viewModel.listStation.observe(this, Observer {

            val adapter =
                ArrayAdapter(
                    this,
                    android.R.layout.select_dialog_item,
                    it.map { x -> x.name }.sorted()
                )
            firstStationTV.setAdapter(adapter)
            secondStationTV.setAdapter(adapter)
        })

        viewModel.errorMessage.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                Snackbar.make(mainView, it, Snackbar.LENGTH_LONG)
                    .setAction("OK", object : View.OnClickListener {
                        override fun onClick(p0: View?) {
                            if (p0 is Snackbar)
                                p0.dismiss()
                        }
                    }).show()
            }
        })
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission_group.STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.INTERNET) || shouldShowRequestPermissionRationale(Manifest.permission_group.STORAGE)) {
                AlertDialog.Builder(this)
                    .setTitle("Wymagane uprawnienia")
                    .setMessage("Uprawnienia są potrzebne aby obliczyć dystans pomiedzy punktami")
                    .setNegativeButton(
                        "Odmów"
                    ) { dialogInterface, i ->
                        Snackbar.make(
                            mainView,
                            "Uprawnienia są wymagane",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Przyznaj") {
                                ActivityCompat.requestPermissions(
                                    this@MainActivity,
                                    arrayOf(Manifest.permission.INTERNET,Manifest.permission_group.STORAGE),
                                    1001
                                )
                            }
                    }
                    .setPositiveButton(
                        "Przyznaj"
                    ) { dialogInterface, i ->
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.INTERNET,Manifest.permission_group.STORAGE),
                            1001
                        )
                    }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.INTERNET),
                    1001
                )
            }
        }
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.INTERNET
        ) == PackageManager.PERMISSION_GRANTED
    }
}