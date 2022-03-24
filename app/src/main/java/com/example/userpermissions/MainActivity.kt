package com.example.userpermissions

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.userpermissions.databinding.ActivityMainBinding
import com.example.userpermissions.volley_communication.CommunicationFunction

class MainActivity : BaseActivity() {

    private val comFun = CommunicationFunction()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        appAlertDialog(binding.root)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_settingsFragment)
                return true}
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * A dialog that inform the user that the application may use his sensitive data.
     * <p>
     * If the user:
     * <p>
     * agree: the application starts and the connection to the server is verified.
     * <p>
     * disagree: the app is terminated.
     *
     * @param view Need a View class for show the dialog to user in activity.
     */
    private fun appAlertDialog(view: View) {
        val builder = AlertDialog.Builder(view.context)

        builder.setTitle(R.string.dialog_title)
        builder.setMessage(R.string.dialog_message)

        builder.setPositiveButton(
            R.string.dialog_yes) { _, _ ->
        }

        builder.setNegativeButton(
            R.string.dialog_no) { _, _ ->
            // terminate app
            finish()
        }

        builder.show()
    }

    override fun onBackPressed() {

        val navigationController = Navigation.findNavController(this, R.id.nav_host_fragment)
        when (navigationController.currentDestination?.id) {
            R.id.PermissionExampleFragment -> {
                Toast.makeText(this,R.string.back_to_theory,Toast.LENGTH_LONG).show()
            }
            R.id.permissionOfflineExampleFragment ->{
                Toast.makeText(this,R.string.back_to_theory,Toast.LENGTH_LONG).show()
            }
            R.id.PermissionTheoryFragment -> {
                comFun.deleteUserInServer(this)
                navigationController.navigate(R.id.permissionFragment)
            }
            R.id.permissionFragment -> {
                navigationController.navigate(R.id.mainMenuFragment)
            }
            R.id.mainMenuFragment -> {
                finish()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        Log.d("test","On Destroy Main Activity")
        //comFun.deleteUserInServer(this)
        super.onDestroy()
    }
}