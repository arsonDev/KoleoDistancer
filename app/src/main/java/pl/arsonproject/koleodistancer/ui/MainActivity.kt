package pl.arsonproject.koleodistancer.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
}